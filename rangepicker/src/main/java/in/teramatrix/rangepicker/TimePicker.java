package in.teramatrix.rangepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * One and only {@link AppCompatActivity} to handle this library. It is a simple range picker can be used in
 * most common scenarios. It has some presets for the hour and days like last 12 hours to now, today and yesterday.
 * There's no {@link android.widget.ListView} or {@link android.support.v7.widget.RecyclerView}. All the elements have
 * been put in xml manually. So the layout is free to edit according to UI/theme of the application.
 * <p>
 * In this class, first all the elements have been initialized with their xml ids. As you click on any {@link RadioButton},
 * all other {@link RadioButton}s will be checked false. The important part of the class is {@link MaterialCalendarView} hidden
 * in {@link CollapsingToolbarLayout}. As user clicks on Custom {@link RadioButton}, {@link CollapsingToolbarLayout} will be
 * expanded. This event has been handled in {@code onDateSelected()} of {@link OnDateSelectedListener}.
 * <p>
 * In this version, there's not too much customizations in this library/time picker screen. But in future, customizations can be done
 * according response of people.
 *
 * @author mohsin.khan
 * @date 24 Mar 2016
 * @since Version 1.0.0
 */
public class TimePicker extends AppCompatActivity implements OnDateSelectedListener, View.OnClickListener,
        TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener/*, View.OnTouchListener*/,
        AppBarLayout.OnOffsetChangedListener {

    /**
     * A third party time picker dialog
     * <a href="https://github.com/wdullaer/MaterialDateTimePicker">Material DateTime Picker</a>
     */
    private TimePickerDialog picker;
    /**
     * This layout encapsulate {@link CollapsingToolbarLayout} inside it that contains a {@link MaterialCalendarView}
     * and custom {@link Toolbar}
     */
    private AppBarLayout appBarLayout;
    /**
     * This is the default date format for the app. In this version this is not customizable.
     * Currently it is formatted with "dd MMM yyyy"
     */
    private SimpleDateFormat dateFormat;
    /**
     * This is the default time format for the app. In this version this is not customizable.
     * Currently it is formatted with "hh:mm a"
     */
    private SimpleDateFormat timeFormat;
    /**
     * To setup custom date, another third party library has been used for calendar view.
     * <a href="https://github.com/prolificinteractive/material-calendarview">Material Calendar View</a>
     */
    private MaterialCalendarView calendarView;

    /**
     * A {@link RadioButton} for the preset of one hour.
     */
    private RadioButton radio1Hour;
    /**
     * A {@link RadioButton} for the preset of six hours.
     */
    private RadioButton radio6Hours;
    /**
     * A {@link RadioButton} for the preset of twelve hours.
     */
    private RadioButton radio12Hours;
    /**
     * A {@link RadioButton} for the preset of twenty four hours.
     */
    private RadioButton radio24Hours;
    /**
     * A {@link RadioButton} for custom time range selection.
     */
    private RadioButton radioCustom;
    /**
     * A {@link RadioButton} for today's time range selection.
     * <br/><br/>
     * <b>From 12:00 AM to current time</b>
     */
    private RadioButton radioToday;
    /**
     * A {@link RadioButton} for the time range of previous day.
     * <br/><br/>
     * <b>From 12:00 AM to 11:59 PM of previous day</b>
     */
    private RadioButton radioYesterday;

    /**
     * A final tag to denote end date of the range.
     */
    public static final String TO = "TO";
    /**
     * A final tag to denote start date of the range.
     */
    public static final String FROM = "FROM";
    /**
     * A final tag input custom {@link Calendar} instance before opening the module.
     */
    public static final String TIME = "TIME";
    /**
     * This is the title of the screen. It will be set up into action bar of the activity.
     * It may be customizable in the next release so it has been declared global.
     */
    public static final String SCREEN_TITLE = "Data Setting";

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        // Default setting of date time formats
        timeFormat = new SimpleDateFormat("hh:mm a");
        dateFormat = new SimpleDateFormat("dd MMM yyyy");

        // A calendar to allow date selection
        calendarView = (MaterialCalendarView) findViewById(R.id.calendar);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        calendarView.setPadding(0, getResources().getDimensionPixelSize(resourceId), 0, 0);
        Calendar temp = getIntent().getSerializableExtra(TIME) != null
                ? (Calendar) getIntent().getSerializableExtra(TIME)
                : Calendar.getInstance();
        calendarView.setSelectedDate(temp);
        calendarView.setCurrentDate(temp);
        calendarView.setSelected(true);
        calendarView.setOnDateChangedListener(this);

        // Scrollview must be after action bar, and it should be sticky in nature
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        nestedScrollView.setNestedScrollingEnabled(false);

        // setting up custom toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnClickListener(this);
        setSupportActionBar(toolbar);

        // Default settings of the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(SCREEN_TITLE);
        }

        // Adding offset listener to block app bar sliding on touch
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);

        // No need of collapsing toolbar title here, removing it
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitleEnabled(false);

        // Initializing calendar view with the current date
        Calendar calendar = Calendar.getInstance();
        picker = TimePickerDialog.newInstance(this, calendar.get(HOUR_OF_DAY), calendar.get(MINUTE), false);
        String currentTime = timeFormat.format(calendar.getTime());

        // For custom time selection, these two text-views have been used
        findViewById(R.id.txt_from_custom).setOnClickListener(this);
        findViewById(R.id.txt_to_custom).setOnClickListener(this);

        // for the preset of one hour
        radio1Hour = (RadioButton) findViewById(R.id.radio_1_hour);
        radio1Hour.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_1_hour, -1);
        setTime(R.id.txt_to_1_hour, currentTime);

        // for the preset of six hours
        radio6Hours = (RadioButton) findViewById(R.id.radio_6_hours);
        radio6Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_6_hours, -6);
        setTime(R.id.txt_to_6_hours, currentTime);

        // for the preset of twelve hours
        radio12Hours = (RadioButton) findViewById(R.id.radio_12_hours);
        radio12Hours.setChecked(true);
        radio12Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_12_hours, -12);
        setTime(R.id.txt_to_12_hours, currentTime);

        // for the preset of twenty four hours
        radio24Hours = (RadioButton) findViewById(R.id.radio_24_hours);
        radio24Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_24_hours, -24);
        setTime(R.id.txt_to_24_hours, currentTime);

        // for the custom time selection
        radioCustom = (RadioButton) findViewById(R.id.radio_custom);
        radioCustom.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_custom, FROM + "\n00:00 AM");
        setTime(R.id.txt_to_custom, TO + "\n" + currentTime);

        // for today's time range selection
        radioToday = (RadioButton) findViewById(R.id.radio_today);
        radioToday.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_today, "00:00 AM");
        setTime(R.id.txt_to_today, currentTime);

        // for the time range of previous day
        radioYesterday = (RadioButton) findViewById(R.id.radio_yesterday);
        radioYesterday.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_yesterday, "00:00 AM");
        setTime(R.id.txt_to_yesterday, "11:59 PM");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.module_menu_main, menu);
        // It's colour is black by default, making it white. I know this can be done in styles also :P
        menu.findItem(R.id.action_submit).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // On pressing back button in action bar
            onBackPressed();
        } else {
            Date to;
            Date from;
            Calendar toCal = Calendar.getInstance();
            Calendar fromCal = Calendar.getInstance();

            // Detecting which preset is selected
            if (radio1Hour.isChecked()) {
                to = getTime(R.id.txt_to_1_hour, null);
                fromCal = (Calendar) findViewById(R.id.txt_from_1_hour).getTag();
            } else if (radio6Hours.isChecked()) {
                to = getTime(R.id.txt_to_6_hours, null);
                fromCal = (Calendar) findViewById(R.id.txt_from_6_hours).getTag();
            } else if (radio12Hours.isChecked()) {
                to = getTime(R.id.txt_to_12_hours, null);
                fromCal = (Calendar) findViewById(R.id.txt_from_12_hours).getTag();
            } else if (radio24Hours.isChecked()) {
                to = getTime(R.id.txt_to_24_hours, null);
                fromCal = (Calendar) findViewById(R.id.txt_from_24_hours).getTag();
            } else if (radioToday.isChecked()) {
                to = getTime(R.id.txt_to_today, null);
                from = getTime(R.id.txt_from_today, null);
                fromCal.set(Calendar.HOUR_OF_DAY, from.getHours());
                fromCal.set(Calendar.MINUTE, from.getMinutes());
            } else if (radioYesterday.isChecked()) {
                to = getTime(R.id.txt_to_yesterday, null);
                from = getTime(R.id.txt_from_yesterday, null);
                toCal.add(Calendar.DAY_OF_MONTH, -1);
                fromCal.add(Calendar.DAY_OF_MONTH, -1);
                fromCal.set(Calendar.HOUR_OF_DAY, from.getHours());
                fromCal.set(Calendar.MINUTE, from.getMinutes());
            } else {
                // If custom range selected/checked
                to = getTime(R.id.txt_to_custom, TO + "\n");
                from = getTime(R.id.txt_from_custom, FROM + "\n");
                toCal.setTime(calendarView.getSelectedDate().getDate());
                fromCal.setTime(calendarView.getSelectedDate().getDate());
                fromCal.set(Calendar.HOUR_OF_DAY, from.getHours());
                fromCal.set(Calendar.MINUTE, from.getMinutes());
            }

            // Adding selected time in result calendar
            toCal.set(Calendar.HOUR_OF_DAY, to.getHours());
            toCal.set(Calendar.MINUTE, to.getMinutes());

            // Finally packaging both dates
            // And submitting the results
            Intent intent = new Intent();
            intent.putExtra(TO, toCal);
            intent.putExtra(FROM, fromCal);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        if (date.getDate().after(new Date())) {
            // if user select date ahead of current date
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            widget.setSelectedDate(Calendar.getInstance());
        } else {
            // Setting up title, it is the date selected by user with default date formatting
            getSupportActionBar().setTitle(dateFormat.format(date.getDate()));
            // collapsing app bar layout again
            appBarLayout.setExpanded(false, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.txt_from_custom) {
                // If user clicks on 'from' time in custom selection
                Date date = timeFormat.parse(((TextView) view).getText().toString().replace(FROM + "\n", "").trim());
                picker.initialize(this, date.getHours(), date.getMinutes(), date.getSeconds(), false);

                // Making validation and showing up picker time dialog
                Date temp = getTime(R.id.txt_to_custom, TO + "\n");
                picker.setMaxTime(temp.getHours(), temp.getMinutes(), temp.getSeconds());
                picker.setTitle(FROM);
                picker.show(getFragmentManager(), FROM);
            } else if (view.getId() == R.id.txt_to_custom) {
                // If user clicks on 'to' time in custom selection
                Date date = timeFormat.parse(((TextView) view).getText().toString().replace(TO + "\n", "").trim());
                picker.initialize(this, date.getHours(), date.getMinutes(), date.getSeconds(), false);

                // Making validation and showing up picker time dialog
                Date temp = getTime(R.id.txt_from_custom, FROM + "\n");
                picker.setMinTime(temp.getHours(), temp.getMinutes(), temp.getSeconds());
                picker.setTitle(TO);
                picker.show(getFragmentManager(), TO);
            } else {
                // if user clicks on 'action bar', expanding calendar
                getSupportActionBar().setTitle(radioCustom.isChecked() ? "" : SCREEN_TITLE);
                appBarLayout.setExpanded(radioCustom.isChecked(), radioCustom.isChecked());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Calendar calc = Calendar.getInstance();
        calc.set(HOUR_OF_DAY, hourOfDay);
        calc.set(MINUTE, minute);
        calc.set(SECOND, second);

        // displaying selected time on respective text-views
        TextView txtView = ((TextView) findViewById(picker.getTitle().contains(FROM)
                ? R.id.txt_from_custom : R.id.txt_to_custom));
        txtView.setText((picker.getTitle().contains(FROM) ? FROM : TO) + "\n" +
                timeFormat.format(calc.getTime()).toUpperCase());
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        appBarLayout.setExpanded(false, true);
        if (isChecked) {
            // Making selection of only single radio button
            selectRadioButton(button);
        }

        if (button.getId() == R.id.radio_custom) {
            // if checking out custom radio button
            String title = isChecked ? dateFormat.format(calendarView.getSelectedDate().getDate()) : SCREEN_TITLE;
            //collapsingToolbar.setTitle(title);
            getSupportActionBar().setTitle(title);
            View view = findViewById(R.id.layout_custom);
            view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!radioCustom.isChecked()) {
            // collapsing calendar if custom radio button is not checked
            appBarLayout.setExpanded(false, false);
        }
    }

    /**
     * A method to deselect/un-check all the visible radio buttons. Only passed radio button
     * will be checked.
     *
     * @param radio radio button which will be set up selected/checked
     */
    private void selectRadioButton(CompoundButton radio) {
        radio1Hour.setChecked(false);
        radio6Hours.setChecked(false);
        radio12Hours.setChecked(false);
        radio24Hours.setChecked(false);
        radioCustom.setChecked(false);
        radioToday.setChecked(false);
        radioYesterday.setChecked(false);

        // clicked radio button should be selected
        radio.setChecked(true);
    }

    /**
     * A method to get parsed {@link Date} instance from the text set up on {@link TextView}.
     *
     * @param id       it is the id of {@link TextView} which text will be used to make {@link Date}
     * @param truncate if there's something to be removed from the string of {@link TextView}
     * @return date instance using text on text-view
     */
    private Date getTime(int id, String truncate) {
        try {
            TextView textView = (TextView) findViewById(id);
            return timeFormat.parse(truncate == null
                    ? textView.getText().toString()
                    : textView.getText().toString().replace(truncate, "").trim());
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * A simple method to set up time on {@link TextView}.
     *
     * @param id   it is the id of {@link TextView} on which the time will displayed
     * @param time formatted time
     */
    private void setTime(int id, String time) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(time);
    }

    /**
     * A simple method to set up time on {@link TextView}.
     *
     * @param id    it is the id of {@link TextView} on which the time will displayed
     * @param hours number of hours which will be subtracted/added in the current time
     */
    private void setTime(int id, int hours) {
        TextView textView = (TextView) findViewById(id);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        textView.setText(timeFormat.format(calendar.getTime()));
        textView.setTag(calendar);
    }

    /**
     * A simple method to set up time on {@link TextView}.
     *
     * @param id     it is the id of {@link TextView} on which the time will displayed
     * @param hours  number of hours which will be subtracted/added in the current time
     * @param prefix if there's something to be prefixed in the string
     */
    private void setTime(int id, int hours, String prefix) {
        TextView textView = (TextView) findViewById(id);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hours);
        textView.setText(prefix.concat(timeFormat.format(calendar.getTime())));
    }
}