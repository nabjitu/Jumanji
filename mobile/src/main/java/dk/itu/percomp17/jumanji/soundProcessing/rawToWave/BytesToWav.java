package dk.itu.percomp17.jumanji.soundProcessing.rawToWave;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dk.itu.percomp17.jumanji.toolbox.MyApplication;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

public class BytesToWav {

    private static final String TAG = "BytesToWave";

    public static byte[] bytesToWave(final byte[] rawData) throws IOException {

        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 16000); // sample rate
            writeInt(output, 16000 * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size

            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(rawData);
        } finally {
            if (output != null) {
                output.close();
            }
        }

        return output.toByteArray();
    }

    private static void writeInt(final ByteArrayOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private static void writeShort(final ByteArrayOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private static void writeString(final ByteArrayOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }


    public static File toFile(byte[] waveBytes, String fileName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName + ".wav");
            if (!file.exists()) file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            fileOut.write(waveBytes);
            Toolbox.scanFile(file.getAbsolutePath(), MyApplication.getContext());
            return file;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
