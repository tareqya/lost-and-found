package com.project.lostandfind.Utils;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class User extends Uid implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String password;
    private int score;

    public User(String name, String phone, String email, String password, int score) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.score = score;
    }

    public User(){
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public User setScore(int score) {
        this.score = score;
        return this;
    }

    public int getScore() {
        return score;
    }

}
