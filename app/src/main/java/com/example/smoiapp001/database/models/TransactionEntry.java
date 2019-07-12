package com.example.smoiapp001.database.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.smoiapp001.utilities.DateConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "transaction")
public class TransactionEntry {

    @PrimaryKey(autoGenerate = true)
    private int    id;
    private String description;
    private float  cost;
    private Date   date;

    public TransactionEntry(String description, float cost, Date date) {
        this.description = description;
        this.cost        = cost;
        this.date        = date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("description", description);
        result.put("cost", cost);
        result.put("date", DateConverter.toTimestamp(date));

        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
