# Python program to explain os.environ object

# importing os module
import os

# Add a new environment variable
os.environ["TWITTER_APP_KEY"] = "FAkVldQ25DE07a2AhdNnfupgf"
os.environ['TWITTER_APP_SECRET'] = "phmgmffLlxX7E3Mf71MIi3zJY2OGkqyW7AjBNUAUCNKoYXWxzE"
os.environ['TWITTER_KEY'] = "AAAAAAAAAAAAAAAAAAAAAGicRwEAAAAArwFgE0kdJyG%2BRVU7LgYzDIB7B8E%3DAeQVZSBHIIXlxX99pcPhcf7hauYpqcGlFb1cHfpKLJM62WDDIH"
os.environ['TWITTER_SECRET'] = "1356638480726712322-MWIQwZ4FHErW8mS2MslmadrT6g2Fyz"


os.environ['PROJECT_NAME'] = 'splendid-ground-324201'
os.environ['COMPUTE_REGION'] = 'southamerica-east1-b' # 'us-central1-a' , 'us-central1-c'
os.environ['PUBSUB_TOPIC_NAME'] = 'dataengineering'
os.environ['PUBSUB_INPUT_SUBSCRIPTION'] = 'www.geeksforgeeks.org'
os.environ['PUBSUB_OUTPUT_TOPIC_NAME'] = 'www.geeksforgeeks.org'

os.environ['SUBSCRIPTION_NAME'] = 'www.geeksforgeeks.org'
os.environ['DATASET_ID'] = 'www.geeksforgeeks.org'
os.environ['TABLE_ID'] = 'www.geeksforgeeks.org'

# Get the value of
# Added environment variable
print("GeeksForGeeks:", os.environ['GeeksForGeeks'])

##
SET PROJ_HOME=%USERPROFILE%/proj/111
SET PROJECT_BASEDIR=%PROJ_HOME%/exercises/ex1
mkdir "%PROJ_HOME%"

SET GOOGLE_APPLICATION_CREDENTIALS=D:\TYPER\python-pubsub\splendid-ground-324201-68e2ed45f735.json

SET PROJECT='gcloud config get-value project'

#~/Downloads/key.json
##

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
