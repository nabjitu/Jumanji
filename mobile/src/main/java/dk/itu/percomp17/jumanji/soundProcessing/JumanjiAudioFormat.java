package dk.itu.percomp17.jumanji.soundProcessing;

import android.media.AudioFormat;

public class JumanjiAudioFormat {

    JumanjiAudioFormat jumanjiAudioFormat;
    public static int ENCODING         = AudioFormat.ENCODING_PCM_16BIT;
    public static int SAMPLE_RATE      = 16000;
    public static int NUM_CHANNELS     = AudioFormat.CHANNEL_IN_MONO;

    private JumanjiAudioFormat() {
    }

    public JumanjiAudioFormat getSoundProcessingSetup() {
        return jumanjiAudioFormat;
    }
}
