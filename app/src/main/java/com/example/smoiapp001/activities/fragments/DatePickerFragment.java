package com.example.smoiapp001.activities.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            month--;
            Log.i("Test", "old");
            // Create a new instance of DatePickerDialog using old values and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Log.i("Test", "default");
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        month++;
        String dayString = Integer.toString(day);
        String monthString = Integer.toString(month);

        if (day < 10) {
            dayString = "0" + dayString;
        }

        if (month < 10) {
            monthString = "0" + monthString;
        }
        String formedDate = dayString + "/" + monthString + "/" + year;
        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("selectedDate", formedDate)
        );
    }
}
