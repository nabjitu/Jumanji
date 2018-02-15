package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract;

public class OperationLocation {

    private String operationLocation;
    private int responseCode;

    public OperationLocation(String operationLocation, int responseCode) {
        this.operationLocation = operationLocation;
        this.responseCode = responseCode;
    }

    public String getOperationLocation() {
        return operationLocation;
    }

    public int getResponseCode() { return this.responseCode; }
}
