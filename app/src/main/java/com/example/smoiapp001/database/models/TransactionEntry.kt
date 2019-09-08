package com.example.smoiapp001.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.example.smoiapp001.utilities.DateUtils

import java.util.Date
import java.util.HashMap

@Entity(tableName = "transaction")
class TransactionEntry(var description: String?, var cost: Float, var date: Date) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["id"] = id
        result["description"] = description!!
        result["cost"] = cost
        result["date"] = DateUtils.toTimestamp(date)

        return result
    }
}
