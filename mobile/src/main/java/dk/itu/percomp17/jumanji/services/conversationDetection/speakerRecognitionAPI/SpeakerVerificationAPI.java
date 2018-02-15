package dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI;

import android.content.Context;

import com.fasterxml.jackson.core.JsonFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class SpeakerVerificationAPI {
    private final String AzKeyOne = "5a8e7f7c65d44e8f8931b82617a10a5f"; //5a8e7f7c65d44e8f8931b82617a10a5f
    private final String AzKeyTwo = "485d156df74d429da677432090457acb"; //485d156df74d429da677432090457acb

    private Toolbox toolsB = new Toolbox();

    Context context;

    JsonFactory jsonFactory;

    // A CONSTRUCTOR
    public SpeakerVerificationAPI() {
        jsonFactory = new JsonFactory();
    }

    public String createVerificationProfile() {
        System.out.println("===== CREATE VERIFICATION PROFILE  ======");

        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/verificationProfiles");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", AzKeyOne);
            connection.setRequestProperty("name", "daniel-test");

            String body = "{\"locale\":\"en-us\"}";
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(body.getBytes());
            out.flush();
            out.close();

            // Get response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            System.out.println("Response Code: " + connection.getResponseCode());
            String response = Toolbox.readStream(in);
            System.out.println(response);
            System.out.println("===================");

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void getAllVerificationProfiles(){
        System.out.println("=========== get All Verification Profiles =========");
        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/verificationProfiles");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", AzKeyOne);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            String response = Toolbox.readStream(in);

            System.out.println("Response: ");
            System.out.println(response);
            System.out.println("===================================================");

        } catch (IOException e) { e.printStackTrace(); }
    }


    public void enrollForVerification() {
        System.out.println("===== ENROLL FOR VERIFICATION ======");

        try {
            URL url = new URL("https://westus.api.cognitive.microsoft.com/spid/v1.0/verificationProfiles/3c85b448-20db-42ac-9854-1828f1f17134/enroll");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "multipart/form-data");
            connection.setRequestProperty("Ocp-Apim-Subscription-Key", AzKeyOne);
            connection.setRequestProperty("name", "nataliesVerificationStemme");

            String body = "{\"locale\":\"en-us\"}";
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            out.write(Toolbox.toBinary("/Users/nataliebrammerjensen/AndroidStudioProjects/github/widex-social-interaction/android/Jumanji/mobile/src/main/java/dk/itu/percomp17/jumanji/ShortNatalieAcceptedPhrase.wav"));
            out.flush();
            out.close();

            // Get response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            System.out.println("Response Code: " + connection.getResponseCode());
            String response = Toolbox.readStream(in);
            System.out.println(response);
            System.out.println("===================");

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TEST CLIENT
    public static void main(String[] args) {

    }
}
