package dk.itu.percomp17.jumanji.testing;

import android.database.Cursor;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.net.SocketPermission;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelperConversation;
import dk.itu.percomp17.jumanji.activities.userAccount.StatisticsActivity;
import dk.itu.percomp17.jumanji.activities.userAccount.login.LoginActivity;
import dk.itu.percomp17.jumanji.toolbox.Toolbox;

/**
 * Created by nataliebrammerjensen on 03/12/2017.
 */

public class TestStatistics {
    DatabaseHelperConversation mDatabaseHelperSB;



    /*
    * Data
    * f3a36b40-e2d8-4ae3-8651-1751da1e715e	03.12.2017	08:35:09	08:35:09
f3a36b40-e2d8-4ae3-8651-1751da1e715e	03.12.2017	08:35:13	08:35:13
f3a36b40-e2d8-4ae3-8651-1751da1e715e	03.12.2017	08:35:20	08:35:20
f3a36b40-e2d8-4ae3-8651-1751da1e715e	03.12.2017	10:54:06	10:54:06
    * */

    public static void main(String[] args) {
        TestStatistics ts = new TestStatistics();

        String azuresID = "f3a36b40-e2d8-4ae3-8651-1751da1e715e";

        try {
            System.out.println("Enters try");
            //=====Copy from statisticsactivity close=====//
            //listview column 1
            ArrayList<String> listOfDatess = new ArrayList<>();
            //ListView column 2
            ArrayList<Integer> lengthsList = new ArrayList<>();

            //Temp variable
            ArrayList<Integer> hashMapEntryVaues = new ArrayList<>();
            try {


                HashMap<String, ArrayList<Integer>> datesAndConversations = ts.newattemptToGetDataFromDB(ts.falseDB(), azuresID);
                HashMap<String, Integer> daysTotal = new HashMap<>();
                for (String day : datesAndConversations.keySet()) {
                    Integer total = 0;
                    ArrayList<Integer> conversations = datesAndConversations.get(day);
                    System.out.println("day: " + day);
                    for (int i : conversations) {
                        total = total + i;
                        daysTotal.put(day, total);
                        System.out.println("total: " + (total/1000)/ 60 + " Sec.");
                    }
                }

                System.out.println("finish try");

                //==========//
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






//        String azIDExample = "f3a36b40-e2d8-4ae3-8651-1751da1e715e";
//        try{
//            ts.attemptToGetDataFromDB(azIDExample);
//
//            ArrayList<String> listOfDatess = new ArrayList<>();
//            try {
//                System.out.println("========VARIABLES==========");
//                Iterator it = ts.attemptToGetDataFromDB(azIDExample).entrySet().iterator();
//                while (it.hasNext()) {
//                    Map.Entry pair = (Map.Entry) it.next();
//                    listOfDatess.add((String) pair.getKey());
//                }
//                for(String ld : listOfDatess){
//                    System.out.println(ld);
//                }
//            } catch(Exception e){
//                e.printStackTrace();
//            }
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }

//    public ArrayList<String> getExamplFrom(){
//        String f1 = "07:35:09";
//        String t1 = "08:35:09";
//        String f2 = "08:35:13";
//        String t2 = "08:35:13";
//        String f3 = "08:35:20";
//        String t3 = "08:35:13";
//        ArrayList<String> froms = new ArrayList<String>();
//        ArrayList<String> tos = new ArrayList<String>();
//        TestStatistics ts = new TestStatistics();
//        froms.add(f1);
//        froms.add(f2);
//        froms.add(f3);
//
//        tos.add(f1);
//        tos.add(f2);
//        tos.add(f3);
//        return froms;
//    }
//    public ArrayList<String> getExamplo(){
//        String f1 = "08:35:09";
//        String t1 = "08:35:09";
//        String f2 = "08:35:13";
//        String t2 = "08:35:13";
//        String f3 = "08:35:20";
//        String t3 = "08:35:13";
//        ArrayList<String> froms = new ArrayList<String>();
//        ArrayList<String> tos = new ArrayList<String>();
//        TestStatistics ts = new TestStatistics();
//        froms.add(f1);
//        froms.add(f2);
//        froms.add(f3);
//
//        tos.add(f1);
//        tos.add(f2);
//        tos.add(f3);
//        return tos;
//    }

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


    public HashMap<String, ArrayList<Integer>> newattemptToGetDataFromDB(String[][] falseDB, String azId) throws Exception{
        System.out.println("GetAllDatesFromDB START");
        HashMap<String, ArrayList<Integer>> dataFromDB = new HashMap<>(); //Dates links to lengths
        ArrayList<Integer> lengthsOnGivenDateAndAzId = new ArrayList<>();
//        Cursor data = mDatabaseHelperSB.getData(); //Kun den der har dette azureId

        for(int i = 0; i<falseDB.length; i++){
//            for(int j = 0; j<falseDB[i].length; j++) {
                //Hvis azId passer så tag alle de rækker der matcher det. og brrug date på disse som keysi mappen.
                String[] variables = new String[4];
                String azureIdfromDb = azId;
                String dateFromDB = null;
                Integer frommFromDB = null;
                Integer tooFromDB = null;
                if (azId.equals(azId)) {
                    System.out.println(falseDB[2][i]);
                    System.out.println(falseDB[2][i]);
                    System.out.println(falseDB[2][i]);
                    System.out.println(falseDB[2][i]);
                    azureIdfromDb = falseDB[i][0];
                    dateFromDB = falseDB[1][i];                     //OK
                    frommFromDB = Integer.parseInt(falseDB[2][i]);
                    tooFromDB = Integer.parseInt(falseDB[3][i]);

                    Integer diff = newsubtract(frommFromDB, tooFromDB);

                    lengthsOnGivenDateAndAzId.add(diff);

                    //Hvis datoen IKKE findes som key så add dato som key og arraylist af length's som value
                    if (!dataFromDB.containsKey(dateFromDB)) {
                        dataFromDB.put(dateFromDB, lengthsOnGivenDateAndAzId);
                    } //Hvis datoen allerede findes osm key, så... hent value'en som er arraylist af length's kopier den over i en lny liste hvor vi adder ny værdi og på ut den ind i mappet igen.
                    else if (dataFromDB.containsKey(dateFromDB)) {
                        //Get the  value list on the key date
                        ArrayList<Integer> oldLstOfLegths = dataFromDB.get(dateFromDB);
                        ArrayList<Integer> newLstOfLegths = new ArrayList<>();
                        newLstOfLegths = oldLstOfLegths;
                        newLstOfLegths.add(diff);
                        dataFromDB.put(dateFromDB, newLstOfLegths);

                    }
//                }
            }
        }
        System.out.println("GetAllDatesFromDB FINISH");
        return dataFromDB;
    }
    public String[][] falseDB(){
        Integer f1 = 558036539;
        Integer t1 = 561308276;
        Date d1 = null;
        Date d2 = null;

        Integer f2 = 558036539;
        Integer t2 = 558400569;
        Date d11 = null;
        Date d22 = null;

        Integer f3 = 558036539;
        Integer t3 = 561308298;;
//        Date d2 = null;
//        Date d1 = null

        Integer f4 = 558036539;
        Integer t4 = 561348287;
//        Date d1 = null;
//        Date d2 = null;

        Integer f5 = 558036539;
        Integer t5 = 561318289;
//        Date d1 =null;
//        Date d2 = null;

        String[][] falsee = new String[5][5];


        falsee[1][0] = "04.12.2017";
        falsee[1][1] = "05.12.2017";
        falsee[1][2] = "03.12.2017";
        falsee[1][3] = "01.12.2017";
        falsee[1][4] = "02.12.2017";

        falsee[2][0] = f1.toString();
        falsee[2][1] = f2.toString();
        falsee[2][2] = f3.toString();
        falsee[2][3] = f4.toString();
        falsee[2][4] = f5.toString();

        falsee[3][0] = t1.toString();
        falsee[3][1] = t2.toString();
        falsee[3][2] = t2.toString();
        falsee[3][3] = t3.toString();
        falsee[3][4] = t4.toString();

        return falsee;




    }

}
