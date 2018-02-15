//Note til mig self.. Både byte array og wave fil er tomme. Det har måske noget at gære med at de bliver oprettet og skrevet til før daataen de skriver er blevet lavet.


package dk.itu.percomp17.jumanji.activities.audioRecorder;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.soundProcessing.rawToWave.RawToWav;

import static android.os.Environment.DIRECTORY_MOVIES;

public class AudioRecorderActivity extends Activity {
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    File audioDataCollection = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "audioDataCollection.csv");

    //Button playRecording = (Button) findViewById(R.id.bPlaySound);
    private MediaPlayer mediaPLayer;
    byte bData[];
    FileOutputStream os = null;

    Context context;

    File pcm = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "proevAzure.pcm");
    File fuckfuckWav = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "ShortNatalieAcceptedPhrase.wav");
    String filePath = pcm.getAbsolutePath();

    public int id = 0;
    File fuckfuck = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES), "ThisIsTheGldenOneForML.txt");
    //private String OUTPUT_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zazuAudio.3gpp";

    String theBytes = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);
        context = getApplicationContext();


        setButtonHandlers();
        enableButtons(false);

        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        /*playRecording.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    playRecording();
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsString = sw.toString();
                    //statusTV.setText("Error");
                }
            }
        });*/
    }

//    private void ditchMediaPLayer() {
//        if (mediaPLayer != null) {
//            try{
//                mediaPLayer.release();
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void playRecording() throws IOException {
//        ditchMediaPLayer();
//        playMp3(bData);
//
//    }
//
//    private void playMp3(byte[] mp3SoundByteArray)
//    {
//        try
//        {
//
//            File path=new File(getCacheDir()+"/musicfile.3gp");
//
//            FileOutputStream fos = new FileOutputStream(path);
//            fos.write(mp3SoundByteArray);
//            fos.close();
//
//            MediaPlayer mediaPlayer = new MediaPlayer();
//
//            FileInputStream fis = new FileInputStream(path);
//            mediaPlayer.setDataSource(getCacheDir()+"/musicfile.3gp");
//
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        }
//        catch (IOException ex)
//        {
//            String s = ex.toString();
//            ex.printStackTrace();
//        }
//    }


    private void setButtonHandlers() {
        ((Button) findViewById(R.id.Bstart)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.Bstop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        ((Button) findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.Bstart, !isRecording);
        enableButton(R.id.Bstop, isRecording);
    }




    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }




    // ==================================    RECORDING LOGIC    ==================================/

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format


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
                scanFile(pcm.getPath());
                recordingThread = null;
            }


            if (!fuckfuckWav.exists()) fuckfuckWav.createNewFile();
            RawToWav.rawToWave(pcm, fuckfuckWav);
            scanFile(fuckfuckWav.getPath());
