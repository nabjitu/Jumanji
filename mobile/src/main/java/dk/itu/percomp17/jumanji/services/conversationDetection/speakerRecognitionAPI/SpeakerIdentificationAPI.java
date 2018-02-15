package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.contract.IdentificationStatus;
import dk.itu.percomp17.jumanji.soundProcessing.rawToWave.BytesToWav;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

/**
 *  Microsoft Cognitive Services, Speaker Identification API Documentation:
 *  https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c3271984551c84ec6797
 *
 *  Radio-Power-Consumption: Every time you initiate a connection—irrespective of the size of the associated
 *  data transfer—you potentially cause the radio to draw power for nearly 20 seconds
 *  when using a typical 3G wireless radio.
 *
 *  https://developer.android.com/training/efficient-downloads/efficient-network-access.html
 */
public class SpeakerIdentificationAPI {

//    private static final String TAG = "SpeakerAPI";

    // Subscription Keys
    private static final String subscriptionKeyOne = "5a8e7f7c65d44e8f8931b82617a10a5f"; //5a8e7f7c65d44e8f8931b82617a10a5f
    private static final String subscriptionKeyTwo = "485d156df74d429da677432090457acb"; //485d156df74d429da677432090457acb

    public SpeakerIdentificationAPI() {}

    /*
     * Creates an Azure profile for Identification
     * @return String, an azure cognitive service user ID for the created profile
     */
    public static String createProfile() {
        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKeyOne);

            String body = "{\"locale\":\"en-us\"}";
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(body.getBytes());
            out.flush();
            out.close();

            // Get response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String response = Toolbox.readStream(in);
            int responseCode = connection.getResponseCode();
//            Log.d(TAG, "CreateProfile() response : " + response);
//            System.out.println("CreateProfile() response : " + responseCode);
            connection.disconnect();

            // Get Identification ID from the Json object
            if (responseCode == 200) {
                final ObjectNode node = new ObjectMapper().readValue(response, ObjectNode.class);
                if (node.has("identificationProfileId"))
                    return node.get("identificationProfileId").asText();
            }

        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * @param filePath
     * @param identificationID
     * @return OperationLocation URL as a String
     */
    public static String enrollForIdentification(String filePath, String identificationID) {
        System.out.println("===== ENROLL FOR IDENTIFICATION ======");

        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/identificationProfiles/566d0f71-21f1-42ab-849f-ec22933b8cf5/enroll?shortAudio=true");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0); // We do not know the length of the body.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "multipart/form-data");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKeyOne);
            connection.setRequestProperty("name", "Daniel Gottlieb Dollerup");
            connection.setRequestProperty("identificationProfileId", identificationID);

            String body = "{\"locale\":\"en-us\"}";
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());

            // Create Bod
            out.write(Toolbox.toBinary(filePath));
            out.flush();
            out.close();

            // Get response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String response = Toolbox.readStream(in);
            System.out.println(response);
            int responseCode = connection.getResponseCode();
            String message = connection.getResponseMessage();
            String operationLocation = null;
            if (responseCode == 202 && message.contains("Accepted")) {
                operationLocation = connection.getHeaderField("Operation-Location");
            }

            connection.disconnect();

