package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.jobFactory;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.jobs.IdentificationJob;
import static android.content.Context.JOB_SCHEDULER_SERVICE;

/**
 * A JobScheduler wrapper class for posting SpeakerRecognition request jobs.
 * TODO: Make it possible to enqueue jobServices to this JobScheduler
 * TODO: This wrapper class might be redundant, and could be inside the ConversationDetectionService
 *
 */
public class JobFactory {

    private static final String TAG = "JobFactory";

    private JobScheduler jobScheduler;
    private Context context;

    public JobFactory(Context context) {
        this.context = context;
        jobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
    }

    public void newIdentificationJob(Integer soundBiteID) {
        ComponentName componentName = new ComponentName(context, IdentificationJob.class);
        JobInfo jobInfo = new JobInfo.Builder(soundBiteID, componentName)
                .setRequiresCharging(false)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Identification Job scheduled!");
        } else {
            Log.d(TAG, "Identification Job not scheduled");
        }
    }

    // Warning, calling this before an identification job has finnished will result in an exception
    // Due to jobs being identified by the matching soundBiteID.
    // hus a soundbite can only have one job running at a time.
    public void newIdentificationStatusJob(Integer soundBiteID) {
        ComponentName componentName = new ComponentName(context, IdentificationJob.class);
        JobInfo jobInfo = new JobInfo.Builder(soundBiteID, componentName)
                .setRequiresCharging(false)
                .setOverrideDeadline(10000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Identification Status Job scheduled!");
        } else {
            Log.d(TAG, "Identification Status Job not scheduled");
        }
    }
}
