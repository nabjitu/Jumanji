package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;

public interface IdentificationStatusListener {

    void onIdentificationResult(IdentificationStatus result, String soundBiteID);

}
