package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.soundBiteQueue;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;

public interface SoundBiteQueueListener {

    void onEnqueue(Integer soundBiteID);

    void onDequeue(Integer soundBiteID);

    void onIdentificationStatus(Integer soundbiteID, IdentificationStatus identificationStatus);

}
