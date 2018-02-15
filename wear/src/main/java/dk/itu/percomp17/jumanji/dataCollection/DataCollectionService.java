package dk.itu.percomp17.jumanji.dataCollection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.itu.percomp17.jumanji.model.DataCollectionLocalDB;

/**
 * https://developer.android.com/reference/android/hardware/SensorEvent.html
 * A sensor of this type measures the acceleration applied to the device (Ad). Conceptually,
 * it does so by measuring forces applied to the sensor itself (Fs) using the relation:
 *
 * Ad = - ∑Fs / mass
 *
 * In particular, the force of gravity is always influencing the measured acceleration:
 *
 * Ad = -g - ∑F / mass
 *
 * For this reason, when the device is sitting on a table (and obviously not accelerating),
 * the accelerometer reads a magnitude of g = 9.81 m/s^2
 *
 * Similarly, when the device is in free-fall and therefore dangerously accelerating towards to
 * ground at 9.81 m/s^2, its accelerometer reads a magnitude of 0 m/s^2.
 * It should be apparent that in order to measure the real acceleration of the device,
 * the contribution of the force of gravity must be eliminated.
 *
 * This can be achieved by applying a high-pass filter.
 * Conversely, a low-pass filter can be used to isolate the force of gravity.
 */
public class DataCollectionService extends Service {

    //Debug Tags
    private static final String TAG = "DataCollectionService";

    // Local Data Collection Database
    private DataCollectionLocalDB sDataCollectionDB = DataCollectionLocalDB.getDataBase();

    // Session ID for this DataCollection
    String dataCollectionID = "";

    // Acc & Gyro Sensor
    private SensorManager mSensorManager;

    private Sensor mAccSensor;
    private Handler mAccSensorHandler;
    private HandlerThread mAccSensorThread;
    private AccelerometerListener mAccListener;

    private Sensor mGyroSensor;
    private Handler mGyroSensorHandler;
    private HandlerThread mGyroSensorThread;
    private GyroScopeListener mGyroListener;

    private PowerManager pm;
    private PowerManager.WakeLock wl;

    // Output Streams
    File accOutFile;
    CSVWriter accWriter;

    File gyroOutFile;
    CSVWriter gyroWriter;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //Debug
        Log.d(TAG, "-onStartCommand-");

        // Get DataCollectionID to collect data parsed with the Intent that started the service
        dataCollectionID = intent.getExtras().getString("dataCollectionID");
        Log.d(TAG, "-intent.getExtras: id = " + dataCollectionID + "-");

        // Get or create our sensor output files
        File storage = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        String fileType = ".csv";

        String accFileName = dataCollectionID + "_acc";
        accOutFile = new File(storage, accFileName + fileType);

        String gyroFileName = dataCollectionID + "_gyro";
        gyroOutFile = new File(storage, gyroFileName + fileType);

        try {
            // Setup output to files matching current dataCollection ID.
            // If an output file cannot be found for a DataCollection ID, create a new file.
            if (!accOutFile.exists()) {
                accOutFile.createNewFile();
                Log.d(TAG, "-file @ path: " + accOutFile.getAbsolutePath());
            }

            if (!gyroOutFile.exists()) {
                gyroOutFile.createNewFile();
                Log.d(TAG, "-file @ path: " + gyroOutFile.getAbsolutePath());
            }

            // Initialize our output writers
            accWriter = new CSVWriter(new FileWriter(accOutFile, true));
            gyroWriter = new CSVWriter(new FileWriter(gyroOutFile, true));

        } catch (IOException e) {
            e.printStackTrace();
        }


        // ========================= Setup Power Management ====================== //
        // Acquire WakeLock to keep the screen on
        // Used as a hack for keeping the accelerometer running while the service is active
        Log.d(TAG, "-Acquiring wakelock-");
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DataCollectionService WakeLog");
        wl.acquire();
        Log.d(TAG, "-Wakelock acquired = " + wl.isHeld() + "-");  //Debug



