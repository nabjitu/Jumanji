package dk.itu.percomp17.jumanji.natalleDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "profl_table"; //profiles
    private static final String COL1 = "ID"; //Ugh.. Her skal det være azureID
    private static final String COL2 = "name";
    private static final String COL3 = "password";

    //ConversationEvent
    private static final String TABLE_NAME2 = "conversation_table"; //profiles
    private static final String T2COL1 = "ID"; //Ugh.. Her skal det være azureID
    private static final String T2COL2 = "date";
    private static final String T2COL3 = "time";
    private static final String T2COL4 = "length";


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.print("KANEL");
        //String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 +" TEXT)";
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID TEXT PRIMARY KEY, " + COL2 + " TEXT, " + COL3 + " TEXT " + ")";
        db.execSQL(createTable);

//        String createTable2 = "CREATE TABLE " + TABLE_NAME2 + " (ID STRING PRIMARY KEY, " + T2COL2 + " TEXT, " + T2COL3 + " TEXT " + T2COL4 + " REAL " +")";
//        System.out.print(createTable2);
//        db.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
//        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    public boolean addData(String itemCOL1, String itemCOL2, String itemCOL3) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, itemCOL1);
        contentValues.put(COL2, itemCOL2);
        contentValues.put(COL3, itemCOL3); //Her kan det gå galt pga. to content values

        Log.d(TAG, "addData: Adding " + itemCOL1 + itemCOL2 + "and " + itemCOL3 +  " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

//    public boolean addDataToConversationTable(String itemCOL1, String itemCOL2, String itemCOL3, String itemCOL4) {
//        SQLiteDatabase db = this.getWritableDatabase(); //Det kunne godt vlre noget rod at der  er to db
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(T2COL1, itemCOL1);
//        contentValues.put(T2COL2, itemCOL2);
//        contentValues.put(T2COL3, itemCOL3); //Her kan det gå galt pga. to content values
//
//        Log.d(TAG, "addData: Adding " + itemCOL1 + itemCOL2 + "and " + itemCOL3 + "and " + itemCOL4 +  " to " + TABLE_NAME2);
//
//        long result = db.insert(TABLE_NAME2, null, contentValues);
//
//        //if date as inserted incorrectly it will return -1
//        if (result == -1) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +
                " = '" + newName + "' WHERE " + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to " + newName);
        db.execSQL(query);
    }

    /**
     * Delete from database
     * @param id
     * @param name
     */
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + COL1 + " = '" + id + "'" +
                " AND " + COL2 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + name + " from database.");
        db.execSQL(query);
    }

    public Cursor getAllMtchingUsernameAndPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + "*" + " FROM " + TABLE_NAME +
                " WHERE " + COL1 + " = '" + username + "' AND " + COL2 + " = '" + password + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    ////////////TEBLE 2 STUFF/////////////
//    public Cursor getAllConversationData(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_NAME2;
//        Cursor data = db.rawQuery(query, null);
//        return data;
//    }
//
//    public Cursor getAllConversationDataFromUser(String azureid){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT " + " * " + " FROM " + TABLE_NAME2 + " WHERE " + COL1 + " = '" + azureid + "'";
//        Cursor data = db.rawQuery(query, null);
//        return data;
//    }
}


