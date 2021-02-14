import base64
import json

from google.cloud import pubsub
from tweepy import StreamListener

import settings


# publishes the tweets to a Pub/Sub topic
def publish(client, topic_path, data_lines):
    messages = []
    for line in data_lines:
        messages.append({'data': line})
    body = {'messages': messages}
    str_body = json.dumps(body)
    data = base64.urlsafe_b64encode(bytearray(str_body, 'utf8'))
    client.publish(topic_path, data=data)


class TweetStreamListener(StreamListener):
    client = pubsub.PublisherClient()
    topic_path = client.topic_path(settings.PROJECT_NAME, settings.PUBSUB_TOPIC_NAME)
    count = 0
    tweets = []
    batch_size = 50

    def write_to_pubsub(self, tw):
        publish(self.client, self.topic_path, tw)

    def on_status(self, status):
        # tweets without a location are not published to the topic
        if not status.user.location:
            return

        place = status.place
        if place is not None:
            text = status.text
            tw = dict(text=text, country=place.country)

            self.tweets.append(tw)
            self.count += 1

            if len(self.tweets) >= self.batch_size:
                print("publishing tweet stream")
                self.write_to_pubsub(self.tweets)
                self.tweets = []

            if (self.count % 50) == 0:
                print("count is: {}".format(self.count))

        return True

    def on_error(self, status_code):
        print("error: {}".format(status_code))
        if status_code == 420:
            return False

        return True
