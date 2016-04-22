package com.byteflair.resthooks.subscriptions;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 21/04/16.
 * <p>
 * The AmqpService is designed to create queues per event type attached to a unique entry point topic exchange, the firehose.
 * Events enter the system through the firehose and are routed to the corresponding event queue.
 * Each subscription creates its own queue that is bridged to the corresponding event queue through a fanout exchange.
 * Then, we attach a listener to the subscription queue. When the system shuts down, the subscription queues disappear
 * as these are non-durable. The rest of the pipes need to be cleaned manually y needed.
 */
@Slf4j
public class AmqpService {
    @Value("${resthooks.namespace:resthooks}")
    private String namespace;
    @Value("${resthooks.exchange:firehose}")
    private String exchange;

    private AmqpAdmin amqpAdmin;
    private ConnectionFactory connectionFactory;
    private AmqpTemplate amqpTemplate;

    private Map<String, Exchange> exchangeMap = new HashMap<>();
    private List<Binding> bindings = new ArrayList<>();
    private List<MessageListenerContainer> listeners = new ArrayList<>();
    private Map<String, String> externalQueueNames = new HashMap<>();
    private List<String> internalQueueNames = new ArrayList<>();

    @Autowired
    public AmqpService(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory, AmqpTemplate amqpTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.connectionFactory = connectionFactory;
        this.amqpTemplate = amqpTemplate;
    }

    public void subscribe(String eventType, MessageListener consumer) {
        Assert.notNull(eventType);
        Assert.notNull(eventType);
        Assert.isTrue(!eventType.isEmpty());

        Exchange exchange = this.getExchangeByNamespacedName(eventType);

        if (exchange == null) {
            LOGGER.warn("Unable to find a fanout exchange for the specified event type: {}", eventType);
            LOGGER.info("Lazily creating channel to handle events of type: {}", eventType);
            this.createNamespacedChannel(eventType);
        }
        this.attachListenerToNamespacedQueue(eventType, consumer);
    }

    public Exchange getExchangeByNamespacedName(String name) {
        return exchangeMap.get(namespace.concat(".").concat(name));
    }

    private void createNamespacedChannel(String key) {
        Assert.notNull(getExchangeByNamespacedName(exchange));
        createNamespacedChannel(exchange, key);
    }

    /**
     * Creates an unamed queue and then attaches a listener to that queue to start processing messages
     *
     * @param key      name of the exchange
     * @param listener a MessageListener to process messages
     */
    private void attachListenerToNamespacedQueue(String key, MessageListener listener) {
        String namespacedExchange = namespace.concat(".").concat(key);
        String actualQueueName = registerQueue("", true, true, true);
        /**
         * las colas son sin nombre, amqpAdmin lo genera.
         * Por lo tanto no tengo que proveer un nombre en un namespace
         */
        registerBinding(actualQueueName, namespacedExchange, "");
        registerListener(actualQueueName, listener);
    }

    private void createNamespacedChannel(String entry, String routingKey) {
        Assert.notNull(entry);
        Assert.notNull(routingKey);
        Assert.hasText(entry);
        Assert.hasText(routingKey);

        //attach an event queue to the
        registerNamespacedQueue(routingKey);
        registerNamespacedBinding(routingKey, entry, routingKey);
        registerNamespacedExchange(routingKey, FanoutExchange.class);
        declareNamespacedBridge(routingKey);
    }

    private String registerQueue(String name, Boolean durable, Boolean exclusive, Boolean autoDelete) {
        Boolean namedQueue = name != null && !name.isEmpty();
        Queue queue = new Queue(name, durable, exclusive, autoDelete);
        String actualName = amqpAdmin.declareQueue(queue);
        if (namedQueue) {
            externalQueueNames.put(name, actualName);
        } else if (!autoDelete) {
            //si se borra sola cuando me desconecto no necesito borrarla y no la registro
            internalQueueNames.add(actualName);
        }
        return actualName;
    }

