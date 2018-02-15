package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.OperationLocation;

public interface OperationLocationListener {

    void onOperationLocationResult(OperationLocation operationLocation, String soundBiteID);

}
