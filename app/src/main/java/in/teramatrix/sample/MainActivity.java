package in.teramatrix.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import in.teramatrix.rangepicker.TimePicker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView textView;
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        findViewById(R.id.button).setOnClickListener(this);
        textView = (TextView) findViewById(R.id.text_view);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, TimePicker.class);
        intent.putExtra(TimePicker.TIME_FORMAT, new SimpleDateFormat("HH:mm"));
        intent.putExtra(TimePicker.DATE_FORMAT, new SimpleDateFormat("dd MMM yyyy"));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -2);
        intent.putExtra(TimePicker.DAY, calendar);

        startActivityForResult(intent, 1992);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1992) {
            Calendar from = (Calendar) data.getSerializableExtra(TimePicker.FROM);
            Calendar to = (Calendar) data.getSerializableExtra(TimePicker.TO);
            textView.setText(format.format(from.getTime()) + "\n\n⇩\n\n" + format.format(to.getTime()));
        }
    }
}