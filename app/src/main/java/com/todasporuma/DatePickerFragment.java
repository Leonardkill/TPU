package com.todasporuma;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener{

    public interface DatePickerListener{
        void onDateSet(DatePicker datePicker, int year, int month, int day);
    }

    DatePickerListener mlistener;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
     try  {
         mlistener= (DatePickerListener) context;
     } catch (Exception e) {
              throw new  ClassCastException(getActivity().toString()+" Must Implements DatePickerListener");
     }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this,year,month,day);

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mlistener.onDateSet(datePicker, i, i1, i2);
    }
}
