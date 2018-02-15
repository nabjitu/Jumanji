package dk.itu.percomp17.jumanji.soundProcessing.audioSampler;

import java.io.Serializable;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.OperationLocation;

public class SoundBite implements Serializable {

    private byte[] data;

    private int from;

    private int to;

    private int id;

    private OperationLocation operationLocation;

    /**
     * Initializes an empty SoundBite object.
     * Use the setters to fill it with data.
     */
    public SoundBite() {
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return Raw sound data as bytes stored with the SoundBite
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * This method saves the current time and stores it as the From date.
     */
    public void setFrom() {
        this.from = (int) System.currentTimeMillis();
    }

    /**
     * This method saves the current time and stores it as the To date.
     */
    public void setTo() {
        this.to = (int) System.currentTimeMillis();
    }

    public void setID() {
        this.id = from + to;
    }

    public Integer getID() {
        return this.id;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public OperationLocation getOperationLocation() {
        if (operationLocation == null) return new OperationLocation("", 404);
        else return operationLocation;
    }

    public void setOperationLocation(OperationLocation operationLocation) {
        this.operationLocation = operationLocation;
    }
}
