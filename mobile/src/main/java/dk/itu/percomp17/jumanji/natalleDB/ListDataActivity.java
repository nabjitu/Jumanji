package dk.itu.percomp17.jumanji.natalleDB;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import dk.itu.percomp17.jumanji.R;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

//    DatabaseHelper mDatabaseHelper;
    DatabaseHelperSoundbite mDatabaseHelperSB;

    private ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_layout);
        mListView = (ListView) findViewById(R.id.listView);
//        mDatabaseHelper = new DatabaseHelper(this);
        mDatabaseHelperSB = new DatabaseHelperSoundbite(this);

        populateListView();
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = mDatabaseHelperSB.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(0));
            listData.add(data.getString(1));
            listData.add(data.getString(2));
            listData.add(data.getString(3));
        }

        /*listData.add("HERE COMES COL2");
        Cursor data2 = mDatabaseHelper.getColumn2();
        while(data2.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            //listData.add(data.getString(1));
            System.out.println("COLOUMN2");
            System.out.println(data2.getString(1));
        }*/

        //create the list adapter and set the adapter
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

//        //set an onItemClickListener to the ListView
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String name = adapterView.getItemAtPosition(i).toString();
//                Log.d(TAG, "onItemClick: You Clicked on " + name);
//
//                Cursor data = mDatabaseHelperSB.getItemID(name); //get the id associated with that name
//                int itemID = -1;
//                while(data.moveToNext()){
//                    itemID = data.getInt(0);
//                }
//                if(itemID > -1){
//                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
//                    Intent editScreenIntent = new Intent(ListDataActivity.this, EditDataActivity.class);
//                    editScreenIntent.putExtra("id",itemID);
//                    editScreenIntent.putExtra("name",name);
//                    startActivity(editScreenIntent);
//                }
//                else{
//                    toastMessage("No ID associated with that name");
//                }
//            }
//        });
    }

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
