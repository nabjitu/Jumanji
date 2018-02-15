//Note til mig self.. Både byte array og wave fil er tomme. Det har måske noget at gære med at de bliver oprettet og skrevet til før daataen de skriver er blevet lavet.


package dk.itu.percomp17.jumanji.soundProcessing.audioRecorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import dk.itu.percomp17.jumanji.soundProcessing.rawToWave.RawToWav;
import dk.itu.percomp17.jumanji.toolbox.MyApplication;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

import static android.os.Environment.DIRECTORY_MOVIES;

public class AudioRecorder {
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    // Button playRecording = (Button) findViewById(R.id.bPlaySound);
    private File pcm = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "proevAzure.pcm");
    private File waveFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "zazuAaudiorecorder.wav");
    public int id = 0;

    // ==================================    RECORDING LOGIC    ==================================/

    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format

    public void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }


    public void stopRecording() {
        try {
            // stops the recording activity
            if (null != recorder) {
                isRecording = false;
                recorder.stop();
                recorder.release();
                recorder = null;
                Toolbox.scanFile(pcm.getPath(), MyApplication.getContext());

                recordingThread = null;
            }

            if (!waveFile.exists()) waveFile.createNewFile();
            RawToWav.rawToWave(pcm, waveFile);
            Toolbox.scanFile(getWavFilePath(), MyApplication.getContext());

        } catch (IOException e) { e.printStackTrace(); }
    }


    private void writeAudioDataToFile() {
        String stringReadBuffer = "";
        int buffersize = BufferElements2Rec * BytesPerElement;

        try {

            if (!pcm.exists()) pcm.createNewFile();
            FileOutputStream output = new FileOutputStream(pcm);

            while (isRecording) {
                // Read sound input from the recorder object
                byte[] readBuffer = new byte[buffersize];
                recorder.read(readBuffer, 0, buffersize);

                // Write buffered data from the recorder object
                output.write(readBuffer, 0, buffersize);
                stringReadBuffer += readBuffer;
            }

            System.out.println("Finished" + "\n" + stringReadBuffer);

            // Release Ressources!
            output.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public String getWavFilePath(){
        return waveFile.getAbsolutePath();
    }
}
