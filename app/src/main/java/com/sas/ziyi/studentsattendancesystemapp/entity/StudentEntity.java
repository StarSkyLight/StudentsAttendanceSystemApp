package com.sas.ziyi.studentsattendancesystemapp.entity;

public class StudentEntity {
    private String studentId;
    private String studentName;
    private boolean studentSex;
    private String studentSchool;
    private String studentNumber;
    private String studentEmail;

    public StudentEntity(){
        super();
    }

    public StudentEntity(String studentId,String studentName,boolean studentSex,
                         String studentSchool,String studentNumber,String studentEmail){
        super();
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentSex = studentSex;
        this.studentSchool = studentSchool;
        this.studentNumber = studentNumber;
        this.studentEmail = studentEmail;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public boolean isStudentSex() {
        return studentSex;
    }

    public void setStudentSex(boolean studentSex) {
        this.studentSex = studentSex;
    }

    public String getStudentSchool() {
        return studentSchool;
    }

    public void setStudentSchool(String studentSchool) {
        this.studentSchool = studentSchool;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    @Override
    public String toString() {
        return "Student [studentId=" + studentId + ", studentName=" + studentName + ", studentSex=" + studentSex
                + ", studentSchool=" + studentSchool + ", studentNumber=" + studentNumber + ", studentEmail="
                + studentEmail + "]";
    }
}
