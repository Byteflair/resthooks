# User Stories

## As a resthooks client, I want to create a subscription to an event type
A subscription identifies the subscriber and the hook URL where notifications are POSTed.
A resthooks client is identified in the system by a subscriber identifier. This is an arbitrary String.
A resthooks client can subscribe to more than one event type.
A resthooks client may subscribe only once to an event type.

## As a subscriber, I want to receive notifications of events I have subscribed to
As soon as an event is received, the resthooks server processes it only if there are subscriptions for that event type.

1. The event is received in the TopicExchange specified with the **resthooks.exchange** variable, with default value of: resthooks.firehose.
2. The exchange routes the event to the corresponding Queue.
3. The event is delivered to a per ecent type message pre-processor that validates the payload and saves the Event
4. The message pre-processor sends the event to a FanOut exchange that in turn broadcasts the Event to a per Subscription EventConsumer
5. The EventConsumer has been configure with the corresponding subscription and receives the Event:
    1. If the Event is valid it continues processing it, otherwise it discards it. Invalid Events are not notified but can be queried.
    2. The EventConsumer tries to send a POST request, with a link to the Event resource (Skinny Payload), to the callback URL configured in the corresponding subscription
    3. If the request fails, the EventConsumer retries following the backoff policy configured with the Subscription
   
## As a subscriber, I want to see a list of my suscriptions


## As a subscriber, I want to deactivate a subscription


## As a subscriber, I want to activate an inactive subscription



## As a subscriber, I want to see all the callbacks made as a result of a subscription

The subscriber can request a list of notification attempts  

## As a subscriber, I want to see the event log of an event

# Infraestructure dependencies

This solution relies in RabbitMQ and MongoDB. You need to configure access to these resoures for the Resthooks module to work.

# Configuration

The following properties should be set, either in the command line as enviroment vairables, JVM arguments, in an application.properties or application.yml files. Please, note the different formats you need to use in each case.

```
   spring.data.mongodb.host=localhost
   spring.data.mongodb.port=27017
   spring.data.mongodb.db=hooks 
   spring.rabbitmq.host=localhost 
   spring.rabbitmq.port=5672
```

By default, we are not using any username or password to connect to rabbitmq. You can override the default values setting these properties:

```
   spring.rabbitmq.username=guest
   spring.rabbitmq.password=guest
```

Additionally you can enable DEBUG or TRACE level logs setting following property to the desired log level: `logging.level.com.byteflair=DEBUG`.
