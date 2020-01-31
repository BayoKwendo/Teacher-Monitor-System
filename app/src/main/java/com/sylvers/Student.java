package com.sylvers;

public class Student {

    // Data Values.
    private String Fullame;
    private String teacherNo;
    private String email;
    private String attendance;

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    // Getter Setters.
    public String getName() {
        return Fullame;
    }

    public void setName(String name) {
        this.Fullame = name;
    }

    public String getRollNo() {
        return teacherNo;
    }

    public void setRollNo(String rollNo) {
        this.teacherNo = rollNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Constructors.
    public Student(){
        attendance = "p";
    }
}
