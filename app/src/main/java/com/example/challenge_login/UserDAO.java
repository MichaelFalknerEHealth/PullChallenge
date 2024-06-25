package com.example.challenge_login;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDAO {

@Insert
    public void addUser (User user);

@Update
    public void updateUser (User user);
@Delete
    public void deleteUser (User user);
    @Query("Select * from user")
    public List<User> getAllUser();

    @Query("Select * from user where user_name=(:user_name) and user_password=(:user_password)")
    User login(String user_name, String user_password);
    @Query("SELECT * FROM user WHERE user_name = :username LIMIT 1")
    User getUserByUsername(String username);


}
