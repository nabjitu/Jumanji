package dk.itu.percomp17.jumanji.activities.userAccount.registerUser;

import android.support.v7.app.AppCompatActivity;

public class RegisterUserActivity extends AppCompatActivity {
//    Toolbox tb = new Toolbox();
//    public Button GoToRegisterVoice;
//    LocalDB ldb = LocalDB.getDB();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register);
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
//        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
//        final Button bRegister = (Button) findViewById(R.id.bRegister);
//
//        MyPermissions mp = new MyPermissions();
//        bRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String username = etUsername.getText().toString();
//                final String password = etPassword.getText().toString();
//
//                SpeakerIdentificationAPI speakerIdentificationAPI = new SpeakerIdentificationAPI();
//
//
//                //TEST get user
//                String azureID = speakerIdentificationAPI.createProfile();
//                System.out.println("User profile was created + " + azureID);
//                tb.doToast("User profile was created + " + azureID, getApplicationContext());
//
//                UserProfile up = new UserProfile(username, azureID, password);
//                ldb.addUser(up);
//
//                Map<String, UserProfile> users = ldb.getAllUsers();
//                for(String userID : users.keySet()) {
//                    UserProfile userProfile = users.get(userID);
//                        System.out.print("NEW USER ID");
//                        System.out.print(userProfile.getAzProfileID());
//                }
//                System.out.print("DEN NYE ER: " + azureID);
//
//                //TEST SLUT
//                //System.out.print("0th user is:" + ldb.getUserList().get(0).getUSername() + ldb.getUserList().get(0).getUSername());
//
//
//               /* Response.Listener<String> responseListener = new Response.Listener<String>(){
//
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonResponse = new JSONObject(response);
//                            boolean success = jsonResponse.getBoolean("succes");
//
//                            if(success) {
//                                Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
//                                RegisterUserActivity.this.startActivity(intent);
//                            } else {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterUserActivity.this);
//                                builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
//                            }
//                        } catch(JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//                RegisterRequest registerRequest = new RegisterRequest(name, username, age, password, responseListener);
//                RecognitionJobScheduler queue = Volley.newRequestQueue(RegisterUserActivity.this);
//                queue.add(registerRequest);*/
//               /*HERfor(UserProfile ups : ldb.getUserList()){
//                   System.out.println(ups);
//               }*/
//
//            }
//        });
//
//        initRegisterVoice();
//    }
//
//    public void initRegisterVoice() {
//        GoToRegisterVoice = (Button) findViewById(R.id.regVoiceB);
//        GoToRegisterVoice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toy = new Intent(RegisterUserActivity.this, RegisterVoiceActivity.class);
//                startActivity(toy);
//            }
//        });
//    }
}
