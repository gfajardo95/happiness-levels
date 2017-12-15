import base64
import json

from google.cloud import pubsub_v1

from tweepy import StreamListener
from utils import convertToJSON

PROJECT_NAME = 'happiness-level'
PUBSUB_TOPIC_NAME = 'tweets'


# adapted from a google example
def publish(client, topic_path, data_lines):
    messages = []
    for line in data_lines:
        messages.append({'data': line})
    body = {'messages': messages}
    str_body = json.dumps(body)
    data = base64.urlsafe_b64encode(bytearray(str_body, 'utf8'))
    client.publish(topic_path, data=data)


class StreamListener(StreamListener):
    client = pubsub_v1.PublisherClient()
    topic_path = client.topic_path(PROJECT_NAME, PUBSUB_TOPIC_NAME)
    count = 0
    tweets = []
    batch_size = 50
    total_tweets = 100

    def write_to_pubsub(self, tw):
        publish(self.client, self.topic_path, tw)

    def on_status(self, status):
        # tweets without a location are not published to the topic
        if not status.user.location:
            return
        # relevant tweet data
        text = status.text
        retweets = status.retweet_count
        loc = status.user.location
        description = status.user.description
        data = dict(text=text, retweets=retweets, location=loc,
                    description=description)
        tw = convertToJSON(data)
        self.tweets.append(tw)
        if len(self.tweets) > self.batch_size:
            self.write_to_pubsub(self.tweets)
            self.tweets = []

        self.count += 1
        if self.count > self.total_tweets:
            return False
        if (self.count % 50) == 0:
            print("count is: {}".format(self.count))
        return True

    def on_error(self, status_code):
        print(status_code)
