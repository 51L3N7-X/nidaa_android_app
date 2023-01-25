package com.nidaa.app.EntityClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int key;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "token")
    public String token;

    public User(int key, String username, String token) {
        this.key = key;
        this.username = username;
        this.token = token;
    }

    @Ignore
    public User(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