//            generateHeader();
//            fourthWaveAttempt();

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


    // ==================================    UI CLICK LISTENER    ================================/
    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Bstart: {
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.Bstop: {
                    enableButtons(false);
                    stopRecording();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    // ==================================    FILE GENERATION   ===================================/
    short sData[] = new short[BufferElements2Rec];

    //Når der er lavet en fil så brug den her metode til at pubæicere filen så den kan ses!!
    //Men husk at tilføje "Context context;" uden for metoden.
    // Plus husk at adde "context = getApplicationContext();" i onCreate() metoden.
    public void scanFile(String path) {

        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

//    public void natallesWaveLibraryLAVTILWAVE() {
//        try
//        {
//           // int sampleRate = 44100;		// Samples per second
//
//            double durationmilli = getDuration(fuckfuckWav.getAbsolutePath());		// Milliseconds
//            double duration = durationmilli / 1000;		// Seconds
//
//            // Calculate the number of frames required for specified duration
//            long numFrames = (long)(duration * RECORDER_SAMPLERATE);
//
//            // Create a wav file with the name specified as the first argument
//            //newWavFile(File file, int numChannels, long numFrames, int validBits, long sampleRate)
//            WavFile wavFile = WavFile.newWavFile(fuckfuckWav, 1, numFrames, 16, RECORDER_SAMPLERATE);
//
//            // Create a buffer of 100 frames
//            double[][] buffer = new double[2][100];
//
//            // Initialise a local frame counter
//            long frameCounter = 0;
//
//            // Loop until all frames written
//            while (frameCounter < numFrames)
//            {
//                // Determine how many frames to write, up to a maximum of the buffer size
//                long remaining = wavFile.getFramesRemaining();
//                int toWrite = (remaining > 100) ? 100 : (int) remaining;
//
//                // Fill the buffer, one tone per channel
//                for (int s=0 ; s<toWrite ; s++, frameCounter++)
//                {
//                    buffer[0][s] = Math.sin(2.0 * Math.PI * 400 * frameCounter / RECORDER_SAMPLERATE);
//                    buffer[1][s] = Math.sin(2.0 * Math.PI * 500 * frameCounter / RECORDER_SAMPLERATE);
//                }
//
//                // Write the buffer
//                wavFile.writeFrames(buffer, toWrite);
//            }
//
//            // Close the wavFile
//            wavFile.close();
//            scanFile(fuckfuckWav.getAbsolutePath());
//        }
//        catch (Exception e)
//        {
//            System.err.println(e);
//        }
//    }
//
//    public void thirdWaveAttempt(){
//        Wave w = new Wave();
//        //final File rawFile, final File waveFile
//        try{
//            w.rawToWave(pcm, fuckfuckWav);
//            scanFile(fuckfuckWav.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void fourthWaveAttempt(){
//        FourthWaveLib fl = new FourthWaveLib();
//        //OutputStream os, short[] pcmdata, int srate, int channel, int format
//        try{
//            OutputStream osWave = new FileOutputStream(fuckfuckWav);
//            fl.PCMtoFile(osWave, sData, 44100, 1, 16);
//            scanFile(fuckfuckWav.getAbsolutePath());
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }
//
//    public void generateHeader() {
//        String typeId = "S";
//
//        EditText db = (EditText) findViewById(R.id.dbNoiseText);
//        String dbString = db.getText().toString();
//
//        EditText numPar = (EditText) findViewById(R.id.participatorsText);
//        String npar = numPar.getText().toString();
//
//        EditText conversationBool = (EditText) findViewById(R.id.CoverationText);
//        String cBool = conversationBool.getText().toString();
//
//        SpeakerVerificationAPI asrr = new SpeakerVerificationAPI();
//
//        try {
//            if (!fuckfuck.exists()) {fuckfuck.createNewFile();}
//            FileWriter writer = new FileWriter(fuckfuck, true);
//            byte[] b;
//            try{
//                id++;
//                System.out.println("HEre comes the bytes: " + theBytes);
//                writer.write("\"" + dbString + "\", \"" + npar + "\", \"" + cBool + "\", \"" + typeId + id + "\", \"" + "AudioFilePath:" + filePath + "\", \"" /*+ theBytes*/ + "\" \n");
//
//
//                //second file This is the binary representatio of the audio
//                String name = "Binary_" + typeId + id + ".txt";
//                File binary = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES), name);
//                binary.createNewFile();
//                FileWriter writerBinary = new FileWriter(fuckfuck, true);
//                b = asrr.toBinary(filePath);
//                for (byte data : short2byte(sData)) {
//                    writerBinary.write(data);
//                }
//
//                scanFile(binary.getAbsolutePath());
//
//            } catch(IOException e){
//                e.printStackTrace();
//            }
//
//            writer.flush();
//            writer.close();
//            scanFile(fuckfuck.getAbsolutePath());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //https://stackoverflow.com/questions/15394640/get-duration-of-audio-file
//    public int getDuration(String pathStr){
//        Uri uri = Uri.parse(pathStr);
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        mmr.setDataSource(context, uri);
//        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        int millSecond = Integer.parseInt(durationStr);
//        return millSecond;
//    }
//
//    public File getWaveFile(){
//        return fuckfuckWav;
//    }
}
