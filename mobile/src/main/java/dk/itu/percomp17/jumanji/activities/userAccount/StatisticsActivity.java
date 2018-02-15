package dk.itu.percomp17.jumanji.activities.userAccount;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperConversation;
import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperSoundbite;
import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;


public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    //    DatabaseHelper mDatabaseHelper;
    DatabaseHelperSoundbite mDatabaseHelperSB;
    DatabaseHelperConversation mDatabaseHelper;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mListView = (ListView) findViewById(R.id.listView);
//        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelperSB = new DatabaseHelperSoundbite(this);

        populateListView();
    }

    private void populateListView() {
        String azuresID = LoginActivity.getLoggedInCredentials().getAzProfileID();

        //listview column 1
        ArrayList<String> days = new ArrayList<>();
        //ListView column 2
        ArrayList<String> totals = new ArrayList<>();

        System.out.println("Before try");
        try {
            System.out.println("Enters try");

            //Temp variable
            ArrayList<Integer> hashMapEntryVaues = new ArrayList<>();
            try {
                HashMap<String, ArrayList<Integer>> datesAndConversations = newattemptToGetDataFromDB(azuresID);
                HashMap<String, Integer> daysTotal = new HashMap<>();
                for (String day : datesAndConversations.keySet()) {
                    Integer total = 0;
                    ArrayList<Integer> conversations = datesAndConversations.get(day);
                    System.out.println("day: " + day);
                    days.add(day);
                    for (int i : conversations) {
                        total = total + i;
                        daysTotal.put(day, (total / 1000)/60);
                        System.out.println("total: " + (total / 1000)/60 + " Sec.");
                    }
                    Integer totall = (newaddAllLengthsForOneDate(conversations) / 1000)/60;
                    totals.add(totall.toString());

                    System.out.println("total: " + (newaddAllLengthsForOneDate(conversations) / 1000)/60 + " Sec.");
//
//                    totals.add(total.toString());
                }

                System.out.println("finish try");

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("catch 1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cathch 2");
        }


        //======List 1========//
        ListAdapter buckysAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, totals);
        ListView buckysListView = (ListView) findViewById(R.id.listView);
        buckysListView.setAdapter(buckysAdapter);
        //===LIst 2 Nested===//
        ListAdapter nestedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, days);
        ListView nestedView = (ListView) findViewById(R.id.listView_nested);
        nestedView.setAdapter(nestedAdapter);
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    public int getWeekNumber(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    public Date stringToDate(String d)throws Exception{
        String string = d; //January 2, 2010
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy"/*"MMMM d, yyyy", Locale.ENGLISH*/);
        Date date = format.parse(string);       //Unparseable date: "117:11:2"
        System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
        return date;
    }

    public long subtract(Date from, Date to){
        long totaltemp = to.getTime() - from.getTime();
        return totaltemp;
    }
    public long addTwoLengths(long d1, long d2)throws Exception{
        long result = d1 + d2;
        return result;
    }
    public String addAllLengthsForOneDate(ArrayList<String> lengths) throws Exception{
        String result = null;
        long tempResult = 0;
        long d1 = 0;
        long d2 = 0;
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        for(int i = 0; i < lengths.size(); i++){
            Date date1 = timeFormat.parse(lengths.get(i));
            d1 = date1.getTime();
            Date date2 = null;
            if(i<lengths.size()-1){
                date2 = timeFormat.parse(lengths.get(i+1));
                d2 = date2.getTime();
            }

            tempResult = tempResult + addTwoLengths(d1, d2);
        }
        return millisecToDate(tempResult);
    }

    //Den her burde virke!!!!!!!
    /*
    * Loads in data from daabase
    * Creates map with dates as unique kwys on the gives azure ID.
    * Values is an ayyarlist on all the legths of the conversations.
    * */
    public HashMap<String, ArrayList<String>> attemptToGetDataFromDB(String azId) throws Exception{
        System.out.println("GetAllDatesFromDB START");
        HashMap<String, ArrayList<String>> dataFromDB = new HashMap<>(); //Dates links to lengths
        ArrayList<String> lengthsOnGivenDateAndAzId = new ArrayList<>();
        Cursor data = mDatabaseHelperSB.getData(); //Kun den der har dette azureId

        while(data.moveToNext()){
            //Hvis azId passer så tag alle de rækker der matcher det. og brrug date på disse som keysi mappen.
            String[] variables = new String[4];
            String azureIdfromDb = null;
            String dateFromDB = null;
            Integer frommFromDB = null;
            Integer tooFromDB = null;
            if(data.getString(0).equals(azId)){
                azureIdfromDb = data.getString(0);
                dateFromDB = data.getString(1);
                frommFromDB = data.getInt(2);
                tooFromDB = data.getInt(3);
                Date from = stringToTime(frommFromDB);
                Date to = stringToTime(tooFromDB);

                long diff = subtract(from, to);
                String conLength = millisecToDate(diff);

                lengthsOnGivenDateAndAzId.add(conLength);

                //Hvis datoen IKKE findes som key så add dato som key og arraylist af length's som value
                if(!dataFromDB.containsKey(dateFromDB)){
                    dataFromDB.put(dateFromDB, lengthsOnGivenDateAndAzId);
                } //Hvis datoen allerede findes osm key, så... hent value'en som er arraylist af length's kopier den over i en lny liste hvor vi adder ny værdi og på ut den ind i mappet igen.
                else if(dataFromDB.containsKey(dateFromDB)){
                    ArrayList<String> oldLstOfLegths = dataFromDB.get(dateFromDB);
                    ArrayList<String> newLstOfLegths = new ArrayList<>();
                    newLstOfLegths = oldLstOfLegths;
                    newLstOfLegths.add(conLength);
                }
            }
        }
        System.out.println("GetAllDatesFromDB FINISH");
        return dataFromDB;
    }

    public Date stringToTime(Integer d)throws Exception{
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss"/*"MMMM d, yyyy", Locale.ENGLISH*/);
//        Date date = format.parse(d); //Unparseable date: "558036539" HER ER PORBLEMET
//        System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010

        //NEW attempt
//        Integer value = 19000101;
        int year = d / 10000;
        int month = (d % 10000) / 100;
        int day = d % 100;
        Date date = new GregorianCalendar(year, month, day).getTime();
        return date;
    }

    public String millisecToDate(long millisec)throws Exception{
        Date date = new Date(millisec);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }

    public void TimeTosString(String d)throws Exception{
    }

    public int newgetWeekNumber(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }

    public Date newstringToDate(String d)throws Exception{
        String string = d; //January 2, 2010
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy"/*"MMMM d, yyyy", Locale.ENGLISH*/);
        Date date = format.parse(string);       //Unparseable date: "117:11:2"
        System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010
        return date;
    }

    public Integer newsubtract(Integer from, Integer to){
        Integer totaltemp = to - from;
        return totaltemp;
    }
//    public String addLengths(Integer d1, Integer d2){
//        Integer result = d1 + d2;
//        return result.toString();
//    }

    //NY
    public Date newstringToTime(Integer d)throws Exception{
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss"/*"MMMM d, yyyy", Locale.ENGLISH*/);
//        Date date = format.parse(d); //Unparseable date: "558036539" HER ER PORBLEMET
//        System.out.println(date); // Sat Jan 02 00:00:00 GMT 2010

        //NEW attempt
//        Integer value = 19000101;
        int year = d / 10000;
        int month = (d % 10000) / 100;
        int day = d % 100;
        Date date = new GregorianCalendar(year, month, day).getTime();
        return date;
    }

    public String newmillisecToDate(long millisec)throws Exception{
        Date date = new Date(millisec);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }

    public Integer newaddTwoLengths(Integer d1, Integer d2)throws Exception{
        Integer result = d1 + d2;
        return result;
    }

    //NY
    public Integer newaddAllLengthsForOneDate(ArrayList<Integer> lengths) throws Exception{
        Integer tempResult = 0;
        for(int i = 0; i < lengths.size(); i++){
            if(i<lengths.size()-1){
                tempResult = tempResult + newaddTwoLengths(lengths.get(i), lengths.get(i+1));
            }
        }
        return tempResult;
    }


    public HashMap<String, ArrayList<Integer>> newattemptToGetDataFromDB( String azId) throws Exception{
        System.out.println("GetAllDatesFromDB START");
        HashMap<String, ArrayList<Integer>> dataFromDB = new HashMap<>(); //Dates links to lengths
        ArrayList<Integer> lengthsOnGivenDateAndAzId = new ArrayList<>();
        Cursor data = mDatabaseHelperSB.getData(); //Kun den der har dette azureId

        while(data.moveToNext()){
//            for(int j = 0; j<falseDB[i].length; j++) {
            //Hvis azId passer så tag alle de rækker der matcher det. og brrug date på disse som keysi mappen.
            String[] variables = new String[4];
            String azureIdfromDb = azId;
            String dateFromDB = null;
            Integer frommFromDB = null;
            Integer tooFromDB = null;
            if (azId.equals(azId)) {
                System.out.println(data.getString(0));
                System.out.println(data.getString(1));
                System.out.println(data.getString(2));
                System.out.println(data.getString(3));
                azureIdfromDb = data.getString(0);
                dateFromDB = data.getString(1);
                frommFromDB = data.getInt(2);
                tooFromDB = data.getInt(3);

                //hertil

                Integer diff = newsubtract(frommFromDB, tooFromDB);

                lengthsOnGivenDateAndAzId.add(diff);

                //Hvis datoen IKKE findes som key så add dato som key og arraylist af length's som value
                if (!dataFromDB.containsKey(dateFromDB)) {
                    dataFromDB.put(dateFromDB, lengthsOnGivenDateAndAzId);
                } //Hvis datoen allerede findes osm key, så... hent value'en som er arraylist af length's kopier den over i en lny liste hvor vi adder ny værdi og på ut den ind i mappet igen.
                else if (dataFromDB.containsKey(dateFromDB)) {
                    ArrayList<Integer> oldLstOfLegths = dataFromDB.get(dateFromDB);
                    ArrayList<Integer> newLstOfLegths = new ArrayList<>();
                    newLstOfLegths = oldLstOfLegths;
                    newLstOfLegths.add(diff);
                }
//                }
            }
        }
        System.out.println("GetAllDatesFromDB FINISH");
        return dataFromDB;
    }

}

