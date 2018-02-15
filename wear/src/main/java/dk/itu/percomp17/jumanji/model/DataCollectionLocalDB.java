package dk.itu.percomp17.jumanji.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DataCollectionLocalDB Singleton to store data collected from the accelerometer.
 * E.g. a new entry has been added to the DataCollection.
 */
public final class DataCollectionLocalDB {

    private static final String TAG = "DataCollectionLocalDB";
    private static DataCollectionLocalDB sDataCollection;
    private HashMap<String, DataCollection> dataCollections;
    private float[] latestReadings;

    private DataCollectionLocalDB() {
        dataCollections = new HashMap<>();
    }

    public void putDataCollection(String id, DataCollection dataCollection) {
        if (dataCollections.containsKey(id)) appendDataCollection(id, dataCollection.getAccData());
        else this.dataCollections.put(id, dataCollection);
    }

    private void appendDataCollection(String id, ArrayList<float[]> data) {
        DataCollection dataCollection = dataCollections.get(id);
        for (float[] entry : data) dataCollection.putAccData(entry);
    }

    public DataCollection getDataCollection(String id) {
        if (dataCollections.containsKey(id)) return dataCollections.get(id);
        else return null;
    }

    public boolean containsDataCollection(String id) {
        return dataCollections.containsKey(id);
    }

    public HashMap<String, DataCollection> getDataCollections() {
        return dataCollections;
    }

    public void publishLatestReadings(float[] data) {
        latestReadings = data;
        Log.d(TAG, "-publishLatestReadings-");
    }

    public synchronized float[] getLatestReadings() {
        if (this.latestReadings != null)  return this.latestReadings;
        else return new float[4];
    }

    public static DataCollectionLocalDB getDataBase() {
        if (sDataCollection == null) { sDataCollection = new DataCollectionLocalDB(); }
        return sDataCollection;
    }
}
