# a pull subscriber that writes tweets to cloud BigQuery
import base64
import json
import time

from google.cloud import bigquery
from google.cloud import pubsub_v1

import settings


def write_tweets_to_bq(dataset_id, table_id, tweets):
    client = bigquery.Client()
    dataset_ref = client.dataset(dataset_id)
    table_ref = dataset_ref.table(table_id)
    table = client.get_table(table_ref)

    errors = client.insert_rows(table, tweets)
    if not errors:
        print('Loaded {} row(s) into {}:{}'.format(len(tweets), dataset_id, table_id))
    else:
        print('Errors:')
        for error in errors:
            print(error)


# twraw is a dictionary
# twraw['messages'] is a list
# each list contains a dict with key data
# list at each position gives us a dict with all the data points. That's the dict we want to save
def write_tweets_to_bigquery(data):
    if data:
        tweets = []
        stream = base64.urlsafe_b64decode(data)
        twraw = json.loads(stream)
        twmessages = twraw.get('messages')
        for message in twmessages:
            tweets.append(message['data'])

        write_tweets_to_bq(settings.DATASET_ID, settings.TABLE_ID, tweets)
        return True
    else:
        return False


def receive_tweets(project, subscription_name):
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    # ack should be the last thing you do
    def callback(message):
        print('Received message: {}'.format(message))
        status = write_tweets_to_bigquery(message.data)
        if status is True:
            message.ack()
        else:
            print("received bad data")

    subscriber.subscribe(subscription_path, callback=callback)

    print('Listening for messages on {}'.format(subscription_path))
    while True:
        time.sleep(60)


if __name__ == '__main__':
    receive_tweets(settings.PROJECT_NAME, settings.SUBSCRIPTION_NAME)
