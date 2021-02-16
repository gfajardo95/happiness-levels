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

### Option 2: Publisher with Data Pipeline
The `run_pipeline.py` script starts up the data pipeline with the Dataflow runner. Once the "workers are ready" message is received 
then the `publisher.py` script from above is executed. The publisher runs infinitely, so exit the program when you wish, and also 
close the pipeline in the cloud console.

To run this option use the command: `$ python twitter/run_pipeline.py`
 
### Option 3: Real-time Dashboard
A React dashboard can communicate through RSocket with an endpoint that subscribes to the pipeline's output. To begin 
the process and eventually see data come into the dashboard (it takes a while) first start up the Spring Boot server. Use the 
`mvn spring-boot:run -Dspring-boot.run.profiles=dev` command to do so. Then, in a separate terminal window, go inside the `/webapp` directory and start the client with `npm start`. Use the logs to track the process, and be patient as the Twitter stream has been 
highly reduced to only include Tweets that have country information.
