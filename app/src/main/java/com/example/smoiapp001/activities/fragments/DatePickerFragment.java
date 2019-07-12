package com.example.smoiapp001.activities.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle mBundle) {
        mBundle = this.getArguments();

        if (mBundle != null && mBundle.containsKey("day")
                && mBundle.containsKey("month") && mBundle.containsKey("year")) {
            int year = mBundle.getInt("year");
            int month = mBundle.getInt("month");
            int day = mBundle.getInt("day");
            // Create a new instance of DatePickerDialog using old values and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

       /* month++;
        String dayString = Integer.toString(day);
        String monthString = Integer.toString(month);

        if (day < 10) {
            dayString = "0" + dayString;
        }

        if (month < 10) {
            monthString = "0" + monthString;
        }*/
        //ArrayList<Integer> selectedDate = new ArrayList<>();
        int[] selectedDate = new int[3];
        selectedDate[0] = year;
        selectedDate[1] = month;
        selectedDate[2] = day;

        /*String formedDate = dayString + "/" + monthString + "/" + year;*/
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("selectedDate", selectedDate)
                /*new Intent().putExtra("selectedDate", formedDate)*/
        );
    }
}
