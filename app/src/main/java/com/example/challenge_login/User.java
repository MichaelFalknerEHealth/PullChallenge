package com.example.challenge_login;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "User")
public class User {

    @ColumnInfo(name = "user_id")
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "user_name")
    String uname;
    @ColumnInfo(name = "user_password")
    String password;
    @ColumnInfo(name = "user_score")
    int score;


@Ignore
    public User() {

    }

    public User(String uname, String password) {
        this.uname = uname;
        this.password = password;
        this.id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUname() {
        return uname;
    }

    public String getPassword() {
        return password;
    }
}
