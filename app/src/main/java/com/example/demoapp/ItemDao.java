package com.example.demoapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items WHERE userId = :userId ORDER BY createdAt DESC")
    List<Item> getAllByUser(int userId);

    @Query("SELECT * FROM items WHERE userId = :userId AND isFavorite = 1 ORDER BY createdAt DESC")
    List<Item> getFavoritesByUser(int userId);
}