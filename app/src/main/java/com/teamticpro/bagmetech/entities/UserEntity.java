package com.teamticpro.bagmetech.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.teamticpro.bagmetech.LoginQuery;

import java.io.Serializable;

@Entity(tableName = "user_entities")
public class UserEntity implements Serializable {
    @ColumnInfo(name = "id")
    @NonNull
    @PrimaryKey
    private String id;
    @ColumnInfo(name = "Name")
    private String Name;
    @ColumnInfo(name = "LastName")
    private String LastName;
    @ColumnInfo(name = "token")
    private String token;
    @ColumnInfo(name = "username")
    private String username;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "NumberId")
    private String NumberId;
    @ColumnInfo(name = "Technical")
    private Boolean Technical;

    public UserEntity(String id, String Name, String LastName, String token, String username, String email, String NumberId, Boolean Technical) {
        this.setId(id);
        setName(Name);
        setLastName(LastName);
        this.setToken(token);
        this.setUsername(username);
        setEmail(email);
        setNumberId(NumberId);
        setTechnical(Technical);
    }

    public void setTechnical(Boolean technical) {
        Technical = technical;
    }

    public Boolean getTechnical() {
        return Technical;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public void setNumberId(String numberId) {
        NumberId = numberId;
    }

    public String getNumberId() {
        return NumberId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public UserEntity fromLoginQuery(LoginQuery.User user) {
        setId(user._id());
        setName(user.Name());
        setLastName(user.LastName());
        setUsername(user.username());
        setEmail(user.email());
        setNumberId(user.NumberId());
        setTechnical(user.technical());
        return this;
    }
}
