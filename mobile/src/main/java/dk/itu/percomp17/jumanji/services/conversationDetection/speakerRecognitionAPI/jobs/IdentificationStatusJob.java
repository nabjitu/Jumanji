package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.config.SubscriptionKeys;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.OperationLocation;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue.SoundBiteQueue;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.SoundBite;

/**
 * JobService for carrying out a GET IdentificationStatus Request on the Azure Cognitive Service API
 */
public class IdentificationStatusJob extends JobService {

    private static final String TAG = "IdentificationStatusJob";

    IdentificationStatusTask mTask;
    OperationLocation mOperationLocation;


    public IdentificationStatusJob() {
    }

    @Override
    public boolean onStartJob(final JobParameters params) {

        Log.d(TAG, "IdentificationStatusJob, onStartJob()");

        final SoundBiteQueue queue = SoundBiteQueue.getInstance();
        final Integer soundBiteID = params.getJobId();
        final SoundBite soundBite = queue.getSoundBite(soundBiteID);

        if (soundBite == null) {
            Log.d(TAG, "IdentificationStatusJob, soundBite == null");
            Log.d(TAG, "IdentificationStatusJob, jobFinished(false)");
            jobFinished(params, false); // abort job no SoundBite
        }

        OperationLocation operationLocation = soundBite.getOperationLocation();
        if (operationLocation.getResponseCode() == 404) {
            Log.d(TAG, "IdentificationStatusJob, operationLocation == 404");
            Log.d(TAG, "IdentificationStatusJob, jobFinished(false)");
            jobFinished(params, false); // abort job no SoundBite
        }

        // Create new Async IdentificationStatusTask, so as not to freeze the UI
        mTask = new IdentificationStatusTask() {
            @Override
            protected void onPostExecute(IdentificationStatus identificationStatus) {
                Log.d(TAG, "IdentificationStatusJob, onPostExecute()");
                boolean redoJob = false;

                // check result of IdentificationStatus or if it is Null.
                if (identificationStatus != null) {
                    String status = identificationStatus.getStatus();
                    switch (status) {
                        case "nostarted":
                            Log.d(TAG, "IdentificationStatusJob, notstarted");
                            redoJob = true;
                            break;

                        case "running":
                            Log.d(TAG, "IdentificationStatusJob, running");
                            redoJob = true;
                            break;

                        case "failed":
                            Log.d(TAG, "IdentificationStatusJob, failed");
                            queue.onIdentificationStatus(soundBiteID, identificationStatus);
                            redoJob = false;
                            break;

                        case "succeeded":
                            Log.d(TAG, "IdentificationStatusJob, succeeded");
                            queue.onIdentificationStatus(soundBiteID, identificationStatus);
                            redoJob = false;
                            break;
                    }
                } Log.d(TAG, "IdentificationStatusJob, identificationStatus == null");

                jobFinished(params, redoJob);
            }
        };

        // Check if SoundBite has an operation location
        if (mOperationLocation != null) {
            if (!mOperationLocation.getOperationLocation().isEmpty()) {
                mTask.execute(mOperationLocation);
                return true; // True: this job runs on its own thread
            }
        }

        // Soundbite has no operationLocation. Abort.
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Release resources
        if (mTask != null) mTask.cancel(true);
        return true;
    }

    /**
     * Private inner AsyncTask mTask class for this JobService.
     * Exist for the purpose of running this job on its own thread.
     *
     * Thiss job will return an IdentificationStatus object.
     * If HTTP request does not return a response code 200, the request
     * has failed and the returned object will be null.
     */
    private class IdentificationStatusTask extends AsyncTask<OperationLocation, Void, IdentificationStatus> {

        @Override
        protected IdentificationStatus doInBackground(OperationLocation... params) {

            HttpURLConnection connection = null;
            IdentificationStatus identificationStatus = null;

            try {
                URL url = new URL(mOperationLocation.getOperationLocation());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Ocp-Apim-Subscription-Key", SubscriptionKeys.subscriptionKeyOne);

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    Log.d(TAG, "IdentificationStatusJob: response 200");
                    JsonNode json = new ObjectMapper().readTree(connection.getInputStream());
                    identificationStatus = new IdentificationStatus(json);
                }

                connection.disconnect();
                return identificationStatus;

            } catch (IOException e) {
                if (connection != null) connection.disconnect();
                e.printStackTrace();
            }
            return null;
        }
    }
}
