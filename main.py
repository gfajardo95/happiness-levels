from twitter_publisher import publish_twitter_stream
from twitter_subscriber import receive_messages


PROJECT_NAME = 'happiness-level'
PUBSUB_TOPIC_NAME = 'tweets'
SUBSCRIPTION_NAME = 'tweet-listener'


# how to make this run at the same time?
# right now the messages are all cached to the topic and when it's done
# streaming THEN the subcriber comes and receives the messages
def main():
    publish_twitter_stream()
    receive_messages(PROJECT_NAME, SUBSCRIPTION_NAME)


if __name__ == '__main__':
    main()
