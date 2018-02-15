package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;
import dk.itu.percomp17.jumanji.soundProcessing.audioSampler.SoundBite;

public class SoundBiteQueue implements Serializable {

    private static final String TAG = "SoundBiteQueue";

    /**
     * Singleton instance
     */
    private static SoundBiteQueue ourInstance;

    /**
     * Queue listeners, for callbacks onDe & onEn -queue
     */
    private ArrayList<SoundBiteQueueListener> mListeners;

    /**
     * Queues for pre and post processing
     */
    private static ConcurrentHashMap<Integer, SoundBite> mEnqueue, mDequeue;

    /**
     * UserID current user. Stored here for debugging
     */
    private final String currentUserID;

    /**
     * Private Singleton constructor
     */
    private SoundBiteQueue() {
        mEnqueue = new ConcurrentHashMap<>();
        mDequeue = new ConcurrentHashMap<>();
        mListeners = new ArrayList<>();
        currentUserID = "566d0f71-21f1-42ab-849f-ec22933b8cf5";
    }

    /**
     * Get Singleton Instance
     * @return this instance
     */
    public static SoundBiteQueue getInstance() {
        if (ourInstance == null) ourInstance = new SoundBiteQueue();
        return ourInstance;
    }

    /**
     * enqueue a soundbite so it is readily available for sources to pre-process
     * Note: The SoundBite has not been enqueued.
     * @param soundBite
     */
    public void enqueue(SoundBite soundBite) {
        mEnqueue.put(soundBite.getID(), soundBite);
        onEnqueue(soundBite.getID()); // Notify listeners
    }

    /**
     * Mark a soundbite for dequeue when it has been pre-processed,
     * so it is readily available for sources to post-process
     * @param soundBiteID, ID of the soundBite to dequeue
     */
    public void dequeue(Integer soundBiteID) {
        if (mEnqueue.containsKey(soundBiteID)) {
            SoundBite soundBite = mEnqueue.get(soundBiteID);
            mDequeue.put(soundBiteID, soundBite);
            mEnqueue.remove(soundBiteID);
            onDequeue(soundBiteID); // Notify listeners
        } else Log.d(TAG, "dequeue() = enqueue !contains " + soundBiteID);
    }

    /**
     * Register a listener for callbacks
     * @param listener
     */
    public void registerListener(SoundBiteQueueListener listener) {
        this.mListeners.add(listener);
    }

    /**
     * Unregister a listener for no callbacks
     * @param listener
     */
    public void unregisterListener(SoundBiteQueueListener listener) {
        this.mListeners.remove(listener);
    }

    /**
     * Listener callback
     * @param soundbiteID
     */
    private void onEnqueue(Integer soundbiteID) {
        for (SoundBiteQueueListener listener: mListeners) listener.onEnqueue(soundbiteID);
    }

    /**
     * Listener callback
     * @param soundbiteID
     */
    private void onDequeue(Integer soundbiteID) {
        for (SoundBiteQueueListener listener : mListeners) listener.onDequeue(soundbiteID);
    }

    public void onIdentificationStatus(Integer soundbiteID, IdentificationStatus identificationStatus) {
        removeFromQueue(soundbiteID);
        for (SoundBiteQueueListener listener : mListeners) listener.onIdentificationStatus(soundbiteID, identificationStatus);
    }

    public void removeFromQueue(Integer soundbiteID) {
        if (mEnqueue.containsKey(soundbiteID)) mEnqueue.remove(soundbiteID);
        if (mDequeue.containsKey(soundbiteID)) mDequeue.remove(soundbiteID);
    }

    /**
     * Will retrieve a soundBite, either from enqueue or dequeue depending on its current state.
     * @param soundBiteID, OR null if nothing can be found with the soundbiteID
     */
    public SoundBite getSoundBite(Integer soundBiteID) {
        if (mEnqueue.containsKey(soundBiteID)) return mEnqueue.get(soundBiteID);
        else if (mDequeue.containsKey(soundBiteID)) return mDequeue.get(soundBiteID);
        return null;
    }

    // TODO: Store current UserID a different place
    public String getCurrentUserID() {
        return this.currentUserID;
    }

    // Persist Queue
//    public static void saveQueue() {
//    }
//
//    public static void loadQueue() {
//    }

}

