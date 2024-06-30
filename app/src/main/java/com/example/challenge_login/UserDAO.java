package com.example.challenge_login;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * DAO für die User-Entität
 * stellt Methoden um mit der User Tabelle in der Datenbank zu interagieren
 */

@Dao
public interface UserDAO {

    /**
     * fügt neuen User in die Datenbank
     * @param user der User, der hinzugefügt wird
     */
    @Insert
    public void addUser (User user);

    /**
     * updatet einen User, der bereits erstellt wurde in der Datenbank
     * @param user der User, der geupdatet wird
     */
    @Update
    public void updateUser (User user);

    /**
     * Löscht einen user aus der Datenbank
     * @param user der User, der gelöscht wird
     */
    @Delete
    public void deleteUser (User user);

    /**
     * liefert alle user aus der Datenbank
     * @return eine Liste mit allen Usern
     */
    @Query("Select * from user")
    public List<User> getAllUser();

    /**
     * liefert einen User aus der Datenbank mit dem gegebenen Usernamen und
     * dem gegebenen Passwort
     * @param user_name der Username des Users
     * @param user_password das Passwort des Users
     * @return den entsprechenden User oder null wenn auf nichts trifft
     */
    @Query("Select * from user where user_name=(:user_name) and user_password=(:user_password)")
    User login(String user_name, String user_password);

    /**
     * liefert einen user aus der Datenbank mit dem gegebenen Usernamen
     * @param username der Username des Users
     * @return den zutreffenden User oder null wenn auf nichts trifft
     */
    @Query("SELECT * FROM user WHERE user_name = :username LIMIT 1")
    User getUserByUsername(String username);


}