    private void registerBinding(String queueName, String exchange, String routingKey) {
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, exchange, routingKey, null);
        amqpAdmin.declareBinding(binding);
        bindings.add(binding);
    }

    /**
     * Attaches a listener to a queue to start procssing messages
     *
     * @param queueName
     * @param listener
     */
    private void registerListener(String queueName, MessageListener listener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listener);
        container.start();
        listeners.add(container);
    }

    /**
     * Creates a non exclusive queue that survives broker restarts and
     *
     * @param name
     */
    private void registerNamespacedQueue(String name) {
        registerNamespacedQueue(name, true, false, false);
    }

    /**
     * Binds a queue to an exchange with the specified routing
     *
     * @param queueName
     * @param exchange
     * @param routingKey
     */
    private void registerNamespacedBinding(String queueName, String exchange, String routingKey) {
        registerBinding(namespace.concat(".").concat(queueName), namespace.concat(".").concat(exchange), routingKey);
    }

    /**
     * Creates a exchange of the desired type with the given name
     *
     * @param name
     * @param exchangeType
     */
    private void registerNamespacedExchange(String name, Class<? extends Exchange> exchangeType) {
        String namespacedName = namespace.concat(".").concat(name);
        Constructor constructor = ConstructorUtils
            .getMatchingAccessibleConstructor(exchangeType, String.class, Boolean.class, Boolean.class);
        try {
            Exchange exchange = (Exchange) constructor.newInstance(namespacedName, true, false);
            amqpAdmin.declareExchange(exchange);
            exchangeMap.put(namespacedName, exchange);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("Unable to instatiate exchange: {}; of type: {}", namespacedName, exchangeType.getName(),
                         e);
        }
    }

    /**
     * Creates a bridge from a queue to an exchange both with the same name
     *
     * @param key
     */
    private void declareNamespacedBridge(String key) {
        declareNamespacedBridge(key, key);
    }

    /**
     * Creates a fully customized queue
     *
     * @param name
     * @param durable
     * @param exclusive
     * @param autoDelete
     * @return
     */
    private String registerNamespacedQueue(String name, Boolean durable, Boolean exclusive, Boolean autoDelete) {
        Boolean namedQueue = name != null && !name.isEmpty();
        String namespacedName = namedQueue ? namespace.concat(".").concat(name) : "";
        return registerQueue(namespacedName, durable, exclusive, autoDelete);
    }

    /**
     * Creates a bridge from a queue to an exchange
     *
     * @param queueName
     * @param exchangeName
     */
    private void declareNamespacedBridge(String queueName, String exchangeName) {
        String namespacedQueue = namespace.concat(".").concat(queueName);
        String namespacedExchange = namespace.concat(".").concat(exchangeName);

        Assert.isTrue(externalQueueNames.containsKey(queueName));
        Assert.notNull(getExchangeByNamespacedName(exchangeName));

        registerListener(namespacedQueue, message->{
            LOGGER.debug("Bridging message from queue: {}; with routing key: {}, to exchange: {}", namespacedQueue,
                         message.getMessageProperties().getReceivedRoutingKey(), namespacedExchange);
            amqpTemplate.send(namespacedExchange, message.getMessageProperties().getReceivedRoutingKey(), message);
        });
    }

    //testability
    Map<String, String> getQueueNames() {
        return new HashMap<>(externalQueueNames);
    }

    //testability
    Map<String, Exchange> getExchangeMap() {
        return new HashMap<>(exchangeMap);
    }

    Exchange getFirehose() {
        return getExchangeByNamespacedName(exchange);
    }

    /**
     * Create the topic exchange acting as resthooks events firehose
     */
    @PostConstruct
    public void initialize() {
        registerNamespacedExchange(exchange, TopicExchange.class);
        /**
         * In runtime, for each subscription we will:
         *  - if no queue exists for the event type, create a queue for the specific event
         *  - create a subscriber channel:
         *      - create a queue
         *      - bind a queue to the firehose
         *      - bridge the queue to a fanout exchange
         *      - bind the fanout exchange to a subscriber queue
         *      - attach a subscriber to the queue
         */
    }

    /**
     * A channel is a shortcut for:
     * - create a queue
     * - bind a queue to the firehose
     * - bridge the queue to a fanout exchange
     *
     * @param entry
     * @param routingKey
     * @param exit
     */
    private void createNamespacedChannel(String entry, String routingKey, String exit) {
        Assert.notNull(entry);
        Assert.notNull(routingKey);
        Assert.notNull(exit);
        Assert.hasText(entry);
        Assert.hasText(routingKey);
        Assert.hasText(exit);
        Assert.isTrue(!entry.equals(exit));

        registerNamespacedQueue(routingKey);
        registerNamespacedBinding(routingKey, entry, routingKey);
        registerNamespacedExchange(exit, FanoutExchange.class);
        declareNamespacedBridge(routingKey, exit);
    }

    @PreDestroy
    public void clean() {
        for (MessageListenerContainer container : listeners) {
            container.stop();
        }
        listeners.clear();
        for (Binding binding : bindings) {
            amqpAdmin.removeBinding(binding);
        }
        bindings.clear();
        for (String queue : externalQueueNames.values()) {
            amqpAdmin.deleteQueue(queue);
        }
        externalQueueNames.clear();
        for (String queue : internalQueueNames) {
            amqpAdmin.deleteQueue(queue);
        }
        internalQueueNames.clear();
        for (String exchangeName : exchangeMap.keySet()) {
            amqpAdmin.deleteExchange(exchangeName);
        }
        exchangeMap.clear();
    }
}
