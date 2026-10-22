package org.example.source;

import com.google.gson.Gson;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.source.dto.WeatherResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WeatherSource implements ApiSource
{
  private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";

  @Override
  public String getName()
  {
    return "weather";
  }

  @Override
  public String buildUrl(QueryParams params)
  {
    return BASE_URL + "?" + params.toQueryString();
  }

  @Override
  public List< AggregatedRecord > parse(String json, QueryParams params)
  {
    Gson gson = new Gson();

    WeatherResponse response = gson.fromJson(json, WeatherResponse.class);

    List< AggregatedRecord > list = new ArrayList<>();

    String latitude = params.getParams().getOrDefault("latitude", "0");
    String longitude = params.getParams().getOrDefault("longitude", "0");
    String location = latitude + ";" + longitude;

    LocalDateTime time = LocalDateTime.parse(response.getCurrent().getTime());

    list.add(new AggregatedRecord("weather", location, "temperature_2m",
      response.getCurrent().getTemperature_2m(), time));

    list.add(new AggregatedRecord("weather", location, "wind_speed_10m",
      response.getCurrent().getWind_speed_10m(), time));

    WeatherResponse.Hourly hourly = response.getHourly();

    if (hourly != null && hourly.getTime() != null)
    {
      List< String > times = hourly.getTime();
      List< Double > temps = hourly.getTemperature_2m();
      List< Double > winds = hourly.getWind_speed_10m();

      int limit = Math.min(24, times.size());

      for (int i = 0; i < limit; ++i)
      {
        if (temps != null && temps.size() > i)
        {
          LocalDateTime forecastTime = LocalDateTime.parse(times.get(i));
          double forecastTemp = temps.get(i);

          list.add(new AggregatedRecord("weather", location,
            "hourly_temperature", forecastTemp, forecastTime));
        }
      }

      for (int i = 0; i < limit; ++i)
      {
        if (winds != null && winds.size() > i)
        {
          LocalDateTime forecastTime = LocalDateTime.parse(times.get(i));
          double forecastWind = winds.get(i);

          list.add(new AggregatedRecord("weather", location,
            "hourly_wind", forecastWind, forecastTime));
        }
      }
    }

    return list;
  }
}
