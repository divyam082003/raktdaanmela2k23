package com.hideandseekapps.blooddonationmela;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class RegistrationForm extends AppCompatActivity {

    String donorType,donorName,donorInstituteName,donorRollNum,donorBranch,donorYear,
            donorGender,donorHostel,donorBloodGrp,donorBloodBank,donorMobile,donorAddress;

    RadioGroup gender;
    TextInputEditText name,rollNumber,branch,mobileNum,address;
    RadioButton male,female;

    Spinner spinnerDonorType,spinnerInstitute,spinnerBloodGroup,spinnerBloodBank,spinnerYear,spinnerHostel;
    Button logOut,submit,resetPassword;
    TextView AdminInfo;

    FirebaseAuth auth;
    FirebaseDatabase database;
    String uid;
    private DatabaseReference databaseReference;
    Model model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        Window window = RegistrationForm.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(RegistrationForm.this,R.color.my_red_color_secondary_variant));

        //firebase instance
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        database = FirebaseDatabase.getInstance();


        //Scrollview scrollbar Disable
        findViewById(R.id.svRegistration).setVerticalScrollBarEnabled(false);

        //button id
        logOut = findViewById(R.id.btAdminLogOut);
        submit = findViewById(R.id.btSubmit);
        resetPassword = findViewById(R.id.btAdminResetPassword);
        AdminInfo = findViewById(R.id.tvAdminInfo);

        //editText
        name  = findViewById(R.id.etName);
        rollNumber  = findViewById(R.id.etRollCode);
        branch  = findViewById(R.id.etBranch);
        mobileNum  = findViewById(R.id.etMobileNum);
        address  = findViewById(R.id.etAddress);

        //radioGroup and button
        gender = findViewById(R.id.rgGender);
        male = findViewById(R.id.rbMale);
        female = findViewById(R.id.rbFemale);

        //spinners
        spinnerDonorType = findViewById(R.id.spinnerDonorType);
        spinnerInstitute = findViewById(R.id.spinnerInstitute);
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerHostel = findViewById(R.id.spinnerHostel);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        spinnerBloodBank = findViewById(R.id.spinnerBloodBank);

        //setting admin info
        AdminInfo.setText("Hi, "+email);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutDialog();
            }
        });

        //resetPassword
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetPasswordDialog();
            }
        });

        String[] donorTypesOptions = {FinalStaticStrings.STRING_STUDENT,FinalStaticStrings.STRING_Teacher
                ,FinalStaticStrings.STRING_Non_Teacher,
                FinalStaticStrings.STRING_Alumni,FinalStaticStrings.STRING_Guest};

        String[] instituteOptions = {"-",FinalStaticStrings.STRING_JMIT,
                FinalStaticStrings.STRING_JMIETI,
                FinalStaticStrings.STRING_COLLEGE_OTHERS};

        String[] yearOptions = {"-","I", "II", "III", "IV"};
        String[] hostelOptions = {"-","Hosteler", "Non-Hosteler"};
        String[] bloodGroupsOptions = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        String[] bloodBankOption = {FinalStaticStrings.STRING_PGI,FinalStaticStrings.STRING_RedCross,FinalStaticStrings.STRING_Others};

        setSpinner(spinnerDonorType,donorTypesOptions);
        setSpinner(spinnerYear,yearOptions);
        setSpinner(spinnerHostel,hostelOptions);
        setSpinner(spinnerBloodGroup,bloodGroupsOptions);
        setSpinner(spinnerBloodBank,bloodBankOption);
        setSpinner(spinnerInstitute,instituteOptions);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model = collectData();
                if (model.checkData()) {
                    addData(model);
                    clearFields();
                }
                else Toast.makeText(RegistrationForm.this,"Enter All Mandatory(*) fields",Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log Out");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sign out the user
                auth.signOut();
                finish();
                // You can also navigate to the login screen or perform other actions here.
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showResetPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("Are you sure you want to reset your password ?\n(Password reset link will be send to your registered email)");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Sign out the user
                setResetPassword();
                // You can also navigate to the login screen or perform other actions here.
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    Model collectData (){
         donorType = spinnerDonorType.getSelectedItem().toString();
         donorName = name.getText().toString().trim();
         donorInstituteName = checkSpinnerNullValue(spinnerInstitute);
         donorRollNum = rollNumber.getText().toString().trim();
         donorBranch = branch.getText().toString().trim();
         donorYear = checkSpinnerNullValue(spinnerYear);
         donorGender = getSelectedRadioButtonText(gender);
         donorHostel = checkSpinnerNullValue(spinnerHostel);
         donorBloodGrp = spinnerBloodGroup.getSelectedItem().toString();
         donorBloodBank = spinnerBloodBank.getSelectedItem().toString();
         donorMobile = mobileNum.getText().toString().trim();
         donorAddress = address.getText().toString().trim();

         Model model = new Model(donorType,donorName,donorInstituteName,donorRollNum,donorBranch,donorYear,
                 donorGender,donorHostel,donorBloodGrp,donorBloodBank,donorMobile,donorAddress);

         return model;
    }

    private String getSelectedRadioButtonText(RadioGroup radioGroup) {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            return selectedRadioButton.getText().toString();
        } else {
            return "";
        }
    }


    void setSpinner(Spinner spinner, String []Array){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Array);
        spinner.setAdapter(adapter);
    }

    void clearFields(){
        name.setText("");
        rollNumber.setText("");
        branch.setText("");
        mobileNum.setText("");
        address.setText("");
        spinnerYear.setSelection(0);
        spinnerHostel.setSelection(0);
    }

    void addData(Model model){
        Date currentDateAndTime = new Date();
        HashMap<String,Object> m = new HashMap <String,Object> ();
        m.put(FinalStaticStrings.DONOR_TYPE, model.getDonorType());
        m.put(FinalStaticStrings.DONOR_NAME, model.getDonorName());
        m.put(FinalStaticStrings.DONOR_INSTITUTE_NAME, model.getDonorInstituteName());
        m.put(FinalStaticStrings.DONOR_ROLL_NUM, model.getDonorRollNum());
        m.put(FinalStaticStrings.DONOR_BRANCH, model.getDonorBranch());
        m.put(FinalStaticStrings.DONOR_YEAR, model.getDonorYear());
        m.put(FinalStaticStrings.DONOR_GENDER, model.getDonorGender());
        m.put(FinalStaticStrings.DONOR_HOSTEL, model.getDonorHostel());
        m.put(FinalStaticStrings.DONOR_BLOOD_GRP, model.getDonorBloodGrp());
        m.put(FinalStaticStrings.DONOR_BLOOD_BANK, model.getDonorBloodBank());
        m.put(FinalStaticStrings.DONOR_MOBILE, model.getDonorMobile());
        m.put(FinalStaticStrings.DONOR_ADDRESS, model.getDonorAddress());
        m.put(FinalStaticStrings.ADMIN_ID, uid);
        m.put(FinalStaticStrings.TIME_STAMP, currentDateAndTime.toString());
        FirebaseDatabase.getInstance().getReference()
                .child("DonorData")
                .push()
                .setValue(m)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegistrationForm.this,"Data Submitted Successfully",Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(RegistrationForm.this,"Data Not Submitted",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void setResetPassword(){
        auth.sendPasswordResetEmail(auth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Toast.makeText(RegistrationForm.this,
                        "Password Reset Email Sent to: "+ auth.getCurrentUser().getEmail(),
                        Toast.LENGTH_LONG ).show();
                else  Toast.makeText(RegistrationForm.this,
                        "Error Occured",
                        Toast.LENGTH_LONG ).show();
            }
        });
    }

    String checkSpinnerNullValue(Spinner spinnerYear){
        if (spinnerYear.getSelectedItem().toString().equals("-")) return "";
        else return spinnerYear.getSelectedItem().toString();
    }
}
