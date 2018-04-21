package com.sas.ziyi.studentsattendancesystemapp.entity;

import java.sql.Timestamp;

public class AttendanceEntity {
    private String attendanceId;
    private String checkId;
    private String studentId;
    private int attendanceKind;
    private boolean attendanceValid;
    private Timestamp attendanceTime;

    public AttendanceEntity() {
        super();
    }



    public AttendanceEntity(String attendanceId, String checkId, String studentId,  int attendanceKind,
                            boolean attendanceValid, Timestamp attendanceTime) {
        super();
        this.attendanceId = attendanceId;
        this.checkId = checkId;
        this.studentId = studentId;
        this.attendanceKind = attendanceKind;
        this.attendanceValid = attendanceValid;
        this.attendanceTime = attendanceTime;
    }





    public String getAttendanceId() {
        return attendanceId;
    }



    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }



    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    public int getAttendanceKind() {
        return attendanceKind;
    }

    public void setAttendanceKind(int attendanceKind) {
        this.attendanceKind = attendanceKind;
    }

    public boolean isAttendanceValid() {
        return attendanceValid;
    }

    public void setAttendanceValid(boolean attendanceValid) {
        this.attendanceValid = attendanceValid;
    }

    public Timestamp getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(Timestamp attendanceTime) {
        this.attendanceTime = attendanceTime;
    }



    public String getCheckId() {
        return checkId;
    }



    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }
}
