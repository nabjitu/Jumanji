package dk.itu.percomp17.jumanji.toolbox;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Toolbox{

    public static void doToast(String message, Context ctx){
        Context context = ctx;
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //Når der er lavet en fil så brug den her metode til at pubæicere filen så den kan ses!!
    //Men husk at tilføje "Context context;" uden for metoden.
    // Plus husk at adde "context = getApplicationContext();" i onCreate() metoden.
    public static void scanFile(String path, Context context) {

        MediaScannerConnection.scanFile(context,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    public static String retrieveWebPage(String webUrl) throws IOException {
        String output;
        URL url = new URL(webUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            output = readStream(in);
        } finally {
            urlConnection.disconnect();
        }
        return output;
    }

    //https://stackoverflow.com/questions/8376072/whats-the-readstream-method-i-just-can-not-find-it-anywhere
    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static byte[] toBinary(String f) throws IOException {
        byte[] fileData = new byte[(int) f.length()];
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(f));
            dis.readFully(fileData);
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }
    public static byte[] toBinaryTwo(File waveFile) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(waveFile));

        int read;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();
        return audioBytes;
    }
    //https://stackoverflow.com/questions/16574482/decoding-json-string-in-java
    public static String JSONdecoder(String json/*, String object1*/, String stringinObject)throws org.json.JSONException{
        String jsonString = json;
        JSONObject jsonObject = new JSONObject(json);
        //JSONObject newJSON = jsonObject.getJSONObject(object1);
        //System.out.println(newJSON.toString());
        //jsonObject = new JSONObject(newJSON.toString());
        System.out.println(jsonObject.getString(stringinObject));
        return jsonObject.getString(stringinObject);
        //System.out.println(jsonObject.getJSONArray("argv"));
    }

    /*    try{
            /*Map json = (Map)parser.parse(jsonString, containerFactory);
            Iterator iter = json.entrySet().iterator();
            System.out.println("==iterate result==");
            Object entry = json.get("stat");
            System.out.println(entry);
        }
    }*/

    // ==============================  OBJECT SERIALIZATION  ================================ //
    public static byte[] serialize(Object obj) {
        try {
            // Serialize Message Object
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(buffer);

            serializer.writeObject(obj);
            serializer.flush();
            serializer.close();

            return buffer.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object deserialize(byte[] byteObject) {
        try {
            // Deserialize object
            ByteArrayInputStream buffer = new ByteArrayInputStream(byteObject);
            ObjectInputStream objectInputStream = new ObjectInputStream(buffer);
            Object object = objectInputStream.readObject();
            return object;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object deserializeFromFile(File file) {
        Object object = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            object = objectInputStream.readObject();
            return object;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void serializeToFile(Object object, File file, boolean append) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(file, append);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            byte[] objectAsBytes = serialize(object);
            if (objectAsBytes != null) objectOutput.write(objectAsBytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readTxtToByteArray(File file) throws IOException {

        ByteArrayOutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            byte[] buffer = new byte[4096];
            outputStream = new ByteArrayOutputStream();
            inputStream = new FileInputStream(file);
            int read = 0;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
            }

            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
            }
        }
        return outputStream.toByteArray();
    }

    public static String getTime(){
//        Date dt = new Date();
//        int hours = dt.getHours();
//        int minutes = dt.getMinutes();
//        int seconds = dt.getSeconds();
//        String curTime = hours + ":" + minutes + ":" + seconds;
//        return curTime;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = df.format(c.getTime());
        return formattedTime;
    }

    public static long getTimeLong(){
        long millis = new java.util.Date().getTime();
        return millis;
    }

    public static String getDate(){
//        Date dt = new Date();
//        int date = dt.getDate();
//        int month = dt.getMonth();
//        int year = dt.getYear();
//        String d = year + ":" +  month + ":"+ date;
//        return d;
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void main(String[] args){
        System.out.println("KAGE");
        System.out.println(getTime());
        System.out.println(getDate());
    }
}




