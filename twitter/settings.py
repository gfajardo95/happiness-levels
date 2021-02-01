import os

from dotenv import load_dotenv
load_dotenv()

TWITTER_APP_KEY = os.environ.get("TWITTER_APP_KEY")
TWITTER_APP_SECRET = os.environ.get('TWITTER_APP_SECRET')
TWITTER_KEY = os.environ.get('TWITTER_KEY')
TWITTER_SECRET = os.environ.get('TWITTER_SECRET')

MAIN_CLASS_NAME = os.environ.get('MAIN_CLASS_NAME')

PROJECT_NAME = os.environ.get('PROJECT_NAME')
COMPUTE_REGION = os.environ.get('COMPUTE_REGION')
PUBSUB_TOPIC_NAME = os.environ.get('PUBSUB_TOPIC_NAME')
PUBSUB_INPUT_SUBSCRIPTION = os.environ.get('PUBSUB_INPUT_SUBSCRIPTION')
PUBSUB_OUTPUT_TOPIC_NAME = os.environ.get('PUBSUB_OUTPUT_TOPIC_NAME')

SUBSCRIPTION_NAME = os.environ.get('SUBSCRIPTION_NAME')
DATASET_ID = os.environ.get('DATASET_ID')
TABLE_ID = os.environ.get('TABLE_ID')
