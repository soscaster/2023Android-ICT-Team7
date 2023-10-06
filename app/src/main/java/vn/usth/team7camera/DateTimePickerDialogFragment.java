package vn.usth.team7camera;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class DateTimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private Calendar selectedDateTime;
    private Calendar maxDateTime; // Set this to limit the maximum date and time

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        selectedDateTime = Calendar.getInstance();
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        // Set the maximum date and time (e.g., limit to the current date and time)
        maxDateTime = Calendar.getInstance();

        // Create a date picker dialog and set the current date as the default value
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                this,
                year,
                month,
                day
        );

        // Set the maximum date for the date picker dialog
        datePickerDialog.getDatePicker().setMaxDate(maxDateTime.getTimeInMillis());

        // Create a time picker dialog and set the current time as the default value
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                this,
                hour,
                minute,
                true
        );

        // Combine both date and time pickers
        datePickerDialog.setCancelable(false); // Prevent closing the dialog by clicking outside

        // Show the date picker dialog first, and when a date is selected, show the time picker dialog
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, day);
                timePickerDialog.show();
            }
        });

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Date is set
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Handle the selected date and time here
        // You can use the selectedDateTime calendar object to access both date and time
        // For example:
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);

        // Check if the selected time is later than the maximum time
        if (selectedDateTime.after(maxDateTime)) {
            boolean isInvalidTime = true;
            Intent intent = new Intent();
            intent.putExtra("invalidTime", isInvalidTime);
            // Set the result for EventsFragment
            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("selectedDateTime", selectedDateTime.getTimeInMillis()); // Pass selected date/time as millis
            if (getTargetFragment() != null) {
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            }

        }

        // Dismiss the dialog
        dismiss();

    }
}
