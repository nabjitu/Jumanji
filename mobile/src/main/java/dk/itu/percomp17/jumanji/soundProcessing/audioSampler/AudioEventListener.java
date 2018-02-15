package dk.itu.percomp17.jumanji.soundProcessing.audioSampler;

public interface AudioEventListener {

    /**
     * Is called when an AudioEvent has taken place.
     * E.g. when the AudioSampler has created a SoundBite.
     * @see AudioSampler
     * @param soundBite
     */
    void onAudioEvent(SoundBite soundBite);
}
