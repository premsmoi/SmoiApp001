package com.example.smoiapp001.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*


class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        var mBundle = this.arguments

        if (mBundle != null && mBundle.containsKey("day")
                && mBundle.containsKey("month") && mBundle.containsKey("year")) {
            val year = mBundle.getInt("year")
            val month = mBundle.getInt("month")
            val day = mBundle.getInt("day")
            // Create a new instance of DatePickerDialog using old values and return it
            return DatePickerDialog(activity!!, this, year, month, day)
        }
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity!!, this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val selectedDate = IntArray(3)
        selectedDate[0] = year
        selectedDate[1] = month
        selectedDate[2] = day

        targetFragment!!.onActivityResult(
                targetRequestCode,
                Activity.RESULT_OK,
                Intent().putExtra("selectedDate", selectedDate)
        )
    }
}
