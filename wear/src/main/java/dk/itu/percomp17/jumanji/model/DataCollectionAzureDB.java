package dk.itu.percomp17.jumanji.model;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import dk.itu.percomp17.jumanji.network.NetworkStatus;

public final class DataCollectionAzureDB {

    private static DataCollectionAzureDB sAzureDB;

    // init CustomerDB database constants
    private static final String DATABASE_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DATABASE_URL = "jdbc:sqlserver://percomp17.database.windows.net:1433;database=widex-db;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    private static final String USERNAME = "jumanji";
    private static final String PASSWORD = "Azure0183";
    private static final String MAX_POOL = "250";

    private Connection connection = null;
    private Properties properties = null;

    private static final String TAG = "AzureDB";

    // CONSTRUCTOR
    private DataCollectionAzureDB() {
        this.properties = getProperties();
    }

    public static DataCollectionAzureDB getsDataCollectionDB() {
        if (sAzureDB == null) sAzureDB = new DataCollectionAzureDB();
        return sAzureDB;
    }

    private Connection connect() {
        Log.d(TAG, "-connect-");
        if (connection == null) {
            try {
                Log.d(TAG, "-new connection-");
//                Class.forName(DATABASE_DRIVER).newInstance();
                Class.forName(DATABASE_DRIVER);
                this.connection = DriverManager.getConnection(DATABASE_URL, properties);
                boolean isConnectionNull = connection == null;
                Log.d(TAG, "-connection: " + isConnectionNull);

            } catch (ClassNotFoundException | SQLException e) {
                Log.d(TAG, "-connect: ClassNotFoundException-");
                e.printStackTrace();
                return null;
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//                return null;
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//                return null;
            }
        }

        return connection;
    }

    private void disconnect() {
        Log.d(TAG, "-disconnect-");
        if (connection != null) {

            try {
                connection.close();
                connection = null;

            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, "-disconnet: exception-");
            }
        }
    }

    private Properties getProperties() {
        if (connection == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return this.properties;
    }


    // ---------------------------------------------------//
    // PUBLIC METHODS FOR INTERACTING WITH THE DATABASE   //
    // ---------------------------------------------------//

    /**
     * Returns true if ID exists in the db, false if not
     */
    public boolean containsID(String id) throws ExecutionException, InterruptedException {

        Log.d(TAG, "-containsID-");

        String query = "SELECT * FROM jumanji.accData WHERE sessionID ='?'";
        boolean result = false;

        try {
            Class.forName(DATABASE_DRIVER).newInstance();

            try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {

                try (PreparedStatement statement = conn.prepareStatement(query)) {
                    statement.setString(1, id);

                    try {
                        result = statement.executeQuery().next();
                        conn.commit();
                        conn.close();
//                        ResultSet resultSet = statement.executeQuery();

                    } catch (SQLException sqlExecute) {
                        Log.d(TAG, "-SQL Execute exception-");
                        return result;
                    }
                } catch (SQLException sqle) {
                    Log.d(TAG, "-Prepared Statement exception-");
                    return false;
                }
            } catch (SQLException outerSqlEx) {
                Log.d(TAG, "-exception: Could not obtain a valid connection-");
                return false;
            }
        } catch (ClassNotFoundException classNotFound) {
            Log.d(TAG, "-exception: Class not found-");
            return false;
        } catch (InstantiationException e) {
            Log.d(TAG, "-exception: Instantiation Exception-");
            return false;
        } catch (IllegalAccessException e) {
            Log.d(TAG, "-exception: Illegal Access exception-");
            return false;
        }

        return result;
    }


    public String geneterateID() throws ExecutionException, InterruptedException {
        String generatedID = "test";
        if (!containsID(generatedID)) return generatedID;
        else return "";
    }

    public boolean isAvailable() {
        return connect() != null;
    }

    public boolean uploadDataCollection(DataCollection dataCollection) {
        return true;
    }

//    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        DataCollectionAzureDB azureDB = DataCollectionAzureDB.getsDataCollectionDB();
//        azureDB.containsID("test");
//    }
}
