package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.desent.desent.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ragnhlar on 28.02.2018.
 */

public class RegisterTransportationHabitsFragment extends Fragment {

    private CheckBox checkBoxWalk;
    private CheckBox checkBoxBicycle;
    private CheckBox checkBoxCar;
    private CheckBox checkBoxBus;
    private CheckBox checkBoxTrain;
    private CheckBox checkBoxMotorcycle;
    private CheckBox checkBoxScooter;
    private CheckBox checkBoxTram;
    private CheckBox checkBoxSubway;
    private CheckBox checkBoxFerry;
    private CheckBox checkBoxOther;

    private SharedPreferences sharedPreferences;

    private ArrayList<CheckBox> checkBoxes;
    private String habitsString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorGreen, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_transportation_habits, container, false);

        checkBoxes = new ArrayList<CheckBox>();
        habitsString = "";

        checkBoxWalk = (CheckBox) rootView.findViewById(R.id.cbWalk);
        checkBoxes.add(checkBoxWalk);

        checkBoxBicycle = (CheckBox) rootView.findViewById(R.id.cbBicycle);
        checkBoxes.add(checkBoxBicycle);

        checkBoxCar = (CheckBox) rootView.findViewById(R.id.cbCar);
        checkBoxes.add(checkBoxCar);

        checkBoxBus = (CheckBox) rootView.findViewById(R.id.cbBus);
        checkBoxes.add(checkBoxBus);

        checkBoxTrain = (CheckBox) rootView.findViewById(R.id.cbTrain);
        checkBoxes.add(checkBoxTrain);

        checkBoxMotorcycle = (CheckBox) rootView.findViewById(R.id.cbMotorcycle);
        checkBoxes.add(checkBoxMotorcycle);

        checkBoxScooter = (CheckBox) rootView.findViewById(R.id.cbScooter);
        checkBoxes.add(checkBoxScooter);

        checkBoxTram = (CheckBox) rootView.findViewById(R.id.cbTram);
        checkBoxes.add(checkBoxTram);

        checkBoxSubway = (CheckBox) rootView.findViewById(R.id.cbSubway);
        checkBoxes.add(checkBoxSubway);

        checkBoxFerry = (CheckBox) rootView.findViewById(R.id.cbFerry);
        checkBoxes.add(checkBoxFerry);

        checkBoxOther = (CheckBox) rootView.findViewById(R.id.cbOther);
        checkBoxes.add(checkBoxOther);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();

        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                habitsString += checkBox.getText().toString() + ", ";
            }
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_key_transportation_habits", habitsString);
        editor.commit();
    }

    private void restorePreferences(){
        String habitsString = sharedPreferences.getString("pref_key_transportation_habits", "");
        List<String> habitList = Arrays.asList(habitsString.split(", "));
        for (String habit : habitList){
            for (CheckBox checkBox : checkBoxes){
                if (checkBox.getText().toString().equals(habit)){
                    checkBox.setChecked(true);
                }
            }
        }
    }
}
