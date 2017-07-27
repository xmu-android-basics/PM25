package io.github.xmu_android_basics.pm25;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the cities of PM2.5 dataset */
    public static final String PM25_CITIES_REQUEST_URL =
            "http://www.pm25.in/api/querys.json?token=5j1znBVAsnSf5xQyNQyq";

    private String citiesResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLoaderManager().initLoader(0, null, this);
    }

    public void onButtonClick(View view) {
        getLoaderManager().restartLoader(0, null, this);
    }

    /**
     * Update the screen to display information from the given response.
     */
    private void updateUi(String response) {
        TextView responseTextView = (TextView) findViewById(R.id.response);

        responseTextView.setText(response);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new QueryDataLoader(this, PM25_CITIES_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String response) {
        updateUi(response);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        updateUi("");
    }

}
