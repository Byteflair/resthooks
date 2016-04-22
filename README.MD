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
3. The event is delivered to the message processor at the other end of the Queue.
   * The message processor saves the event in the database
   * The message processor tries to notify the event to the 
   
   
## As a subscriber, I want to see a list of my suscriptions

## As a subscriber, I want to deactivate a subscription

## As a subscriber, I want to activate an inactive subscription

## As a subscriber, I want to see the events of a subscription

## As a subscriber, I want to see the event log of an event
