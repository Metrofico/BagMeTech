package com.teamticpro.bagmetech.entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserEntityDAO {
    String user_entities = "user_entities";
    String user_column_token = "token";
    String user_column_id = "id";

    @Query("SELECT * FROM " + user_entities + " LIMIT 1 ")
    UserEntity getUser();


    @Query("SELECT * FROM " + user_entities + " WHERE " + user_column_id + " = :id")
    UserEntity getUser(String id);

    @Query("SELECT " + user_column_token + " FROM " + user_entities + " WHERE " + user_column_id + "= :id")
    String getTokenById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUser(UserEntity entity);

    @Delete
    void removeUser(UserEntity entity);

    @Query("DELETE FROM " + user_entities + " WHERE " + user_column_id + "= :id")
    int removeUser(String id);

    @Update
    void updateUser(UserEntity entity);

}
