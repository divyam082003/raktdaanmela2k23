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
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class RegistrationForm extends AppCompatActivity {

    String donorType,donorName,donorInstituteName,donorRollNum,donorBranch,donorYear,
            donorGender,donorHostel,donorBloodGrp,donorBloodBank,donorMobile,donorAddress;

    RadioGroup gender;
    EditText name,institute,rollNumber,branch,mobileNum,address;
    RadioButton male,female;

    Spinner spinnerDonorType,spinnerBloodGroup,spinnerBloodBank,spinnerYear,spinnerHostel;
    Button logOut,submit;

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
        window.setStatusBarColor(ContextCompat.getColor(RegistrationForm.this,R.color.red));

        //firebase instance
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();


        //Scrollview scrollbar Disable
        findViewById(R.id.svRegistration).setVerticalScrollBarEnabled(false);

        //button id
        logOut = findViewById(R.id.btAdminLogOut);
        submit = findViewById(R.id.btSubmit);

        //editText
        name  = findViewById(R.id.etName);
        institute  = findViewById(R.id.etInstituteName);
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
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerHostel = findViewById(R.id.spinnerHostel);
        spinnerBloodGroup = findViewById(R.id.spinnerBloodGroup);
        spinnerBloodBank = findViewById(R.id.spinnerBloodBank);




        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignOutDialog();
            }
        });

        String[] donorTypesOptions = {FinalStaticStrings.STRING_STUDENT,FinalStaticStrings.STRING_Teacher
                ,FinalStaticStrings.STRING_Non_Teacher,
                FinalStaticStrings.STRING_Alumni,FinalStaticStrings.STRING_Guest};

        String[] yearOptions = {"","I", "II", "III", "IV"};
        String[] hostelOptions = {"","Hosteler", "Non-Hosteler"};
        String[] bloodGroupsOptions = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        String[] bloodBankOption = {FinalStaticStrings.STRING_PGI,FinalStaticStrings.STRING_RedCross,FinalStaticStrings.STRING_Others};

        setSpinner(spinnerDonorType,donorTypesOptions);
        setSpinner(spinnerYear,yearOptions);
        setSpinner(spinnerHostel,hostelOptions);
        setSpinner(spinnerBloodGroup,bloodGroupsOptions);
        setSpinner(spinnerBloodBank,bloodBankOption);


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


    Model collectData (){
         donorType = spinnerDonorType.getSelectedItem().toString();
         donorName = name.getText().toString().trim();
         donorInstituteName = institute.getText().toString().trim();
         donorRollNum = rollNumber.getText().toString().trim();
         donorBranch = branch.getText().toString().trim();
         donorYear = spinnerYear.getSelectedItem().toString();
         donorGender = getSelectedRadioButtonText(gender);
         donorHostel = spinnerHostel.getSelectedItem().toString();
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
        institute.setText("");
        rollNumber.setText("");
        branch.setText("");
        mobileNum.setText("");
        address.setText("");
        spinnerYear.setSelection(0);
        spinnerHostel.setSelection(0);
    }

    void addData(Model model){
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


}
