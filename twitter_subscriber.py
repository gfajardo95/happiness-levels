# a pull subscriber that writes tweets to cloud BigQuery
import base64
import json
import time

from google.cloud import pubsub_v1
from google.cloud import bigquery


PROJECT_NAME = 'happiness-level'
SUBSCRIPTION_NAME = 'tweet-listener'


def receive_tweets(project, subscription_name):
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    def callback(message):
        print('Received message: {}'.format(message))
        message.ack()
        send_tweets_to_bigquery(message.data)

    subscriber.subscribe(subscription_path, callback=callback)

    print('Listening for messages on {}'.format(subscription_path))
    while True:
        time.sleep(60)


def send_tweets_to_bigquery(message):
    stream = base64.urlsafe_b64decode(message)
    twstream = json.loads(stream)
    print([twstream])


if __name__ == '__main__':
    receive_tweets(PROJECT_NAME, SUBSCRIPTION_NAME)
