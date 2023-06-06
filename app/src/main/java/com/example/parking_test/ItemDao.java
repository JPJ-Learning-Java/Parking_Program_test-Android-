package com.example.parking_test;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM Item order by ItemCode")
    List<Item> getAll();

    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM Item WHERE ItemCode = :selectCode")
    List<Item> selectCode(String selectCode);

    @Query("DELETE FROM Item WHERE ItemCode = :delCode")
    void deleteCode(String delCode);

    @Query("SELECT COUNT(ItemCode) FROM Item WHERE ItemCode = :countCode")
    int countCode(String countCode);

    @Query("SELECT ItemCode FROM Item order by ItemCode desc LIMIT 1")
    String newCode();

    @Query("UPDATE Item SET ItemName = :ItemName, ItemDate = :ItemDate, ItemTime = :ItemTime WHERE ItemCode = :countCode")
    void updateCode( String countCode, String ItemName, String ItemDate, String ItemTime);

    @Query("SELECT COUNT(ItemCode) FROM Item")
    int countCode();

}

