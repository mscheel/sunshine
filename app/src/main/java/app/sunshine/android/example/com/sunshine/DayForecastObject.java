package app.sunshine.android.example.com.sunshine;

/**
 * Created by a596969 on 9/21/14.
 */
public class DayForecastObject {
    long dt;
    WeatherObject[] weather;
    TempObject temp;

    public DayForecastObject(long dt, WeatherObject[] weather, TempObject temp) {
        super();
        this.dt = dt;
        this.weather = weather;
        this.temp = temp;
    }

    public String prettyString() {
        return weather[0].main + " " + temp.min + "/" + temp.max;
    }
}
