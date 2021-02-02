from google.cloud import pubsub

import settings


def read_and_publish_file():
    client = pubsub.PublisherClient()
    topic_path = client.topic_path(settings.PROJECT_NAME, settings.PUBSUB_TOPIC_NAME)
    f = open('twitter/tmp/tweetbytes.txt', 'rb')
    client.publish(topic_path, data=f.read())


if __name__ == '__main__':
    read_and_publish_file()