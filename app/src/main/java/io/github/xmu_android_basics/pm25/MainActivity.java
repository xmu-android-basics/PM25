package io.github.xmu_android_basics.pm25;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

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

    private static class QueryDataLoader extends AsyncTaskLoader<String> {

        public static final String REQUEST_URL_KEY = "REQUEST_URL";

        private String mUrlString;

        public QueryDataLoader(Context context) {
            super(context);
        }

        public QueryDataLoader(Context context, Bundle args) {
            super(context);

            mUrlString = args.getString(REQUEST_URL_KEY, MainActivity.PM25_CITIES_REQUEST_URL);
        }

        public QueryDataLoader(Context context, String urlString) {
            super(context);

            if (TextUtils.isEmpty(urlString)) {
                mUrlString = MainActivity.PM25_CITIES_REQUEST_URL;
            } else {
                mUrlString = urlString;
            }
        }

        @Override
        public String loadInBackground() {
            return queryData(mUrlString);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        private String queryData(String queryString) {
            // Create URL object
            URL url = createUrl(queryString);

            // Perform HTTP request to the URL and receive a JSON response back
            String response = "";
            try {
                response = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
            }

            return response;
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String response = "";

            // If the URL is null, then return early.
            if (url == null) {
                return response;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                // If the request was successful (response code 200),
                // then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    response = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return response;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
