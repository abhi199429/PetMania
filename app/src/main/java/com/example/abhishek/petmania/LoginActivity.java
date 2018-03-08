package com.example.abhishek.petmania;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public static String user = null;
    Button login;
    TextView signup;
    EditText emailText, passwordText;
    ImageView logo;
    boolean loginFlag = false;

    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.btn_login);
        signup = (TextView) findViewById(R.id.link_signup);
        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        signup = (TextView) findViewById(R.id.link_signup);
        logo = (ImageView) findViewById(R.id.logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setAlpha(0f);

        userDatabase = FirebaseDatabase.getInstance().getReference("user");
        final SessionManager sessionDetails = new SessionManager(LoginActivity.this);

        if(sessionDetails.getName()!=""){

            Intent intentTO = new Intent(getApplicationContext(), MapPageActivity.class);
            intentTO.putExtra("Name", sessionDetails.getName());
            startActivity(intentTO);
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        login.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_DayNight_DarkActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.


        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                    User userTemp = userSnapshot.getValue(User.class);

                    if(email.equals(userTemp.getUserEmail()) && password.equals(userTemp.getUserPassword())){
                        loginFlag = true;
                        SessionManager session = new SessionManager(LoginActivity.this);
                        user = userTemp.getUserName();
                        session.setName(user);
                        onLoginSuccess();
                        Toast.makeText(getApplicationContext(), "Welcome " + userTemp.getUserName(), Toast.LENGTH_LONG).show();

                    }

                }


                if(!loginFlag)
                    onLoginFailed();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        login.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MapPageActivity.class);
        intent.putExtra("Name", user);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }



}
