package com.example.demoapp;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {


    @PrimaryKey(autoGenerate = true)
    public int id;


    public int userId;

    public String title;
    public String description;
    public double price;
    public boolean isFavorite;
    public long createdAt;
}