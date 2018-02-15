package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract;

import com.fasterxml.jackson.databind.JsonNode;

public class IdentificationStatus {

    /**
     * The status of the operation:
     *   notstarted: The operation is not started.
     *   running: The operation is running.
     *   failed: The operation is finished and failed.
     *   succeeded: The operation is finished and succeeded.
     */
    private String status = "";

    /**
     * Created date of the operation.
     */
    private String createdDateTime = "";

    /**
     * Last date of usage for this operation.
     */
    private String lastActionDateTime = "";

    /**
     *  Detail message returned by this operation.
     *  Used in operations with failed status to show detail failure message.
     */
    private String message = "";

    /**
     * Speaker identification profile enrollment status:
     *   Enrolling: profile is currently enrolling and is not ready for identification.
     *   Training:  profile is currently training and is not ready for identification.
     *   Enrolled:  profile is currently enrolled and is ready for identification.
     */
    private String enrollmentStatus = "";

    /**
     * Speaker identification profile enrollment length in seconds of speech.
     */
    private double enrollmentSpeechTime = -1.0;

    /**
     * Remaining number of speech seconds to complete minimum enrollment.
     */
    private double remainingEnrollmentSpeechTime = -1.0;

    /**
     * Seconds of useful speech in enrollment audio.
     */
    private double speechTime = -1.0;


    /**
     * The identified speaker identification profile id.
     * If this value is 00000000-0000-0000-0000-000000000000,
     * it means there's no speaker identification profile identified
     * and the audio file to be identified belongs to none of the
     * provided speaker identification profiles.
     */
    private String identificationProfileId = "";

    /**
     * The confidence value of the identification.
     *   Low: The confidence of the identification is low.
     *   Normal: The confidence of the identification is normal.
     *   High: The confidence of the identification is high.
     */
    private String confidence = ""; // [Low | Normal | High]

    // CONSTRUCTOR
    public IdentificationStatus(JsonNode json) {
        if (json != null) {
            // STANDARD FIELDS
            this.status                             = json.has("status") ? json.get("status").asText() : "";
            this.createdDateTime                    = json.has("createdDateTime") ? json.get("createdDateTime").asText() : "";
            this.lastActionDateTime                 = json.has("lastActionDateTime") ? json.get("lastActionDateTime").asText() : "";
            this.message                            = json.has("message") ? json.get("message").asText() : "";

            if (!json.has("processingResult")) return;

            // ENROLLMENT RESULT
            if (json.get("processingResult").has("enrollmentStatus")) {
                this.enrollmentStatus               = json.get("processingResult").get("enrollmentStatus").asText();
                this.remainingEnrollmentSpeechTime  = json.get("processingResult").get("remainingEnrollmentSpeechTime").asDouble();
                this.speechTime                     = json.get("processingResult").get("speechTime").asDouble();
                this.enrollmentSpeechTime           = json.get("processingResult").get("enrollmentSpeechTime").asDouble();
            }

            // IDENTIFICATION RESULT
            if (json.get("processingResult").has("identifiedProfileId")) {
                this.identificationProfileId        = json.get("processingResult").get("identificationProfileId").asText();
                this.confidence                     = json.get("processingResult").get("confidence").asText();
            }
        }
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public String getLastActionDateTime() {
        return lastActionDateTime;
    }

    public String getMessage() {
        return message;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public double getRemainingEnrollmentSpeechTime() {
        return remainingEnrollmentSpeechTime;
    }

    public double getSpeechTime() {
        return speechTime;
    }

    public double getEnrollmentSpeechTime() {
        return enrollmentSpeechTime;
    }

    public String getIdentificationProfileId() {
        return identificationProfileId;
    }

    public String getConfidence() {
        return confidence;
    }

    public void   printAllFields() {
        System.out.println("Status: " + getStatus());
        System.out.println("CreatedDateTime: " + getCreatedDateTime());
        System.out.println("LastActionDateTime: " + getLastActionDateTime());
        System.out.println("Message: " + getMessage());
        System.out.println("EnrollmentStatus: " + getEnrollmentStatus());
        System.out.println("RemainingEnrollmentSpeechTime: " + getRemainingEnrollmentSpeechTime());
        System.out.println("SpeechTime: " + getSpeechTime());
        System.out.println("EnrollmentSpeechTime: " + getEnrollmentSpeechTime());
        System.out.println("IdentificationProfileId: " + getIdentificationProfileId());
        System.out.println("confidence: " + getConfidence());
    }
}