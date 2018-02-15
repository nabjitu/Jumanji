package dk.itu.percomp17.jumanji.activities.userAccount.registerUser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.io.PrintWriter;
import java.io.StringWriter;

import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;
import dk.itu.percomp17.jumanji.services.conversationDetection.speakerRecognitionAPI.SpeakerIdentificationAPI;
import dk.itu.percomp17.jumanji.soundProcessing.audioRecorder.AudioRecorder;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class RegisterVoiceActivity extends Activity {

    public Button bstart;
    public Button bFinish;
    Context context;

    String pathOfWav = "";

    private static final int REQUEST_INTERNET_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.INTERNET};

    AudioRecorder arecorder;
    //SpeakerIdentificationAPI speakerIdentificationAPI = new SpeakerIdentificationAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_voice);

        arecorder = new AudioRecorder();

        bstart = findViewById(R.id.button_record_voice);
        bFinish = findViewById(R.id.bSubmit_voice);

        // På start knap
        // Optag lyd
        bstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    arecorder.startRecording();
                    Toolbox.doToast("Recording", getApplicationContext());
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    Toolbox.doToast("Error", getApplicationContext());
                }
            }
        });

        //På finish knap
        //Kald asrRequest på API'en med wave

        bFinish.setOnClickListener(new View.OnClickListener() {
            public static final String TAG = "OperationLocation";

            @Override
            public void onClick(View view) {
                arecorder.stopRecording();
                if (pathOfWav.isEmpty()) pathOfWav = arecorder.getWavFilePath();
                Toolbox.doToast("Stopped recording", getApplicationContext());
                Toolbox.doToast("Enrolling your voice", getApplicationContext());
                final String operationLocation = SpeakerIdentificationAPI.enrollForIdentification(pathOfWav, LoginActivity.getLoggedInCredentials().getAzProfileID());
                Log.d(TAG, "OperationLocation: " + operationLocation);

//                    Runnable checkForCompletion = new Runnable() {
//                        @Override
//                        public void run() {
//                            String status = "";
//                            while (!status.equals("enrolled")) {
//                                IdentificationStatus identificationStatus = SpeakerIdentificationAPI.getIdentificationStatus(operationLocation);
//                                if (identificationStatus.getMessage().contains("fail")) {
//                                    Toolbox.doToast("Enrollment failed", getApplicationContext());
//                                    break;
//                                } else status = identificationStatus.getEnrollmentStatus();
//                            }
//
//                            Toolbox.doToast("Enrollment completed", getApplicationContext());
//                        }
//                    };
//                    checkForCompletion.run();

                Toolbox.doToast("Voice added for enrollment", getApplicationContext());
            }
        });
    }
}
