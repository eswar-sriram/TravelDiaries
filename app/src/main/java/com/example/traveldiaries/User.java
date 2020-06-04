package com.example.traveldiaries;


public class User {
    private String username,email,age,gender,mobile,state;
    public User(){}
    public User(String username,String email, String age, String gender, String mobile, String state) {
        this.username=username;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.state = state;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
