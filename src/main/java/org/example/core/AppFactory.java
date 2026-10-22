package org.example.core;

import org.example.http.HttpClient;
import org.example.http.JavaHttpClient;
import org.example.model.QueryParams;
import org.example.source.ApiSource;
import org.example.source.CryptoSource;
import org.example.source.CurrencySource;
import org.example.source.WeatherSource;

import java.util.Map;
import java.util.TreeMap;

public class AppFactory
{
  public AggregateService createService()
  {
    HttpClient httpClient = new JavaHttpClient();
    Map< String, ApiSource > sources = createSources();
    return new AggregateService(sources, httpClient);
  }

  public Map< String, ApiSource > createSources()
  {
    Map< String, ApiSource > sources = new TreeMap<>();

    sources.put(new WeatherSource().getName(), new WeatherSource());
    sources.put(new CurrencySource().getName(), new CurrencySource());
    sources.put(new CryptoSource().getName(), new CryptoSource());

    return sources;
  }

  public Map< String, QueryParams > createDefaultParams()
  {
    Map< String, QueryParams > defParams = new TreeMap<>();

    QueryParams weather = new QueryParams();
    weather.add("latitude", "52.52");
    weather.add("longitude", "13.41");
    weather.add("current", "temperature_2m,wind_speed_10m");
    weather.add("hourly", "temperature_2m,wind_speed_10m");

    defParams.put("weather", weather);

    QueryParams currency = new QueryParams();
    currency.add("base", "USD");
    currency.add("symbols", "EUR,GBP,JPY");

    defParams.put("currency", currency);

    QueryParams crypto = new QueryParams();
    crypto.add("ids", "bitcoin,ethereum");
    crypto.add("vs_currencies", "usd,eur");
    crypto.add("include_market_cap", "true");
    crypto.add("include_24hr_vol", "true");

    defParams.put("crypto", crypto);

    return defParams;
  }
}