            System.out.println("OperationLocation " + operationLocation);
            return operationLocation;

        } catch (IOException e) {
            System.out.println("Could not read file");
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized String identification(byte[] data) {
        System.out.println("===== IDENTIFICATION  ======");

        HttpURLConnection connection = null;
        String operationLocation = null;

        try {

            String p1 = "566d0f71-21f1-42ab-849f-ec22933b8cf5";
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds=" + p1 + "&shortAudio=true");
            connection = (HttpURLConnection) url.openConnection();
            byte[] waveData = BytesToWav.bytesToWave(data);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(waveData.length);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKeyOne);
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(waveData);
            connection.connect();
            out.flush();
            out.close();

            // Get response
            int responseCode = connection.getResponseCode();
            String message = "message = " + connection.getResponseMessage();
            System.out.println("Response Code" + responseCode);

            if (responseCode == 202 && message.contains("Accepted")) {
//                Log.d(TAG, "Accepted");
                operationLocation = connection.getHeaderField("Operation-Location");
//                Log.d(TAG, "OP " + operationLocation);
                System.out.println("Operation-Location = " + operationLocation);
            }

            //Status
            System.out.println("===================");

            connection.disconnect();

            return operationLocation;

        } catch (IOException e) {
            if (connection != null) connection.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized String identificationFromFile(String f) {
        System.out.println("===== IDENTIFICATION  ======");

        HttpURLConnection connection = null;
        String operationLocation = null;

        try {

            String p1 = "566d0f71-21f1-42ab-849f-ec22933b8cf5";
//            String p2 = "d988573c-f7de-4073-aeef-6bebce38f991";
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/identify?identificationProfileIds=" + p1 + "&shortAudio=true");
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            byte[] waveData = Toolbox.toBinary(f);
            connection.setChunkedStreamingMode(waveData.length);

            connection.setRequestProperty("Content-Type", "multipart/form-data");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKeyOne);

            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(waveData);
            out.flush();
            out.close();

            // Get response
            int responseCode = connection.getResponseCode();
            String message = "message = " + connection.getResponseMessage();
            System.out.println("Response Code" + responseCode);

            if (responseCode == 202 && message.contains("Accepted")) {
//                Log.d(TAG, "Accepted");
                operationLocation = connection.getHeaderField("Operation-Location");
//                Log.d(TAG, "OP " + operationLocation);
                System.out.println("Operation-Location = " + operationLocation);
            }

            //Status
            System.out.println("===================");

            connection.disconnect();

            return operationLocation;

        } catch (IOException e) {
            if (connection != null) connection.disconnect();
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Azure Cognitive Service, Get Operation Status:
     * https://westus.dev.cognitive.microsoft.com/docs/services/563309b6778daf02acc0a508/operations/5645c725ca73070ee8845bd6
     *
     * @param operationsURL, operations location URL provided by the webservice.
     * @return IdentificationStatus object containing the results.
     */
    public static IdentificationStatus getIdentificationStatus(String operationsURL) {

        HttpURLConnection connection = null;
        IdentificationStatus identificationStatus = null;

        try {
            URL url = new URL(operationsURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKeyOne);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                JsonNode json = new ObjectMapper().readTree(connection.getInputStream());
                identificationStatus = new IdentificationStatus(json);
            }

            connection.disconnect();
            return identificationStatus;

        } catch (IOException e) {
            if (connection != null) connection.disconnect();
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Test Client for the Azure API
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        // Files
//        String profileID = SpeakerIdentificationAPI.createProfile();
//        System.out.println(profileID);

//        String[] profiles = { "1291337d-50c1-498b-b886-c509b34e20c9", "14b017e9-9540-4077-87ab-ec5d256dffd9" };
//        String profileID = "2f4b1c21-a8a2-49dc-a208-d23f9cb87deb";
//        String profileIDTWo = "12b229c8-8de7-4d7b-b892-95d9d85f44fb";
//        String profileThree = "566d0f71-21f1-42ab-849f-ec22933b8cf5"; //This one is enrolled with daniels Macbook
        String enroll_file = "/Users/dgot/Documents/development/itu/third-semester/widex-social-interaction/android/Jumanji/mobile/src/main/java/dk/itu/percomp17/jumanji/ressources/humanMixedVoices.wav";
//        String enroll_id = "fc5c7fc9-d3fe-4ef4-b7b0-c7e2f6511fde";
//        System.out.println(enroll_id);
// File file = new File(enroll_file);
//        String operationLocation = identificationFromFile(enroll_file);
        String operationLocation = identificationFromFile(enroll_file);
        System.out.println(operationLocation);

    }
}



// Profile ID That is enrolled: 566d0f71-21f1-42ab-849f-ec22933b8cf5
// This ID can be used for identification.
// User: Daniel