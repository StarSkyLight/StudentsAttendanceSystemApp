package com.sas.ziyi.studentsattendancesystemapp.entity;

public class TeacherEntity {
    private String teacherId;
    private String teacherName;
    private boolean teacherSex;
    private String teacherSchool;
    private String teacherEmail;

    public TeacherEntity(){
        super();
    }

    public TeacherEntity(String teacherId,String teacherName,boolean teacherSex,
                         String teacherSchool,String teacherEmail){
        super();
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherSex = teacherSex;
        this.teacherSchool = teacherSchool;
        this.teacherEmail = teacherEmail;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public boolean isTeacherSex() {
        return teacherSex;
    }

    public void setTeacherSex(boolean teacherSex) {
        this.teacherSex = teacherSex;
    }

    public String getTeacherSchool() {
        return teacherSchool;
    }

    public void setTeacherSchool(String teacherSchool) {
        this.teacherSchool = teacherSchool;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    @Override
    public String toString() {
        return "Teacher [teacherId=" + teacherId + ", teacherName=" + teacherName + ", teacherSex=" + teacherSex
                + ", teacherSchool=" + teacherSchool + ", teacherEmail=" + teacherEmail + "]";
    }
}