        // Setup threads for the sensors
        mAccSensorThread = new HandlerThread("Acc Sensor Thread", Thread.MAX_PRIORITY);
        mAccSensorThread.start();
        mAccSensorHandler = new Handler(mAccSensorThread.getLooper());

        mGyroSensorThread = new HandlerThread("Gyro Sensor Thread", Thread.MAX_PRIORITY);
        mGyroSensorThread.start();
        mGyroSensorHandler = new Handler(mGyroSensorThread.getLooper());

        // Get sensors and register sensor listener
        Log.d(TAG, "-Creating Sensor Manager and Sensor Listener-");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccListener = new AccelerometerListener();

        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroListener = new GyroScopeListener();

        // Register Sensor Listener along with thread
        mSensorManager.registerListener(mAccListener,  mAccSensor,  SensorManager.SENSOR_DELAY_NORMAL, mAccSensorHandler);
        mSensorManager.registerListener(mGyroListener, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL, mGyroSensorHandler);

        // Restart the service if it is destroyed by the system
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "-onDestroy-");

        // Release resources
        try {
            mSensorManager.unregisterListener(mAccListener);
            wl.release();

            if (accWriter != null) {
                accWriter.flush();
                accWriter.close();
            }

            if (gyroWriter != null) {
                gyroWriter.flush();
                gyroWriter.close();
            }

//        // When service is stopped, save logged data as a DataCollection and add it to the DataBase.
//        DataCollection dataCollection = new DataCollection(dataCollectionID, accData, gyroData);
//        sDataCollectionDB.putDataCollection(dataCollectionID, dataCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Private class implementation of the SensorEventListener for Acceleroemter Data
     * Listens for changes in sensors and writes data to local dataCollection.
     */
    private class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "-AccSensorListener: onSensorChanged-");

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // All values are in SI units (m/s^2)
                // values[0]: Acceleration !minus Gravity on the x-axis
                // values[1]: Acceleration !minus Gravity on the y-axis
                // values[2]: Acceleration !minus Gravity on the z-axis
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                String t = new SimpleDateFormat("dd:MM:hh:mm:ss:SSS").format(new Date(System.currentTimeMillis()));

                // CSV Output
                String strReadings =
                                dataCollectionID + ";"  +
                                String.valueOf(x) + ";" +
                                String.valueOf(y) + ";" +
                                String.valueOf(z) + ";" + t;

                accWriter.writeNext(strReadings.split(";"));

                // Publish latest readings
                float[] readings = {x, y, z};
                sDataCollectionDB.publishLatestReadings(readings);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }


    /**
     * Private class implementation of the SensorEventListener for GyroScope Data
     * Listens for changes in sensors and writes data to local dataCollection.
     */
    private class GyroScopeListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                // values[0] Rate of rotation around the x axis. rad/s
                // values[1] Rate of rotation around the y axis. rad/s
                // values[2] Rate of rotation around the z axis. rad/s
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                String t = new SimpleDateFormat("dd:MM:hh:mm:ss:SSS").format(new Date(System.currentTimeMillis()));

                // CSV Output
                String strReadings =
                                dataCollectionID + ";"  +
                                String.valueOf(x) + ";" +
                                String.valueOf(y) + ";" +
                                String.valueOf(z) + ";" + t;

                gyroWriter.writeNext(strReadings.split(";"));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}


// TODO: Use JobScheduler to register for when wear enters Doze state, for Doze-safe code execution
// https://developer.android.com/training/wearables/data-layer/network-access.html#waiting-for-network-availability
// You should schedule jobs with the JobScheduler API, which enables your app to register for
// Doze-safe code execution. When scheduling jobs, you can select constraints such as periodic
// execution and the need for connectivity or device charging. Configure jobs in a way that does
// not adversely impact battery life. Jobs should use a JobInfo.Builder object to provide
// constraints and metadata, e.g. with one or more of the following methods for a task:
