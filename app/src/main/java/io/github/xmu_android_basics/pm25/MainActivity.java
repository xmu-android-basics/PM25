package io.github.xmu_android_basics.pm25;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the cities of PM2.5 dataset */
    private static final String PM25_CITIES_REQUEST_URL =
            "http://www.pm25.in/api/querys.json?token=5j1znBVAsnSf5xQyNQyq";

    private String citiesResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        new QueryDataTask(this).execute(PM25_CITIES_REQUEST_URL);
    }

    /**
     * Update the screen to display information from the given response.
     */
    public void updateUi(String response) {
        TextView responseTextView = (TextView) findViewById(R.id.response);

        responseTextView.setText(response);
    }
}
