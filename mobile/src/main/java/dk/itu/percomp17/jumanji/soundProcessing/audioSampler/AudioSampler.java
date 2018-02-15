package dk.itu.percomp17.jumanji.soundProcessing.audioSampler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for capturing and publishing audio samples at a fixed sampleLength and intervals.
 * Captures sound from a given sound source.
 */
public class AudioSampler extends Thread {

    private static final String TAG = "ConversationService";

    // Audio Format
    private final int RECORDER_CHANNELS;
    private final int RECORDER_SAMPLERATE;
    private final int RECORDER_AUDIO_ENCODING;
    private final int RECORDER_AUDIO_SOURCE;

    private int minimumBufferSize;
    private int audioSource;

    // AudioSampler Window Size
    private long sampleInterval;
    private long sampleLength;

    AudioRecord recorder;
    private volatile AtomicBoolean isActive;
    private ArrayList<AudioEventListener> listeners;

    private Thread samplerThread;

    /**
     * Constructs an AudioSampler that captures sound with given parameters.
     *
     * @param audioSource
     * @param encoding
     * @param sampleRate
     * @param numChannels
     * @see AudioFormat
     */
    public AudioSampler(int audioSource, int encoding, int sampleRate, int numChannels) {
        // Default SamplerSize
        this.isActive = new AtomicBoolean();
        this.isActive.set(false);
        this.sampleLength = 1000;
        this.sampleInterval = 1000;

        this.listeners = new ArrayList<>();
        this.audioSource = audioSource;

        this.RECORDER_SAMPLERATE = sampleRate;
        this.RECORDER_CHANNELS = numChannels;
        this.RECORDER_AUDIO_ENCODING = encoding;
        this.RECORDER_AUDIO_SOURCE = audioSource;

        this.minimumBufferSize = AudioRecord
                .getMinBufferSize(sampleRate, numChannels, encoding);

        recorder = newRecorder();
    }

    /**
     * Specify the sampleInterval and sampleLength at which the AudioSampler should capture sound.
     * Intervals and length must be above or equal to a second. Anything lower will default too a second.
     *
     * @param interval_millis, the sampleInterval between each sample in milliseconds
     * @param length_millis,   the sample sampleLength in milliseconds
     */
    public void setSampleWindow(long interval_millis, long length_millis) {
        if (interval_millis >= 1000) this.sampleInterval = interval_millis;
        else sampleInterval = 1000;

        if (length_millis >= 1000) this.sampleLength = length_millis;
        else sampleLength = 1000;
    }

    @Override
    public void start() {
        if (!isActive()) {
            if (sampleLength >= 1000 || sampleInterval >= 100) {
                isActive.set(true);
                samplerThread = new Thread(sampler);
                samplerThread.start();
            }
        }
    }

    private AudioRecord newRecorder() {
        return  recorder = new AudioRecord(RECORDER_AUDIO_SOURCE,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, minimumBufferSize);
    }

    public void stopSampler() {
        isActive.set(false);
    }

    public boolean isActive() {
        return isActive.get();
    }


    public void registerListener(AudioEventListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(AudioEventListener listener) {
        listeners.remove(listener);
    }

    private void onAudioEvent(SoundBite soundbite) {
        for (AudioEventListener listener : listeners) listener.onAudioEvent(soundbite);
    }

    // ===============================  SAMPLER THREAD  ===========================================

    private Runnable sampler = new Runnable() {

        @Override
        public void run() {

            while (isActive()) {

                // Record and publish sample
                SoundBite soundbite = recordSample();
                if (soundbite != null) onAudioEvent(soundbite);
                else System.out.println("No soundbite object");

                // Pause recording for the specified window size
                long time = System.currentTimeMillis();
                long end = time + sampleInterval;

                while(System.currentTimeMillis() < end) {
                }
            }

            Log.d(TAG, "-AudioSampler: sampler !isActive");
        }
    };

    /**
     * Records for the specified interval
     * @return SoundBite containing the raw sound data and a timestamp
     */
    private SoundBite recordSample() {

        ByteArrayOutputStream dataOut;
        SoundBite soundBite;

        // Get our resources
        dataOut = new ByteArrayOutputStream();
        recorder.startRecording();

        // Record for the specified time
        long time = System.currentTimeMillis();
        long end = time + sampleLength;

        byte[] readBuffer = new byte[minimumBufferSize];

        soundBite = new SoundBite();
        soundBite.setFrom();

        while (System.currentTimeMillis() < end) {
            int bytesRead = recorder.read(readBuffer, 0, minimumBufferSize);
            dataOut.write(readBuffer, 0, bytesRead);
        }

        recorder.stop();

        soundBite.setTo();
        soundBite.setData(dataOut.toByteArray());
        soundBite.setID();

        return soundBite;
    }
}