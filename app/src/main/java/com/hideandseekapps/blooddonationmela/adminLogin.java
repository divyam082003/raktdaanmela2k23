package com.hideandseekapps.blooddonationmela;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class adminLogin extends AppCompatActivity {

    EditText UserID,Password;
    Button login;

    //FirebaseAuthentication
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Window window = adminLogin.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(adminLogin.this,R.color.red));

        UserID = findViewById(R.id.UserId);
        Password = findViewById(R.id.password);
        login = findViewById(R.id.BTNlogin);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(UserID.getText());
                String password = String.valueOf(Password.getText());

                if(checkCredentialIsEmpty(id,password)){
                    loginUser(id,password);
                }
                closeKeyboard(login);
            }

        });
    }

     boolean checkCredentialIsEmpty(String userID, String password) {
         if (TextUtils.isEmpty(userID) || TextUtils.isEmpty(password)) {
             Toast.makeText(adminLogin.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
             return false;
         } else {
             // Call the login function
             return true;
         }
    }

    private void loginUser(String userID, String password) {
        firebaseAuth.signInWithEmailAndPassword(userID, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful
                            Toast.makeText(adminLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                            // You can navigate to another activity here, for example, the home screen.
                            activityStart(adminLogin.this,RegistrationForm.class);
                            finish();
                        } else {
                            // Login failed
                            Toast.makeText(adminLogin.this, "Login failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void activityStart(Activity a , Class c){
        Intent intent = new Intent(a,c);
        startActivity(intent);
    }

    void closeKeyboard(View view){
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}