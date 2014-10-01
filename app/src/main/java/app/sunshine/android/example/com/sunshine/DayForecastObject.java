package app.sunshine.android.example.com.sunshine;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by a596969 on 9/21/14.
 */
public class DayForecastObject {
    long dt;
    WeatherObject[] weather;
    TempObject temp;
    Context mContext;

    public DayForecastObject(long dt, WeatherObject[] weather, TempObject temp) {
        super();
        this.dt = dt;
        this.weather = weather;
        this.temp = temp;
    }

    public String prettyString(Context context) {
        mContext = context;
        return weather[0].main + " " + format(temp.min) + "/" + format(temp.max);
    }

    public String format(double val) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String units = prefs.getString(mContext.getString(R.string.pref_units_key), mContext.getString(R.string.pref_units_metric));
        if(units.equals(mContext.getString(R.string.pref_units_metric))) {
            return String.valueOf(val);
        } else {
            double val2 = (val * 1.8) + 32;
            return String.valueOf(Math.round(val2));
        }

    }
}
