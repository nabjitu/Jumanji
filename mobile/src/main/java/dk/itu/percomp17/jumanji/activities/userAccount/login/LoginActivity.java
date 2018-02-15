package dk.itu.percomp17.jumanji.activities.userAccount.login;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dk.itu.percomp17.jumanji.LocalDB;
import dk.itu.percomp17.jumanji.natalleDB.NewCopyRegisterUserActivity;
import dk.itu.percomp17.jumanji.natalleDB.DatabaseHelper;
import dk.itu.percomp17.jumanji.R;
import dk.itu.percomp17.jumanji.UserProfile;
import dk.itu.percomp17.jumanji.activities.userAccount.UserAccountActivity;

public class LoginActivity extends AppCompatActivity {
    LocalDB db = LocalDB.getDB();
    DatabaseHelper dbHelper;
    static UserProfile up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Context context = getApplicationContext();
        dbHelper = new DatabaseHelper(context);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, NewCopyRegisterUserActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final String usernameGiven = etUsername.getText().toString();
                final String passwordGiven = etPassword.getText().toString();
                //DEBUG
//                System.out.println("GIRAF");
//                Cursor data = dbHelper.getData();
//                String azIDFromDB = null;
//                String passwordFromDB = null;
//                String usernameFromDB = null;
//                while(data.moveToNext()){
//                    System.out.println("toiletpapir" + data.getString(0) + " " + data.getString(1)+ " " + data.getString(2));
//                    azIDFromDB = data.getString(0);
//                    usernameFromDB = data.getString(1);
//                    passwordFromDB = data.getString(2);
//                    System.out.println("KRAGE" + data.getString(0) + " " + data.getString(1)+ " " + data.getString(2));
//                }
//                System.out.println(usernameFromDB + " " + passwordFromDB);

                if (authenticate(usernameGiven, passwordGiven)) {
                    //THEN LOGIN
                    System.out.println("BIKSEMAD");
                    //HER ER BUG. DDEN SÆTTER UP TIL DEN SIDTE ENTRY I TABELLEN.
                    setLoggedInCredebtials(getAzueId(usernameGiven, passwordGiven), usernameGiven, passwordGiven);

                    //DEBUG
//                    System.out.println("BIKSEMAD2" + up.getAzProfileID() + ", " + up.getUSername() + ", " + up.getPassword());
//                    System.out.println("BIKSEMAD3" + azIDFromDB + ", " + usernameFromDB + ", " + passwordFromDB);
                    Intent toy = new Intent(LoginActivity.this, UserAccountActivity.class);
                    startActivity(toy);

                }

            }
        });
    }

    public boolean authenticate(String usernameGiven, String passwordGiven){
        Cursor data = dbHelper.getData();
        String passwordFromDB = null;
        String usernameFromDB = null;
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            usernameFromDB = (data.getString(1));
            passwordFromDB = (data.getString(2));

            if(usernameGiven.equals(usernameFromDB)){
                if(passwordGiven.equals(passwordFromDB)){
                    return true;
                }
            } else if(usernameFromDB == null || passwordFromDB == null ) {
                return false;
            }
        }
        return false;
    }

    public String getAzueId(String usernameGiven, String passwordGiven){
        Cursor data = dbHelper.getData();
        String azIdFromDB = null;
        String passwordFromDB = null;
        String usernameFromDB = null;
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            azIdFromDB = (data.getString(0));
            usernameFromDB = (data.getString(1));
            passwordFromDB = (data.getString(2));

            if(usernameGiven.equals(usernameFromDB)){
                if(passwordGiven.equals(passwordFromDB)){
                    return azIdFromDB;
                }
            } else if(usernameFromDB == null || passwordFromDB == null ) {
                return null;
            }
        }
        return null;
    }

//    public boolean passwordMatch(ArrayList<UserProfile> up, String s){
//        for(UserProfile str: up) {
//            if(str.getPassword().equals(s)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean usernameAndPasswordMatch(String usernameGiven, String passwordGiven){
//        Cursor data = dbHelper.getAllMtchingUsernameAndPassword(usernameGiven, passwordGiven);
//        String passwordFromDB = null;
//        String usernameFromDB = null;
//        while(data.moveToNext()){
//            usernameFromDB = data.getString(1);
//            passwordFromDB = data.getString(2);
//        }
//        if(usernameGiven.equals(usernameFromDB)){
//            if(passwordGiven.equals(passwordFromDB)){
//                return true;
//            }
//        } else if(usernameFromDB == null || passwordFromDB == null ) {
//            return false;
//        }
//        return false;
//    }

    public static UserProfile getLoggedInCredentials(){
        return up;
    }

    private void setLoggedInCredebtials(String username, String azureID, String password){
        up = new UserProfile(azureID, username, password);
    }
}

