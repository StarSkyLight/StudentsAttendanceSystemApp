package com.sas.ziyi.studentsattendancesystemapp.entity;

public class ClassEntity {
    private String classId;
    private String className;
    private String classFounderId;

    public ClassEntity(){
        super();
    }

    public ClassEntity(String classId,String className,String classFounderId){
        super();
        this.classId = classId;
        this.className = className;
        this.classFounderId = classFounderId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassFounderId() {
        return classFounderId;
    }

    public void setClassFounderId(String classFounderId) {
        this.classFounderId = classFounderId;
    }

    @Override
    public String toString() {
        return "Class [classId=" + classId + ", className=" + className + ", classFounderId=" + classFounderId + "]";
    }
}
