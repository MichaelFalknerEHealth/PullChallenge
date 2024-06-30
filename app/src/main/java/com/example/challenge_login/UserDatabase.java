package com.example.challenge_login;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * abstrakte Klasse repräsentiert die Datenbank für User-operationen
 * verwendet Room für Datenbankzugriff und -management
 */
@Database(entities = {User.class},version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String dbName = "user";
    private static UserDatabase userDatabase;

    /**
     * liefert eine instanz der Userdatenbank UserDatabase
     * stellt sicher, dass nur eine Instanz der Datenbank während
     * des App-Lifecycles erstellt wird
     * @param context der Application kontext
     * @return liefert eine Instanz von UserDatabase
     */
    public static synchronized UserDatabase getUserDatabase(Context context){
        if (userDatabase == null){
            userDatabase = Room.databaseBuilder(context,UserDatabase.class, dbName)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return userDatabase;
    }

    /**
     * Abstrakte Methode um die userDAO zu bekommen
     * @return die Instanz von userDAO
     */
    public abstract UserDAO userDAO();

}
