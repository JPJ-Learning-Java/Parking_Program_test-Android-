package com.example.parking_test;

import android.widget.TextView;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String ItemCode;
    private String ItemName;
    private String ItemDate;
    private String ItemTime;


    public Item( String ItemCode, String ItemName, String ItemDate, String ItemTime) {
        this.ItemCode = ItemCode;
        this.ItemName = ItemName;
        this.ItemDate = ItemDate;
        this.ItemTime = ItemTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String sItemCode) {
        this.ItemCode = ItemCode;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String sItemName) {
        this.ItemName = ItemName;
    }

    public String getItemDate(){
        /*long now;
        Date date;
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");

        now = System.currentTimeMillis();
        date = new Date(now);
        ItemDate = nowDate.format(date);
        return (ItemDate);*/
        return ItemDate;
    }

    public void setItemDate(String sDate){
        this.ItemDate = ItemDate;
    }

    public String getItemTime(){
        return ItemTime;
    }

    public void setItemTime(String sItemTime) {
        this.ItemTime = ItemTime;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", ItemDate='" + ItemDate + '\'' +
                ", ItemTime=" + ItemTime + '\'' +
                '}';
    }
}
