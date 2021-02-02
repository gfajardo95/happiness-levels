import shlex
import subprocess

from twitter import settings


# note: the pipeline MUST be closed by the user in the cloud
def run_pipeline():
    pipeline_args = '''
--runner=DataflowRunner --project={0} --region={1} 
--gcpTempLocation=gs://dataflow-{0}/tmp 
--inputSubscription=projects/{0}/subscriptions/{2} 
--outputTopic=projects/{0}/topics/{3} 
--workerMachineType=n1-standard-1 --maxNumWorkers=2'''.format(
        settings.PROJECT_NAME, settings.COMPUTE_REGION, settings.PUBSUB_INPUT_SUBSCRIPTION,
        settings.PUBSUB_OUTPUT_TOPIC_NAME).replace('\n', '')

    pipeline_run_command = "mvn compile exec:java -Dexec.mainClass={} -Dexec.args=\"{}\" -Pdataflow-runner".format(
        settings.MAIN_CLASS_NAME, pipeline_args)

    happinessPipeline = subprocess.Popen(shlex.split(pipeline_run_command),
                                         stdout=subprocess.PIPE,
                                         stderr=subprocess.STDOUT,
                                         universal_newlines=True)

    for line in iter(happinessPipeline.stdout.readline, ""):
        print(line)
        if "Workers have started successfully" in line:
            break


def open_twitter_stream():
    print("Opening twitter stream")
    twitterStreamer = subprocess.Popen(['python', 'twitter/publisher.py'],
                                       stdout=subprocess.PIPE,
                                       stderr=subprocess.STDOUT,
                                       universal_newlines=True)

    while True:
        print(twitterStreamer.stdout.readline())

        return_code = twitterStreamer.poll()
        if return_code is not None:
            print("Return code {}".format(return_code))
            break


run_pipeline()
open_twitter_stream()
