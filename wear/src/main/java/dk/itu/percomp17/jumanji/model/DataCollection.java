package dk.itu.percomp17.jumanji.model;
import java.util.ArrayList;

public class DataCollection  {

    private String id;
    private ArrayList<float[]> accData;
    private ArrayList<float[]> gyroData;

    public DataCollection(String id, ArrayList<float[]> accelerometer_data, ArrayList<float[]> gyroscope_data) {
        this.id = id;
        this.accData = accelerometer_data;
        this.gyroData = gyroscope_data;
    }

    /**
     * Set the uniquie ID for this DataCollection
     * This id can later be used for retrieving the DataCollection from the
     * database. The id is the PrimaryKey.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Add an accelerometer data entry to the DataCollection
     * @param entry
     */
    public void putAccData(float[] entry) {
        this.accData.add(entry);
    }

    /**
     * Add a gyroscope data entry to the DataCollection
     * @param entry
     */
    public void putGyroData(float[] entry) {
        this.gyroData.add(entry);
    }

    /**
     * @return the ID of the DataCollection.
     */
    public String getID() { return this.id; }

    public ArrayList<float[]> getAccData() { return accData; }

    public ArrayList<float[]> getGyroData() { return gyroData; }
}
