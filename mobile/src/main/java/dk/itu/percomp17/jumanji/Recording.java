package dk.itu.percomp17.jumanji;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by nataliebrammerjensen on 07/10/2017.
 */

public class Recording {

    private MediaPlayer mediaPLayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;

    public void beginRecording() throws Exception{
        ditchMediaRecorder();
        File outFile = new File(OUTPUT_FILE);

        if (outFile.exists()) {
            outFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(OUTPUT_FILE);

        /*API constraints
        Container	WAV
        Encoding	PCM
        Rate	16K
        Sample Format	16 bit    OK
        Channels	Mono         OK
         */
        recorder.setAudioSamplingRate(16);
        //channel 1 equals mono, 2 eqals stereo
        recorder.setAudioChannels(1);

        recorder.prepare();
        recorder.start();
    }

    public void playVoice() throws IOException {
        ditchMediaPLayer();
        mediaPLayer = new MediaPlayer();
        mediaPLayer.setDataSource(OUTPUT_FILE);
        mediaPLayer.prepare();
        mediaPLayer.start();

    }

    public void ditchMediaPLayer() {
        if (mediaPLayer != null) {
            try{
                mediaPLayer.release();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ditchMediaRecorder() {
        if(recorder != null)
            recorder.release();
    }


}
