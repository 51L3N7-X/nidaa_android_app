package com.nidaa.app.TableDao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.nidaa.app.EntityClass.Table;
import com.nidaa.app.EntityClass.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TableDao {
    @Query("SELECT * FROM Tables")
    List<Table> getAll();

    @Query("SELECT * FROM Tables WHERE `key` IN (:tableIds)")
    List<Table> loadAllByIds(int[] tableIds);

    @Query("SELECT * FROM Tables WHERE table_number LIKE :table_number")
    Table findByNumber(String table_number);

    @Insert
    void insertTable(Table table);

    @Delete
    void delete(Table table);

    @Query("DELETE FROM Tables")
    void deleteAllTables();
}
