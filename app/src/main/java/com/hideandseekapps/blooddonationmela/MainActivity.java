package com.hideandseekapps.blooddonationmela;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView blinkingTextView;
    private boolean isBlinking = false;
    private final Handler handler = new Handler();
    private final Runnable blinkRunnable = new Runnable() {
        @Override
        public void run() {
            if (blinkingTextView.getVisibility() == View.VISIBLE) {
                blinkingTextView.setVisibility(View.INVISIBLE);
            } else {
                blinkingTextView.setVisibility(View.VISIBLE);
            }
            // Continue the blinking by posting the runnable again
            handler.postDelayed(this, 800); // Adjust the interval as needed
        }
    };

    Button admin;
    TextView total,pgi,redcross,other,student,teacher,nonTeacher,alumni,guest,jmit,jmieti,otherCollege;
    TextView liveData,devInfo;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.my_red_color_secondary_variant));

         admin = findViewById(R.id.btAdmin);
         mAuth = FirebaseAuth.getInstance();

         liveData = findViewById(R.id.liveData);
         startBlinking(liveData);
         devInfo = findViewById(R.id.tvDev);
         total = findViewById(R.id.tvTotalRegister);
         pgi = findViewById(R.id.tvPGIRegister);
         redcross = findViewById(R.id.tvRDCRegister);
         other = findViewById(R.id.tvOtherRegister);
         student = findViewById(R.id.tvStudents);
         teacher = findViewById(R.id.tvTeaching);
         nonTeacher = findViewById(R.id.tvNonTeaching);
        alumni = findViewById(R.id.tvAlumni);
        guest = findViewById(R.id.tvGuest);
        jmit = findViewById(R.id.tvJMITregister);
        jmieti = findViewById(R.id.tvJMIETIregister);
        otherCollege = findViewById(R.id.tvOtherCllgregister);



        findViewById(R.id.svData).setVerticalScrollBarEnabled(false);

         admin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (checkIsAlreadySignIn(mAuth)){
                     activityStart(MainActivity.this,RegistrationForm.class);
                 }
                 else {
                     activityStart(MainActivity.this, adminLogin.class);
                 }
             }
         });

         readDataAndCountNodes();

         devInfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 activityStart(MainActivity.this, developerInfo.class);
             }
         });
    }

    boolean checkIsAlreadySignIn(FirebaseAuth mAuth){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            return true;
        } else {
            return false;
        }
    }
    void activityStart(Activity a , Class c){
        Intent intent = new Intent(a,c);
        startActivity(intent);
    }

    private void readDataAndCountNodes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DonorData");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                int istudent = 0;int iTeacher = 0;int iNonTeacher = 0;int iAlumni = 0;int iGuest= 0;
                int iPgi = 0;int iRedCross = 0;int iOther = 0; int ijmit=0; int ijmieti = 0; int iotherCollege=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Model donor;
                    donor = snapshot.getValue(Model.class);
                    //DonorType Count
                    if (donor.getDonorType().equals(FinalStaticStrings.STRING_STUDENT)){
                        student.setText(String.valueOf(++istudent));
                    } else if (donor.getDonorType().equals(FinalStaticStrings.STRING_Teacher)) {
                        teacher.setText(String.valueOf(++iTeacher));
                    }
                    else if (donor.getDonorType().equals(FinalStaticStrings.STRING_Non_Teacher)) {
                        nonTeacher.setText(String.valueOf(++iNonTeacher));
                    }
                    else if (donor.getDonorType().equals(FinalStaticStrings.STRING_Alumni)) {
                        alumni.setText(String.valueOf(++iAlumni));
                    }
                    else if (donor.getDonorType().equals(FinalStaticStrings.STRING_Guest)) {
                        guest.setText(String.valueOf(++iGuest));
                    }

                    //BloodBank Count
                    if (donor.getDonorBloodBank().equals(FinalStaticStrings.STRING_PGI)){
                        pgi.setText(String.valueOf(++iPgi));
                    } else if (donor.getDonorBloodBank().equals(FinalStaticStrings.STRING_RedCross)) {
                        redcross.setText(String.valueOf(++iRedCross));
                    }
                    else if (donor.getDonorBloodBank().equals(FinalStaticStrings.STRING_Others)) {
                        other.setText(String.valueOf(++iOther));
                    }

                    //collegeWise
                    if (donor.getDonorInstituteName().equals(FinalStaticStrings.STRING_JMIT)){
                        jmit.setText(String.valueOf(++ijmit));
                    } else if (donor.getDonorInstituteName().equals(FinalStaticStrings.STRING_JMIETI)) {
                        jmieti.setText(String.valueOf(++ijmieti));
                    } else if (donor.getDonorInstituteName().equals(FinalStaticStrings.STRING_COLLEGE_OTHERS)) {
                        otherCollege.setText(String.valueOf(++iotherCollege));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void startBlinking(TextView textView) {
        if (!isBlinking) {
            isBlinking = true;
            blinkingTextView = textView; // Assign the provided TextView
            handler.post(blinkRunnable);
        }
    }

}