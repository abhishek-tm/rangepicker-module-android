package in.teramatrix.rangepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

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
@SuppressWarnings("JavaDoc")
public class TimePicker extends AppCompatActivity implements OnDateSelectedListener, View.OnClickListener,
        TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener,
        /*AppBarLayout.OnOffsetChangedListener,*/ Runnable {

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
     * A final tag to denote type of filter.
     */
    public static final String FILTER = "FILTER";
    /**
     * A final tag to input custom {@link Calendar} instance before opening time picker activity.
     */
    public static final String DAY = "DAY";
    /**
     * This is to configure filter before opening it. This tag will be used to pass custom time format to this activity.
     */
    public static final String TIME_FORMAT = "TIME";
    /**
     * This is also a part of configuration. This tag will be used to pass custom date format to this activity.
     */
    public static final String DATE_FORMAT = "DATE";
    /**
     * This is the title of the screen. It will be set up into action bar of the activity.
     * It may be customizable in the next release so it has been declared global.
     */
    public static final String SCREEN_TITLE = "TITLE";

    /**
     * A calendar instance to maintain 'from' date whenever user clicks on any {@link RadioButton}.
     * This will be submitted as a final result.
     */
    private static final Calendar calendarFrom = Calendar.getInstance();
    /**
     * A calendar instance to maintain 'to' date whenever user clicks on any {@link RadioButton}.
     * This will be submitted as a final result.
     */
    private static final Calendar calendarTo = Calendar.getInstance();
    /**
     * It is just to hold last selection of user. It will be also returned in activity results.
     */
    private static Filter filter;

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        // Default setting of date time formats and selected filter
        filter = getIntent().getSerializableExtra(FILTER) == null ? Filter.TWELVE_HOURS
                : (Filter) getIntent().getSerializableExtra(FILTER);
        dateFormat = getIntent().getSerializableExtra(DATE_FORMAT) == null ? new SimpleDateFormat("dd MMM yyyy")
                : (SimpleDateFormat) getIntent().getSerializableExtra(DATE_FORMAT);
        timeFormat = getIntent().getSerializableExtra(TIME_FORMAT) == null ? new SimpleDateFormat("HH:mm")
                : (SimpleDateFormat) getIntent().getSerializableExtra(TIME_FORMAT);

        // A calendar to allow date selection
        calendarView = (MaterialCalendarView) findViewById(R.id.calendar);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        calendarView.setPadding(0, getResources().getDimensionPixelSize(resourceId), 0, 0);
        Calendar temp = getIntent().getSerializableExtra(DAY) != null
                ? (Calendar) getIntent().getSerializableExtra(DAY)
                : Calendar.getInstance();
        calendarView.setSelectedDate(temp);
        calendarView.setCurrentDate(temp);
        calendarView.setSelected(true);
        calendarView.setOnDateChangedListener(this);

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
            actionBar.setTitle(getIntent().getStringExtra(SCREEN_TITLE) != null
                    ? getIntent().getStringExtra(SCREEN_TITLE) : "Data Setting");
        }

        // Adding offset listener to block app bar sliding on touch
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.post(this);
        //appBarLayout.addOnOffsetChangedListener(this);

        // Scrollview must be after action bar, and it should be sticky in nature
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        nestedScrollView.setNestedScrollingEnabled(false);

        // Initializing calendar view with the current date
        Calendar current = Calendar.getInstance();
        picker = TimePickerDialog.newInstance(this, current.get(HOUR_OF_DAY), current.get(MINUTE), false);

        // For custom time selection, these two text-views have been used
        findViewById(R.id.txt_from_custom).setOnClickListener(this);
        findViewById(R.id.txt_to_custom).setOnClickListener(this);

        // for the preset of one hour
        radio1Hour = (RadioButton) findViewById(R.id.radio_1_hour);
        radio1Hour.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_1_hour, -1);
        setTime(R.id.txt_to_1_hour, 0);

        // for the preset of six hours
        radio6Hours = (RadioButton) findViewById(R.id.radio_6_hours);
        radio6Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_6_hours, -6);
        setTime(R.id.txt_to_6_hours, 0);

        // for the preset of twelve hours
        radio12Hours = (RadioButton) findViewById(R.id.radio_12_hours);
        radio12Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_12_hours, -12);
        setTime(R.id.txt_to_12_hours, 0);
        calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_12_hours).getTag()).getTime());
        calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_12_hours).getTag()).getTime());

        // for the preset of twenty four hours
        radio24Hours = (RadioButton) findViewById(R.id.radio_24_hours);
        radio24Hours.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_24_hours, -24);
        setTime(R.id.txt_to_24_hours, 0);

        // for the custom time selection
        radioCustom = (RadioButton) findViewById(R.id.radio_custom);
        radioCustom.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_custom, 0, -Calendar.getInstance().get(HOUR_OF_DAY), 0);


        if (isSimilar(calendarView.getSelectedDate().getCalendar(), current)) {
            setTime(R.id.txt_to_custom, 0);
        } else {
            setTime(R.id.txt_to_custom, 0, 23 - Calendar.getInstance().get(HOUR_OF_DAY), 59);
        }

        // for today's time range selection
        radioToday = (RadioButton) findViewById(R.id.radio_today);
        radioToday.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_today, 0, -Calendar.getInstance().get(HOUR_OF_DAY), 0);
        setTime(R.id.txt_to_today, 0);

        // for the time range of previous day
        radioYesterday = (RadioButton) findViewById(R.id.radio_yesterday);
        radioYesterday.setOnCheckedChangeListener(this);
        setTime(R.id.txt_from_yesterday, -1, -Calendar.getInstance().get(HOUR_OF_DAY), 0);
        setTime(R.id.txt_to_yesterday, -1, 23 - Calendar.getInstance().get(HOUR_OF_DAY), 59);

        switch (filter) {
            case ONE_HOUR:
                radio1Hour.setChecked(true);
                break;
            case SIX_HOURS:
                radio6Hours.setChecked(true);
                break;
            case TWELVE_HOURS:
                radio12Hours.setChecked(true);
                break;
            case TWENTY_FOUR_HOURS:
                radio24Hours.setChecked(true);
                break;
            case TODAY:
                radioToday.setChecked(true);
                break;
            case YESTERDAY:
                radioYesterday.setChecked(true);
                break;
            default:
                radioCustom.setChecked(true);
        }
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
            if (!(getSupportActionBar() != null
                    && getSupportActionBar().getTitle() != null
                    && getSupportActionBar().getTitle().toString().trim().equals(""))) {
                onBackPressed();
            }
        } else {
            // Finally packaging both dates
            // And submitting the results
            Intent intent = new Intent();
            intent.putExtra(FILTER, filter);
            intent.putExtra(TO, calendarTo);
            intent.putExtra(FROM, calendarFrom);
            setResult(RESULT_OK, intent);
            finish();
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean bool) {
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

            if (isSimilar(date.getCalendar(), Calendar.getInstance())) {
                setTime(R.id.txt_to_custom, 0);
            } else {
                setTime(R.id.txt_to_custom, 0, 23 - Calendar.getInstance().get(HOUR_OF_DAY), 59);
            }

            setTime(R.id.txt_from_custom, date.getCalendar(), false);
            setTime(R.id.txt_to_custom, date.getCalendar(), false);
            onCheckedChanged(radioCustom, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onClick(View view) {
        if (view.getId() == R.id.toolbar) {
            // if user clicks on 'action bar', expanding calendar
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle(radioCustom.isChecked()
                        ? "" : (getIntent().getStringExtra(SCREEN_TITLE) != null
                                ? getIntent().getStringExtra(SCREEN_TITLE) : "Data Setting"));
            appBarLayout.setExpanded(radioCustom.isChecked(), radioCustom.isChecked());
        } else {
            // If user clicks on 'from' time in custom selection
            Calendar tagged = (Calendar) view.getTag();
            picker.initialize(this, tagged.get(HOUR_OF_DAY), tagged.get(MINUTE), tagged.get(SECOND), timeFormat.toPattern().contains("H"));
            if (view.getId() == R.id.txt_from_custom) {
                // Making validation and showing up picker time dialog for "from" time
                Calendar temp = (Calendar) findViewById(R.id.txt_to_custom).getTag();
                picker.setMaxTime(temp.get(HOUR_OF_DAY), temp.get(MINUTE), temp.get(SECOND));
                picker.setTitle(FROM);
                if (!picker.isVisible())
                    picker.show(getFragmentManager(), FROM);
            } else {
                // Making validation and showing up picker time dialog for "to" time
                Date temp = getTime(R.id.txt_from_custom);
                picker.setMinTime(temp.getHours(), temp.getMinutes(), temp.getSeconds());
                Calendar current = Calendar.getInstance();
                if (isSimilar(calendarView.getSelectedDate().getCalendar(), current)) {
                    // Setting up maximum time if selected date is today's date
                    picker.setMaxTime(current.get(HOUR_OF_DAY), current.get(MINUTE), current.get(SECOND));
                } else {
                    picker.setMaxTime(23, 59, 59);
                }
                picker.setTitle(TO);
                if (!picker.isVisible())
                    picker.show(getFragmentManager(), TO);
            }
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        Calendar selected = calendarView.getSelectedDate().getCalendar();
        selected.set(HOUR_OF_DAY, hourOfDay);
        selected.set(MINUTE, minute);
        selected.set(SECOND, second);
        setTime(picker.getTitle().contains(FROM) ? R.id.txt_from_custom : R.id.txt_to_custom, selected, true);
        onCheckedChanged(radioCustom, true);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        appBarLayout.setExpanded(false, true);
        if (isChecked) {
            // Making selection of only single radio button
            radio1Hour.setChecked(false);
            radio6Hours.setChecked(false);
            radio12Hours.setChecked(false);
            radio24Hours.setChecked(false);
            radioCustom.setChecked(false);
            radioToday.setChecked(false);
            radioYesterday.setChecked(false);

            // Only clicked radio button should be selected
            button.setChecked(true);
            findViewById(R.id.layout_custom).setVisibility(radioCustom.isChecked() ? View.VISIBLE : View.GONE);
        }

        if (button.getId() == R.id.radio_1_hour) {
            filter = Filter.ONE_HOUR;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_1_hour).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_1_hour).getTag()).getTime());
        } else if (button.getId() == R.id.radio_6_hours) {
            filter = Filter.SIX_HOURS;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_6_hours).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_6_hours).getTag()).getTime());
        } else if (button.getId() == R.id.radio_12_hours) {
            filter = Filter.TWELVE_HOURS;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_12_hours).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_12_hours).getTag()).getTime());
        } else if (button.getId() == R.id.radio_24_hours) {
            filter = Filter.TWENTY_FOUR_HOURS;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_24_hours).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_24_hours).getTag()).getTime());
        } else if (button.getId() == R.id.radio_today) {
            filter = Filter.TODAY;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_today).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_today).getTag()).getTime());
        } else if (button.getId() == R.id.radio_yesterday) {
            filter = Filter.YESTERDAY;
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_yesterday).getTag()).getTime());
            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_yesterday).getTag()).getTime());
        } else {
            // if checking out custom radio button
            filter = Filter.CUSTOM;
            //if (isChecked) onClick(findViewById(R.id.toolbar));
            getSupportActionBar().setTitle(isChecked
                    ? dateFormat.format(calendarView.getSelectedDate().getDate()) : (getIntent().getStringExtra(SCREEN_TITLE) != null
                    ? getIntent().getStringExtra(SCREEN_TITLE) : "Data Setting"));

            Calendar selected = calendarView.getSelectedDate().getCalendar();
            calendarFrom.setTime(((Calendar) findViewById(R.id.txt_from_custom).getTag()).getTime());
            calendarFrom.set(DAY_OF_MONTH, selected.get(DAY_OF_MONTH));
            calendarFrom.set(MONTH, selected.get(MONTH));
            calendarFrom.set(YEAR, selected.get(YEAR));

            calendarTo.setTime(((Calendar) findViewById(R.id.txt_to_custom).getTag()).getTime());
            calendarTo.set(DAY_OF_MONTH, selected.get(DAY_OF_MONTH));
            calendarTo.set(MONTH, selected.get(MONTH));
            calendarTo.set(YEAR, selected.get(YEAR));
        }
    }

    /*@Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (!radioCustom.isChecked()) {
            // collapsing calendar if custom radio button is not checked
            appBarLayout.setExpanded(false, false);
        }
    }*/

    @Override
    public void onBackPressed() {
        if (getSupportActionBar() != null
        && getSupportActionBar().getTitle() != null
        && getSupportActionBar().getTitle().toString().trim().equals("")) {
            // collapsing app bar layout first
            appBarLayout.setExpanded(false, true);
            // Setting up title, it is the date selected by user with default date formatting
            getSupportActionBar().setTitle(dateFormat.format(calendarView.getSelectedDate().getDate()));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void run() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
        }
    }

    /**
     * A method to get parsed {@link Date} instance from the text set up on {@link TextView}.
     *
     * @param id it is the id of {@link TextView} which text will be used to make {@link Date}
     * @return date instance using text on text-view
     */
    private Date getTime(int id) {
        try {
            TextView textView = (TextView) findViewById(id);
            return timeFormat.parse(textView.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * A method to compare two dates on the basis of day, month and year
     *
     * @param c1 first date to compare
     * @param c2 second date to compare
     * @return true if these three parameter match otherwise false
     */
    private boolean isSimilar(Calendar c1, Calendar c2) {
        return c1.get(DAY_OF_MONTH) == c2.get(DAY_OF_MONTH)
                && c1.get(MONTH) == c2.get(MONTH)
                && c1.get(YEAR) == c2.get(YEAR);
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
        calendar.add(HOUR, hours);
        // Checking whether time format contains seconds or not
        // If it doesn't contain seconds, then seconds will be set to zero
        if (!timeFormat.toPattern().toLowerCase().contains("s")) {
            calendar.set(SECOND, 0);
        }

        textView.setText(timeFormat.format(calendar.getTime()));
        textView.setTag(calendar);
    }

    /**
     * A simple method to set up time on {@link TextView}.
     *
     * @param id it is the id of {@link TextView} on which the time will displayed
     */
    private void setTime(int id, int days, int hours, @Nullable Integer integer) {
        TextView textView = (TextView) findViewById(id);
        Calendar calendar = Calendar.getInstance();
        calendar.add(DAY_OF_MONTH, days);
        calendar.add(HOUR, hours);
        if (integer != null) {
            calendar.set(MINUTE, integer);
            calendar.set(SECOND, integer);
        }
        textView.setText(timeFormat.format(calendar.getTime()));
        textView.setTag(calendar);
    }

    /**
     * A simple method to set up time on {@link TextView}.
     * * @param id it is the id of {@link TextView} on which the time will displayed
     *
     * @param selected date to be set up
     */
    private void setTime(int id, Calendar selected, boolean toBeSet) {
        TextView textView = (TextView) findViewById(id);
        Calendar tagged = (Calendar) textView.getTag();
        tagged.set(DAY_OF_MONTH, selected.get(DAY_OF_MONTH));
        tagged.set(MONTH, selected.get(MONTH));
        tagged.set(YEAR, selected.get(YEAR));
        if (toBeSet) {
            tagged.set(HOUR_OF_DAY, selected.get(HOUR_OF_DAY));
            tagged.set(MINUTE, selected.get(MINUTE));
            tagged.set(SECOND, selected.get(SECOND));
            textView.setText(timeFormat.format(tagged.getTime()));
        }
        textView.setTag(tagged);
    }

    public enum Filter {
        ONE_HOUR,
        SIX_HOURS,
        TWELVE_HOURS,
        TWENTY_FOUR_HOURS,
        TODAY,
        YESTERDAY,
        CUSTOM
    }
}