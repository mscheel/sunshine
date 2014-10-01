package app.sunshine.android.example.com.sunshine;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity {

    static List<String> mWeekForecast;
    PlaceholderFragment mPlaceHolderFragment;
    ArrayAdapter mForecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            mPlaceHolderFragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPlaceHolderFragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (id == R.id.action_map) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            showMap(Uri.parse("geo:0,0?q=" + location), getApplicationContext());
        } else if (id == R.id.action_refresh) {
            updateData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMap(Uri geolocation, Context context) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setData(geolocation);
        if(mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(context, "no map", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateData() {
        Toast.makeText(this, "refresh", Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        JsonObjectRequest request = new JsonObjectRequest("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + location + ",co&mode=json&units=metric&cnt=7", null,
        new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d("MARK", response.toString());
                final Gson gson = new Gson();
                try {
                    JSONArray list = response.getJSONArray("list");
                    DayForecastObject[] dfoArray = gson.fromJson(list.toString(), DayForecastObject[].class);
                    mWeekForecast = new ArrayList<String>();
                    for (int i = 0; i < dfoArray.length; i++) {
                        mWeekForecast.add(dfoArray[i].prettyString(getApplicationContext()));
                    }
                    Log.d("MARK", "list size: " + mWeekForecast.size());
                    mPlaceHolderFragment.refreshListView();
//                                Toast.makeText(getApplicationContext(), dfoArray[0].weather[0].main + " " + dfoArray[0].temp.max, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },

        new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MARK", error.toString());
                Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
);
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {

        ListView mListView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Log.d("MARK", "ocv");

            if (mWeekForecast == null) {
                Log.d("MARK", "ocv 2");
                String[] forecastArray = new String[]{"Today - Sunny - 88/63", "Tomorrow - Cold - 32/53", "Future - Meatballs - 62/64"};

                mWeekForecast = Arrays.asList(forecastArray);
            }
            mForecastAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    mWeekForecast
            );

            mListView = (ListView) rootView.findViewById(R.id.listview_forecast);
            mListView.setAdapter(mForecastAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent detailintent = new Intent(getApplicationContext(), DetailActivity.class);
                    detailintent.putExtra(Intent.EXTRA_TEXT, "weather: " + mWeekForecast.get(i));
                    startActivity(detailintent);
                }
            });

            return rootView;
        }

        public void refreshListView() {

            for (String s : mWeekForecast) {
                Log.d("MARK", s);
            }

            mForecastAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_forecast,
                    R.id.list_item_forecast_textview,
                    mWeekForecast
            );

            mForecastAdapter.notifyDataSetChanged();

            mListView.setAdapter(mForecastAdapter);
        }

    }
}
