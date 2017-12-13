# a pull subscriber that writes tweets to cloud BigQuery
import time

from google.cloud import pubsub_v1
from google.cloud import bigquery


PROJECT_NAME = 'happiness-level'
SUBSCRIPTION_NAME = 'tweet-listener'


def receive_messages(project, subscription_name):
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    def callback(message):
        print('Received message: {}'.format(message))
        message.ack()

    subscriber.subscribe(subscription_path, callback=callback)

    print('Listening for messages on {}'.format(subscription_path))
    while True:
        time.sleep(60)


if __name__ == '__main__':
    receive_messages(PROJECT_NAME, SUBSCRIPTION_NAME)
