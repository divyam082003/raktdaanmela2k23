package com.hideandseekapps.blooddonationmela;

public class Model {


   private String donorType,donorName,donorInstituteName,
            donorRollNum,donorBranch,donorYear,
            donorGender,donorHostel,donorBloodGrp,
            donorBloodBank,donorMobile,donorAddress;

   public static int Sno = 0;




    public Model(String donorType,
                 String donorName,
                 String donorInstituteName,
                 String donorRollNum, String donorBranch,
                 String donorYear, String donorGender,
                 String donorHostel, String donorBloodGrp,
                 String donorBloodBank, String donorMobile,
                 String donorAddress)
    {
        this.donorType = donorType;
        this.donorName = donorName;
        this.donorInstituteName = donorInstituteName;
        this.donorRollNum = donorRollNum;
        this.donorBranch = donorBranch;
        this.donorYear = donorYear;
        this.donorGender = donorGender;
        this.donorHostel = donorHostel;
        this.donorBloodGrp = donorBloodGrp;
        this.donorBloodBank = donorBloodBank;
        this.donorMobile = donorMobile;
        this.donorAddress = donorAddress;
    }


    public Model() {
    }

    boolean checkData(){
        if(donorType.isEmpty() || donorName.isEmpty() || donorGender.isEmpty()
        || donorBloodGrp.isEmpty() || donorBloodBank.isEmpty() || donorMobile.isEmpty()
        ){
            return false;
        }
        else return true;
    }

    public String getDonorType() {
        return donorType;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getDonorInstituteName() {
        return donorInstituteName;
    }

    public String getDonorRollNum() {
        return donorRollNum;
    }

    public String getDonorBranch() {
        return donorBranch;
    }

    public String getDonorYear() {
        return donorYear;
    }

    public String getDonorGender() {
        return donorGender;
    }

    public String getDonorHostel() {
        return donorHostel;
    }

    public String getDonorBloodGrp() {
        return donorBloodGrp;
    }

    public String getDonorBloodBank() {
        return donorBloodBank;
    }

    public String getDonorMobile() {
        return donorMobile;
    }

    public String getDonorAddress() {
        return donorAddress;
    }


}
