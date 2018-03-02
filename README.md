# Happiness Levels
Which is the happiest country on Earth? There are many top 10 lists out there, but this application aims to make its own conclusion. 


## How It Works
An Apache Beam pipeline collects tweets in "streaming" mode and performs sentiment analysis on them. After 2 minutes of data collection, the pipeline begins to send data to BigQuery where calculations are made on the happiness of different countries.
