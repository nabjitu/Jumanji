package dk.itu.percomp17.jumanji;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import dk.itu.percomp17.jumanji.toolbox.Toolbox;
import static android.os.Environment.DIRECTORY_MOVIES;

/**
 * Singleton LocalDB
 */
public class LocalDB implements Serializable {

    private static LocalDB sLocalDB;
    private File mUserFile;

    private LocalDB() {
        try {
            sLocalDB = this;
            this.mUserFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MOVIES), "dbTxtFile.txt");
            if(!mUserFile.exists()) mUserFile.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static LocalDB getDB() {
        if (sLocalDB == null) sLocalDB = new LocalDB();
        return sLocalDB;
    }

    public UserProfile getUser(String userID) {
        HashMap<String, UserProfile> m = loadDB();
        return m.get(userID);
    }


    public void addUser(UserProfile userProfile) {
        HashMap<String, UserProfile> m = loadDB();
        m.put(userProfile.AzProfileID, userProfile);
        saveDB(m);
    }

    public Map<String, UserProfile> getAllUsers(){
        HashMap<String, UserProfile> m = loadDB();
        return m;
    }

    public void removeUser(String userId){
        HashMap<String, UserProfile> m = loadDB();
        m.remove(userId);
        saveDB(m);
    }

    private void saveDB(HashMap<String, UserProfile> tempUserDB) {
        if (tempUserDB != null) {
            if (!tempUserDB.isEmpty()) {

                try {
                    FileOutputStream out = null;
                    byte[] usersAsBytes = Toolbox.serialize(tempUserDB);

                    if (usersAsBytes != null) {
                        out = new FileOutputStream(mUserFile, true);
                        out.write(usersAsBytes);
                        out.close();
                        out.flush();
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    private HashMap<String, UserProfile> loadDB() {
        HashMap<String, UserProfile> users = new HashMap<>();
        FileInputStream in = null;
        try {
            byte[] bytes = Toolbox.readTxtToByteArray(mUserFile);
            Object obj = Toolbox.deserialize(bytes);

            if (obj != null) {
                if (obj.getClass() == users.getClass()) {
                    users = (HashMap<String, UserProfile>) obj; // Maybe this works?
                }
            }

            return users;

        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args){
//        SpeakerIdentificationAPI speakerIdentificationAPI = new SpeakerIdentificationAPI();
//
//        LocalDB localDB = LocalDB.getDB();
//        UserProfile userProfile = new UserProfile("dgot", "ED7183", "admin");
//        localDB.addUser(userProfile);
//
//        UserProfile test = localDB.getUser(userProfile.AzProfileID);
//        System.out.println(test.getAzProfileID());








        /*String azureID = speakerIdentificationAPI.createProfile();
        System.out.println("User profile was created + " + azureID);
        UserProfile up = new UserProfile("un", azureID, "pw");
        //------
        //UserProfile up = new UserProfile("un", "aid", "pw");
        LocalDB db = new LocalDB();
        db.addUser(up);

        for(int i = 0; i < db.users.size(); i++){
            System.out.println();

            for(Object u : db.users){
                System.out.println("ny");
                byte[] b = Toolbox.serialize(u);
                for(byte bb : b){
                    System.out.println(bb);
                }
            }
        }*/

        //Det her virker. Dermed må det være galt der hvor hjeg laver en UserProfile, så den ikke har noget at lave serialize på.
        //Create profile virker også og den printer gene binary af den. Så det må være noget med wirter til txt der går galt.
    }
}
