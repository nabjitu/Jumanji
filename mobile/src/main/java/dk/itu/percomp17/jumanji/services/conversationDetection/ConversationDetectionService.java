package dk.itu.percomp17.jumanji.services.conversationDetection;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;
import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperSoundbite;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.SpeakerIdentificationAPI;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.jobFactory.JobFactory;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue.SoundBiteQueue;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue.SoundBiteQueueListener;
import dk.itu.percomp17.jumanji.soundProcessing.JumanjiAudioFormat;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.AudioEventListener;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.AudioSampler;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.SoundBite;

/**
 * Big Brother
 */

// TODO: Incorporate the JobScheduler in this service.
// TODO: On AudioEvents, add pre-process soundbites -> create identificationRequests and post them to the JobScheduler
// TODO: On ConversationEvent, check result of ConversationEvent -> store result in the Database

public class ConversationDetectionService extends Service {

    private static final String TAG = "ConversationService";

    AudioSampler mSampler;
    AudioEventListener mAudioEventListener;

    SoundBiteQueue mSoundBiteQueue;
    SoundBiteQueueListener mQueueListener;
    DatabaseHelperSoundbite databaseSB;

    String userId;

    // For scheduling API calls to save battery consumption
    JobFactory mJobFactory;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "-ConversationDetectionService: onStartCommand()-");

//        databaseSB = new DatabaseHelperSoundbite(getApplicationContext());

        // Used for launching SoundBite analysis jobs
//        mJobFactory = new JobFactory(getApplicationContext());

        // Get Queue and register for queue events
//        mSoundBiteQueue = SoundBiteQueue.getInstance();
//        mQueueListener = new QueueListener();
//        mSoundBiteQueue.registerListener(mQueueListener);

        // AudioSampler config
        int encoding = JumanjiAudioFormat.ENCODING;
        int sampleRate = JumanjiAudioFormat.SAMPLE_RATE;
        int numChannels = JumanjiAudioFormat.NUM_CHANNELS;
        int audioSource = MediaRecorder.AudioSource.DEFAULT;

        // Setup AudioSampler
        mSampler = new AudioSampler(audioSource, encoding, sampleRate, numChannels);
        mSampler.setSampleWindow(1000, 4000);

        // Setup Callbacks for AudioEvents created by the AudioSampler
        mAudioEventListener = new AudioListener();
        mSampler.registerListener(mAudioEventListener);

        // RUN!
        mSampler.start();

        return Service.START_STICKY; // Start again should the system shut down the service
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "-onDestroy-");
    }

    // ==================================  Event Listeners ====================================== //
    /**
     * Private class implementation of the AudioEventListener for receiving sound data
     * published by an AudioEvent creator such as the AudioSampler.
     * @see AudioSampler
     */
    private class AudioListener implements AudioEventListener {

        @Override
        public void onAudioEvent(SoundBite soundBite) {
            Log.d(TAG, "-onAudioEvent: soundbite Received-");
//            byte[] data = soundBite.getData();
//            if (data.length > 1) {
//                mSoundBiteQueue.enqueue(soundBite);
//            }
//
//            else Log.d(TAG, "-onAudioEvent: soundbite data is empty-");
        }
    }

    /**
     * Private class implementation of the SoundBiteQueueListener
     * Upon queue changes, will dispatch pre or post processing tasks
     */
//    private class QueueListener implements SoundBiteQueueListener {
//
//        @Override
//        public void onEnqueue(Integer soundBiteID) {
//            Log.d(TAG, "-onEnqueue: newIdentificationJob-");
//
//            String operationLocation = SpeakerIdentificationAPI.identification(mSoundBiteQueue.getSoundBite(soundBiteID).getData());
//            Log.d(TAG, "OperationLocation = " + operationLocation);
//            mSoundBiteQueue.removeFromQueue(soundBiteID);
////            mJobFactory.newIdentificationJob(soundBiteID);
//        }
//
//        @Override
//        public void onDequeue(Integer soundBiteID) {
//            mSoundBiteQueue.removeFromQueue(soundBiteID);
////            Log.d(TAG, "-onDequeue: newIdentificationStatusJob-");
//        }
//
//        @Override
//        public void onIdentificationStatus(Integer soundbiteID, IdentificationStatus identificationStatus) {
//            SoundBite sb =  SoundBiteQueue.getInstance().getSoundBite(soundbiteID);
//            String date = new SimpleDateFormat("dd:MM:yyyy").format(new Date(System.currentTimeMillis()));
//
//            if (sb != null) {
//                switch (identificationStatus.getStatus()) {
//                    case "succeeded" :
//                        Log.d(TAG, "IdentificationSucceeded");
//                        Log.d(TAG, "Identification Confidence: " + identificationStatus.getConfidence());
//                        databaseSB.addData(LoginActivity.getLoggedInCredentials().getAzProfileID(), date, sb.getFrom(), sb.getTo(), 1);
//                        break;
//
//                    case "failed" :
//                        Log.d(TAG, "IdentificationFailed " + identificationStatus.getMessage());
//                        databaseSB.addData(LoginActivity.getLoggedInCredentials().getAzProfileID(), date, sb.getFrom(), sb.getTo(), 1);
//                        break;
//                }
//            }
//        }
//    }
}


// NEVER DESTROY! THIS KEEPS THE SERVICE RUNNING FOREVER. TODO: fix this
// Intent broadcastIntent = new Intent("dk.itu.percomp17.jumanji.RestartConversationDetectionService");
// sendBroadcast(broadcastIntent);