package com.example.desent.desent.fragments;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;

public class RegisterTransportationFragment extends Fragment {

    private ScrollView scrollView;

    private CheckBox carOwner;
    private Spinner carSizeSpinner;
    private EditText regNrTextView;
    private EditText priceTextView;
    private EditText drivingDistanceTextView;
    private EditText ownershipPeriodTextView;

    private CheckBox carOwner2;
    private Spinner carSizeSpinner2;
    private EditText regNrTextView2;
    private EditText priceTextView2;
    private EditText drivingDistanceTextView2;
    private EditText ownershipPeriodTextView2;
    private LinearLayout car_nr_2_info;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getTheme().applyStyle(R.style.AppTheme_NoActionBar_AccentColorGreen, true);

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_register_transportation, container, false);

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollViewTransportation);

        //TODO: button
        carOwner = rootView.findViewById(R.id.checkbox_car_owner);
        carOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onCheckboxClicked(compoundButton, b);
            }
        });

        carSizeSpinner = rootView.findViewById(R.id.spinner_car_size);
        ArrayAdapter<CharSequence> adapterCar1 = ArrayAdapter.createFromResource(getActivity(), R.array.pref_car_size_list, android.R.layout.simple_spinner_item);
        adapterCar1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        carSizeSpinner.setAdapter(adapterCar1);

        regNrTextView = rootView.findViewById(R.id.reg_nr);
        priceTextView = rootView.findViewById(R.id.car_price);
        drivingDistanceTextView = rootView.findViewById(R.id.car_driving_distance);
        ownershipPeriodTextView = rootView.findViewById(R.id.car_ownership_period);

        car_nr_2_info = rootView.findViewById(R.id.car_nr_2_info);
        car_nr_2_info.setVisibility(View.GONE);

        carOwner2 = rootView.findViewById(R.id.checkbox_car_owner_2);
        carOwner2.setChecked(false);
        carOwner2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //car_nr_2_info.setVisibility(View.VISIBLE);
                onCheckboxClicked(compoundButton, b);
            }
        });

        carSizeSpinner2 = rootView.findViewById(R.id.spinner_car_size2);
        ArrayAdapter<CharSequence> adapterCar2 = ArrayAdapter.createFromResource(getActivity(), R.array.pref_car_size_list, android.R.layout.simple_spinner_item);
        adapterCar2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        carSizeSpinner2.setAdapter(adapterCar2);

        regNrTextView2 = rootView.findViewById(R.id.reg_nr_car_2);
        priceTextView2 = rootView.findViewById(R.id.car_price_nr_2);
        drivingDistanceTextView2 = rootView.findViewById(R.id.car_driving_distance_nr_2);
        ownershipPeriodTextView2 = rootView.findViewById(R.id.car_ownership_period_nr_2);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        restorePreferences();

        carOwner.callOnClick();
        carOwner2.callOnClick();

        return rootView;
    }

    public void onCheckboxClicked(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == R.id.checkbox_car_owner) {
            regNrTextView.setEnabled(b);
            carSizeSpinner.setEnabled(b);
            priceTextView.setEnabled(b);
            drivingDistanceTextView.setEnabled(b);
            ownershipPeriodTextView.setEnabled(b);
        } else if (compoundButton.getId() == R.id.checkbox_car_owner_2) {
            if (b) {
                car_nr_2_info.setVisibility(View.VISIBLE);
            } else {
                car_nr_2_info.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(carOwner.isChecked()){
            editor.putBoolean("pref_key_car_owner", carOwner.isChecked());
            editor.putString("pref_key_reg_nr", regNrTextView.getText().toString());
            editor.putString("pref_key_car_size", carSizeSpinner.getSelectedItem().toString());
            editor.putString("pref_key_car_price", String.valueOf(priceTextView.getText()));
            editor.putString("pref_key_car_distance", String.valueOf(drivingDistanceTextView.getText()));
            editor.putString("pref_key_car_ownership_period", String.valueOf(ownershipPeriodTextView.getText()));
        }else{
            editor.putBoolean("pref_key_car_owner", false);
            /*
            editor.putString("pref_key_car_size", ;
            editor.putString("pref_key_car_price", String.valueOf(priceTextView.getText()));
            editor.putString("pref_key_car_distance", String.valueOf(drivingDistanceTextView.getText()));
            editor.putString("pref_key_car_ownership_period", String.valueOf(ownershipPeriodTextView.getText()));
        */
        }
        if(carOwner2.isChecked()){
            editor.putBoolean("pref_key_car_owner_2", carOwner2.isChecked());
            editor.putString("pref_key_reg_nr_2", regNrTextView2.getText().toString());
            editor.putString("pref_key_car_size_2", carSizeSpinner2.getSelectedItem().toString());
            editor.putString("pref_key_car_price_2", String.valueOf(priceTextView2.getText()));
            editor.putString("pref_key_car_distance_2", String.valueOf(drivingDistanceTextView2.getText()));
            editor.putString("pref_key_car_ownership_period_2", String.valueOf(ownershipPeriodTextView2.getText()));
        }

        editor.commit();
    }

    private void restorePreferences(){
        carOwner.setChecked(sharedPreferences.getBoolean("pref_key_car_owner", true));
        carSizeSpinner.setSelection(((ArrayAdapter<String>) carSizeSpinner.getAdapter()).getPosition(sharedPreferences.getString("pref_key_car_size", "Small")));
        priceTextView.setText(sharedPreferences.getString("pref_key_car_price", "300000"), TextView.BufferType.EDITABLE);
        drivingDistanceTextView.setText(sharedPreferences.getString("pref_key_car_distance", "8000"), TextView.BufferType.EDITABLE);
        ownershipPeriodTextView.setText(sharedPreferences.getString("pref_key_car_ownership_period", "3"), TextView.BufferType.EDITABLE);
        regNrTextView.setText(sharedPreferences.getString("pref_key_reg_nr", "HJ 20005"), TextView.BufferType.EDITABLE);

        carOwner2.setChecked(sharedPreferences.getBoolean("pref_key_car_owner_2", false));
        carSizeSpinner2.setSelection(((ArrayAdapter<String>) carSizeSpinner2.getAdapter()).getPosition(sharedPreferences.getString("pref_key_car_size_2", "Small")));
        priceTextView2.setText(sharedPreferences.getString("pref_key_car_price_2", "300000"), TextView.BufferType.EDITABLE);
        drivingDistanceTextView2.setText(sharedPreferences.getString("pref_key_car_distance_2", "8000"), TextView.BufferType.EDITABLE);
        ownershipPeriodTextView2.setText(sharedPreferences.getString("pref_key_car_ownership_period_2", "3"), TextView.BufferType.EDITABLE);
        regNrTextView2.setText(sharedPreferences.getString("pref_key_reg_nr_2", "HJ 20005"), TextView.BufferType.EDITABLE);
    }
}
