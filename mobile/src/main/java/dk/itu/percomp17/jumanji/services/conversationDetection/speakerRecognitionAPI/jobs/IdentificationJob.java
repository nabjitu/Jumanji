package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.config.SubscriptionKeys;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue.SoundBiteQueue;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.SoundBite;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.OperationLocation;
import dk.itu.percomp17.jumanji.soundProcessing.rawToWave.BytesToWav;

/**
 * JobService for carrying out a PUT Identification Request to the Azure Cognitive Service API
 */
public class IdentificationJob extends JobService {

    private static final String TAG = "IdentificationJob";
    private IdentificationTask task;

    public IdentificationJob() {
    }

    @Override
    public boolean onStartJob(final JobParameters params) {

        // Get Job Resources
        final Integer soundBiteID = params.getJobId();
        Log.d(TAG, "======================================");
        Log.d(TAG, "onStartJob, SoundBiteID " + soundBiteID);
        Integer[] taskParams = { soundBiteID };

        // Create new Async IdentificationTask, so as not to freeze the UI
        task = new IdentificationTask() {
            @Override
            protected void onPostExecute(OperationLocation operationLocation) {
                if (operationLocation != null) {
                    if (operationLocation.getResponseCode() == 202) { //202 Accepted
                        Log.d(TAG, "-----------------------");
                        Log.d(TAG, "onPostExecute()");
                        Log.d(TAG, "operationLocation = " + operationLocation.getOperationLocation());
                        SoundBiteQueue queue = SoundBiteQueue.getInstance();
                        SoundBite soundBite = queue.getSoundBite(soundBiteID);
                        soundBite.setOperationLocation(operationLocation);
                        Log.d(TAG, "dequeue() soundbite = " + soundBiteID);
                        queue.dequeue(soundBiteID);
                    } else Log.d(TAG, "IdentificationJob, onPostExecute() Response Code 404");
                } else Log.d(TAG, "IdentificationJob, OperationLocation == null");

                jobFinished(params, false);
            }
        };

        task.execute(taskParams);
        return true; // True: this job runs on its own thread
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Release resources
        if (task != null) task.cancel(true);
        return true;
    }

    /**
     * Private inner AsyncTask mTask class for this JobService.
     * Exist for the purpose of running this job on its own thread.
     */
    private class IdentificationTask extends AsyncTask<Integer, Void, OperationLocation> {

        @Override
        protected OperationLocation doInBackground(Integer... params) {

            final SoundBiteQueue queue = SoundBiteQueue.getInstance();
            final String userID = queue.getCurrentUserID();
            final Integer soundBiteID = params[0];

            Log.d(TAG, "---------------------------------");
            Log.d(TAG, "!! IdentificaitonTask !! doInBackground(), soundBiteID = " + soundBiteID);
            Log.d(TAG, "userID " + userID);

            SoundBite soundBite = queue.getSoundBite(soundBiteID);

            if (soundBite.getData() == null) {
                Log.d(TAG, "soundBite == null");
                return null; //Abort mission, could not find SoundBite
            }

            HttpURLConnection connection = null;

            String id = "566d0f71-21f1-42ab-849f-ec22933b8cf5";

            try {
                URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds="+ id + "&shortAudio=true");
                connection = (HttpURLConnection) url.openConnection();

                byte[] waveData = BytesToWav.bytesToWave(soundBite.getData());
                connection.setFixedLengthStreamingMode(waveData.length);
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(waveData);
                output.flush();
                output.close();

                // Get response
                int responseCode = connection.getResponseCode();
                String message = connection.getResponseMessage();

                Log.d(TAG, "responseCode " + responseCode);
                Log.d(TAG, "responseMessage " + message);
                Log.d(TAG, "OperationLocation " + connection.getHeaderField("Operation-Location"));

                if (responseCode == 202) {
                    Log.d(TAG, "IdentificationJob: responseCode 202");
                    String operationLocationURL = connection.getHeaderField("Operation-Location");
                    connection.disconnect();
                    return new OperationLocation(operationLocationURL, responseCode);
                }

                if (responseCode == 404) {
                    Log.d(TAG, "IdentificationJob: responseCode 404");
                    connection.disconnect();
                    queue.removeFromQueue(soundBiteID);
                    return new OperationLocation("", responseCode);
                }

                Log.d(TAG, "No response Code");
                return null;

            } catch (IOException e) {
                e.printStackTrace();
                if (connection != null) connection.disconnect();
                return null;
            }
        }
    }
}
