package com.nidaa.app.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nidaa.app.EntityClass.Table;
import com.nidaa.app.EntityClass.User;
import com.nidaa.app.TableDao.TableDao;
import com.nidaa.app.UserDao.UserDao;

@Database(entities = {Table.class} , version=1)
public abstract class DatabaseClass extends RoomDatabase {
//    public abstract UserDao userDao();
    public abstract TableDao tableDao();
}
