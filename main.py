from twitter_publisher import publish_twitter_stream
from twitter_subscriber import receive_tweets


PROJECT_NAME = 'happiness-level'
PUBSUB_TOPIC_NAME = 'tweets'
SUBSCRIPTION_NAME = 'tweet-listener'


# how to make this multithreaded?
def main():
    publish_twitter_stream()
    receive_tweets(PROJECT_NAME, SUBSCRIPTION_NAME)


if __name__ == '__main__':
    main()
