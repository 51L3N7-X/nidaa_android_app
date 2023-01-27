package com.nidaa.app.UserDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.nidaa.app.EntityClass.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE `key` IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM User WHERE username LIKE :username")
    User findByName(String username);

    @Insert
    void insertUser(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM User")
    void deleteAllUsers();
}
