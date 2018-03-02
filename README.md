# Happiness Levels
Which is the happiest country on Earth? There are many top 10 lists out there, but this application aims to make its own conclusion. 


## How It Works
An Apache Beam pipeline collects tweets in "streaming" mode and performs sentiment analysis on them. After 2 minutes of data collection, the pipeline begins to send data to BigQuery where calculations are made on the happiness of different countries.

## Installation
You'll need to have Python3 and Java 8 installed. Make sure, if your on windows, to be able to run Python from the 
Command Line. In other words, make sure it's in the PATH.

### Steps
1. Clone this repo: `$ git clone https://github.com/gfajardo95/happiness-levels.git`
2. Switch into the working directory: `$ cd happiness-levels`
3. Create a virtual environment for the python packages: `$ virtualenv venv`
4. Install the project's python dependencies: `pip install -r requirements.txt`

## Usage
The application has different ways it can be used. The different components of the pipeline can run individually, or 
they can be ran together to form the data pipeline.

### Option 1: Publisher
In the `twitter` directory you will find a file called `publisher.py`. It's possible to simply run this 
script, and integrate it with your own Google Cloud "subscriber".

To run this option use the command: `$ python twitter/publisher.py`

### Option 2: Publisher with Subscriber
To see a fully integrated example of a Pub/Sub application without an intermediate data transformation step (the Beam pipeline) you can also run the `main.py` file, which is also found in the `twitter` directory. This file publishes the tweets to a Cloud Pub/Sub topic, and then the subscriber client, `subscriber.py`, sends the tweets from the topic to a Cloud BigQuery table.

To run this option use the command: `$ python twitter/main.py`
 
### Option 3: Publisher to Beam
This is the full end-to-end solution this application is designed to do. It is coming soon!
