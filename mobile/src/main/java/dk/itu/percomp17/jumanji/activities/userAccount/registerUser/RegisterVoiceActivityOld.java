package dk.itu.percomp17.jumanji.activities.userAccount.registerUser;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import dk.itu.percomp17.jumanji.R;
public class RegisterVoiceActivityOld extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_voice);


        Button button = (Button) findViewById(R.id.button_record_voice);
        button.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        mHandler.post(mRecord);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(mRecord);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mRecord = new Runnable() {
                @Override public void run() {
                    AudioRecord recorder;
                    AudioFormat format;


                    System.out.println("Performing Record action...");
                    mHandler.postDelayed(this, 500);
                }
            };

        });



    }
}
