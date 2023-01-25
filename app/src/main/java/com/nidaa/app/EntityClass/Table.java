package com.nidaa.app.EntityClass;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tables")
public class Table {
    @PrimaryKey(autoGenerate = true)
    public int key;

    @ColumnInfo(name = "table_number")
    public String table_number;



    public Table(int key, String table_number) {
        this.key = key;
        this.table_number = table_number;
    }

    @Ignore
    public Table(String table_number) {
        this.table_number = table_number;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getTableNumber() {
        return table_number;
    }

    public void setTableNumber(String table_number) {
        this.table_number = table_number;
    }
    
}
