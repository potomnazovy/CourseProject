package org.example.core;

import org.example.http.HttpClient;
import org.example.http.JavaHttpClient;
import org.example.model.QueryParams;
import org.example.source.ApiSource;
import org.example.source.CryptoSource;
import org.example.source.CurrencySource;
import org.example.source.WeatherSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.TreeMap;

@Configuration
public class AppFactory
{
  @Bean
  public HttpClient httpClient()
  {
    return new JavaHttpClient();
  }

  @Bean
  public Map< String, ApiSource > apiSources()
  {
    Map< String, ApiSource > sources = new TreeMap<>();

    sources.put("weather", new WeatherSource());
    sources.put("currency", new CurrencySource());
    sources.put("crypto", new CryptoSource());

    return sources;
  }

  @Bean
  public Map< String, QueryParams > defaultParams()
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
