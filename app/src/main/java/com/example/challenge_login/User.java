package com.example.challenge_login;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


/**
 * Entity-Klasse repräsentiert einen User in der Datenbank
 * Diese Klasse wird mit Room verwendet
 *
 */
@Entity(tableName = "User")
public class User {

    @ColumnInfo(name = "user_id")
    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "user_name")
    String uname;
    @ColumnInfo(name = "user_password")
    String password;



@Ignore
    public User() {
    }

    /**
     * Konstruktor erstellt neuen User mit spezifischem Usernamen und Passwort
     * @param uname Der Username für den User
     * @param password Das Passwort für den User
     */
    public User(String uname, String password) {
        this.uname = uname;
        this.password = password;
        this.id = 0;
    }

    /**
     * Getter Methode für ID
     * @return id die ID für den User
     */
    public int getId() {
        return id;
    }

    /**
     * Setter Methode für ID
     * @param id die ID für den User
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter Methode für ID
     * @param uname den Usernamen für den User
     */
    public void setUname(String uname) {
        this.uname = uname;
    }

    /**
     * Setter Methode für ID
     * @param password das Passwort für den User
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter Methode für den Username
     * @return uname den Usernamen des Users
     */
    public String getUname() {
        return uname;
    }

    /**
     * Getter Methode für das Passwort
     * @return password das Passwort für den User
     */
    public String getPassword() {
        return password;
    }


}
