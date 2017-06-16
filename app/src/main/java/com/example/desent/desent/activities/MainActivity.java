package com.example.desent.desent.activities;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.fragments.CategoryFragment;
import com.example.desent.desent.fragments.CircleFragment;
import com.example.desent.desent.fragments.MonthFragment;
import com.example.desent.desent.fragments.WeekFragment;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.views.CircularIndicator;
import com.example.desent.desent.views.EstimationButton;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Date date = null;
    Spinner timeSpinner;

    //Fragments
    private ArrayList<CircleFragment> circleFragments = new ArrayList<>();
    private WeekFragment weekFragment;
    private MonthFragment monthFragment;
    private CategoryFragment transportationDashboardFragment;
    private CategoryFragment housingDashboardFragment;

    //Indicators
    protected ArrayList<Indicator> indicators = new ArrayList<>();
    protected Indicator calories;
    protected Indicator expenses;
    protected Indicator carbonFootprint;
    protected Indicator transportation;
    protected Indicator housing;

    //Buttons
    EstimationButton solarPanelButton;
    EstimationButton walkButton;
    EstimationButton cycleButton;
    EstimationButton electricCarButton;
    private TextView captionView;
    private String captionText = "";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public enum ActiveView {
        DAY,
        WEEK,
        MONTH
    }

    @Override
    public void onBackPressed() {
        timeSpinner.setSelection(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUp();
        solarPanelButton.setOnClickListener(estimationButtonHandler);
        walkButton.setOnClickListener(estimationButtonHandler);
        cycleButton.setOnClickListener(estimationButtonHandler);
        electricCarButton.setOnClickListener(estimationButtonHandler);

        timeSpinner.setOnItemSelectedListener(timeSpinnerActivity);

        goToDailyView();
    }

    AdapterView.OnItemSelectedListener timeSpinnerActivity = new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    goToDailyView();
                    break;
                case 1:
                    goToWeeklyView();
                    break;
                case 2:
                    goToMonthlyView();
            }

        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    View.OnClickListener estimationButtonHandler = new View.OnClickListener() {

        public void onClick(View v) {

            EstimationButton button = (EstimationButton) v;

            if (button.getId() == R.id.walkingButton) {
                if (cycleButton.isActivated()) {
                    disableEstimation(cycleButton);
                }
                if (electricCarButton.isActivated()) {
                    disableEstimation(electricCarButton);
                }
            }

            if (button.getId() == R.id.cyclingButton) {
                if (walkButton.isActivated()) {
                    disableEstimation(walkButton);
                }
                if (electricCarButton.isActivated()) {
                    disableEstimation(electricCarButton);
                }
            }

            if (button.getId() == R.id.electricCarButton) {
                if (walkButton.isActivated()) {
                    disableEstimation(walkButton);
                }
                if (cycleButton.isActivated()) {
                    disableEstimation(cycleButton);
                }
            }

            if (!button.isActivated()) {
                enableEstimation(button);
            } else
                disableEstimation(button);

            if (!(walkButton.isActivated() || cycleButton.isActivated() || electricCarButton.isActivated())) {

                for (Indicator indicator : indicators) {
                    indicator.estimateDailyValues(date, "Transportation", 0);
                    indicator.estimateWeeklyValues(date, "Transportation", 0);
                    indicator.estimateMonthlyValues(date, "Transportation", 0);
                }

            }

            if (!(solarPanelButton.isActivated())) {

                for (Indicator indicator : indicators) {
                    indicator.estimateDailyValues(date, "Housing", 1);
                    indicator.estimateWeeklyValues(date, "Housing", 1);
                    indicator.estimateMonthlyValues(date, "Housing", 1);
                }
            }


            for (CircleFragment circleFragment : circleFragments)
                circleFragment.refresh();

            transportationDashboardFragment.refresh();
            housingDashboardFragment.refresh();
            weekFragment.refresh();
            monthFragment.refresh();
        }
    };

    public void enableEstimation(EstimationButton estimationButton) {

        estimationButton.setActivated(true);

        if (captionText.equals(""))
            captionText = "Your performance " + estimationButton.getCaption();
        else
            captionText = captionText + " and " + estimationButton.getCaption();

        for (Indicator indicator : indicators) {
            indicator.estimateDailyValues(date, estimationButton);
            indicator.estimateWeeklyValues(date, estimationButton);
            indicator.estimateMonthlyValues(date, estimationButton);
        }

        for (CircleFragment circleFragment : circleFragments) {
            circleFragment.refresh();
        }

        ((TextView) findViewById(R.id.caption)).setText(captionText);
        weekFragment.refresh();
        monthFragment.refresh();

    }

    public void disableEstimation(EstimationButton estimationButton) {

        estimationButton.setActivated(false);

        captionText = captionText.replace(estimationButton.getCaption(), "");
        if (captionText.contains("and"))
            captionText = captionText.replace(" and ", "");
        else
            captionText = "";
        ((TextView) findViewById(R.id.caption)).setText(captionText);

        weekFragment.refresh();
        monthFragment.refresh();

    }

    protected void goToIndicatorView(Indicator indicator) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        for (CircleFragment circleFragment : circleFragments) {
            ft.hide(circleFragment);
        }

        ft.commit();


        ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        for (CircleFragment circleFragment : circleFragments) {

            circleFragment.saveInitialHeight();

            if (indicator.getName().equals(circleFragment.getIndicator().getName()))
                ft.show(circleFragment);

        }

        weekFragment.setIndicator(indicator);
        weekFragment.refresh();
        monthFragment.setIndicator(indicator);
        monthFragment.refresh();

        if (indicator != calories) {
            transportationDashboardFragment.setIndicator(indicator);
            housingDashboardFragment.setIndicator(indicator);
            ft.show(transportationDashboardFragment);
            ft.show(housingDashboardFragment);
        } else {
            ft.hide(transportationDashboardFragment);
            ft.hide(housingDashboardFragment);
        }

        ft.commit();

    }

    protected void goToDailyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        captionView.setVisibility(View.VISIBLE);
        ft.hide(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment : circleFragments) {
            circleFragment.setActiveView(ActiveView.DAY);
        }

        transportationDashboardFragment.setActiveView(ActiveView.DAY);
        housingDashboardFragment.setActiveView(ActiveView.DAY);
    }

    protected void goToWeeklyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        captionView.setVisibility(View.GONE);
        ft.show(weekFragment);
        ft.hide(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment : circleFragments)
            circleFragment.setActiveView(ActiveView.WEEK);

        transportationDashboardFragment.setActiveView(ActiveView.WEEK);
        housingDashboardFragment.setActiveView(ActiveView.WEEK);

    }

    protected void goToMonthlyView() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack("month");

        captionView.setVisibility(View.GONE);
        ft.hide(weekFragment);
        ft.show(monthFragment);
        ft.commit();

        for (CircleFragment circleFragment : circleFragments)
            circleFragment.setActiveView(ActiveView.MONTH);

        transportationDashboardFragment.setActiveView(ActiveView.MONTH);
        housingDashboardFragment.setActiveView(ActiveView.MONTH);

    }

    protected void setUp() {

        //Colors
        int mRed = ContextCompat.getColor(getApplicationContext(), R.color.red);
        int mOrange = ContextCompat.getColor(getApplicationContext(), R.color.orange);
        int mGreen = ContextCompat.getColor(getApplicationContext(), R.color.green);
        int mBlue = ContextCompat.getColor(getApplicationContext(), R.color.blue);
        int mDarkGrey = ContextCompat.getColor(getApplicationContext(), R.color.dark_grey);
        int mLightGrey = ContextCompat.getColor(getApplicationContext(), R.color.light_grey);

        //Limit values
        int targetCalories = 1700;
        int limitExpenses = 400;
        int limitCarbonFootprint = 4;

        //Fragments
        CircleFragment caloriesCircleFragment;
        CircleFragment expensesCircleFragment;
        CircleFragment carbonFootprintCircleFragment;

        circleFragments.add(carbonFootprintCircleFragment = (CircleFragment) getFragmentManager().findFragmentById(R.id.dailyCarbonFootprint));

        weekFragment = (WeekFragment) getFragmentManager().findFragmentById(R.id.weeklyData);
        monthFragment = (MonthFragment) getFragmentManager().findFragmentById(R.id.monthlyData);

        transportationDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.transportation_dashboard_fragment);
        housingDashboardFragment = (CategoryFragment) getFragmentManager().findFragmentById(R.id.housing_dashboard_fragment);

        captionView = (TextView) findViewById(R.id.caption);
        captionView.setTextColor(mDarkGrey);

        //Date
        InputStream inputStream = getResources().openRawResource(R.raw.data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = dateFormat.parse("2017-04-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Spinners
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.time_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(adapter);

        //Indicators
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Transportation");
        columnNames.add("Housing"); //Prevent errors for estimations
        indicators.add(carbonFootprint = new Indicator(inputStream, "Carbon footprint", "kgCO2", columnNames));
        indicators.add(calories = new Indicator(inputStream, "Calories", "kCal", columnNames));
        indicators.add(expenses = new Indicator(inputStream, "Expenses", "kr", columnNames));

        transportation = new Indicator(inputStream, "Transportation", "km", "Distance");
        housing = new Indicator(inputStream, "Housing", "kWh", "Energy consumption");

        carbonFootprintCircleFragment.setStartAngle(135);
        carbonFootprintCircleFragment.setSweepAngle(270);
        carbonFootprintCircleFragment.setImgName("earth");
        carbonFootprintCircleFragment.setNumberOfStates(5);

        calories.setColor(mOrange);
        calories.setLimitColor(mLightGrey);

        ArrayList<Integer> energyTransportationColors = new ArrayList<>();
        energyTransportationColors.add(mGreen);
        energyTransportationColors.add(mBlue);
        carbonFootprint.setColors(energyTransportationColors);
        carbonFootprint.setLimitColor(mRed);
        expenses.setColors(energyTransportationColors);
        expenses.setLimitColor(mRed);

        transportation.setColor(mGreen);
        transportation.setDecimalsNumber(1);
        housing.setColor(mBlue);
        housing.setDecimalsNumber(1);

        for (Indicator indicator : indicators) {
            indicator.readDailyValues(date);
            indicator.readWeeklyValues(date);
            indicator.readMonthlyValues(date);
        }

        transportation.readDailyValues(date);
        housing.readDailyValues(date);

        //Estimation buttons
        solarPanelButton = (EstimationButton) findViewById(R.id.solarPanelButton);
        solarPanelButton.setCaption("with a solar installation");
        walkButton = (EstimationButton) findViewById(R.id.walkingButton);
        walkButton.setCaption("walking 5km instead of driving");
        cycleButton = (EstimationButton) findViewById(R.id.cyclingButton);
        cycleButton.setCaption("cycling 5km instead of driving");
        electricCarButton = (EstimationButton) findViewById(R.id.electricCarButton);
        electricCarButton.setCaption("with the Nissan Leaf");

        calories.setMaxValue(targetCalories);
        calories.setLimitValue(targetCalories);
        expenses.setMaxValue(limitExpenses);
        expenses.setLimitValue(limitExpenses);
        carbonFootprint.setMaxValue(2 * limitCarbonFootprint);
        carbonFootprint.setLimitValue(limitCarbonFootprint);

        calories.setDecimalsNumber(0);
        expenses.setDecimalsNumber(0);
        carbonFootprint.setDecimalsNumber(1);

        carbonFootprintCircleFragment.setIndicator(carbonFootprint);
        carbonFootprintCircleFragment.setFormat(CircularIndicator.Format.CIRCLE_IMG_TEXT);
        carbonFootprintCircleFragment.setUp();

        weekFragment.setIndicator(carbonFootprint);
        weekFragment.setUp();

        monthFragment.setIndicator(carbonFootprint);
        monthFragment.setUp();

        transportationDashboardFragment.setCategory(transportation);
        transportationDashboardFragment.setCategoryIndex(0);
        transportationDashboardFragment.setIndicator(carbonFootprint);
        transportationDashboardFragment.setUp();

        housingDashboardFragment.setCategory(housing);
        housingDashboardFragment.setCategoryIndex(1);
        housingDashboardFragment.setIndicator(carbonFootprint);
        housingDashboardFragment.setUp();
    }
}
