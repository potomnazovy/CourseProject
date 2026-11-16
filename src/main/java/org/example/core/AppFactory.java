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

import java.util.List;
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
  public Map< String, List< QueryParams > > defaultParams()
  {
    Map< String, List< QueryParams > > defParams = new TreeMap<>();

    defParams.put("weather", List.of(
      createWeatherParams("52.52", "13.41"),
      createWeatherParams("55.75", "37.62")));

    defParams.put("currency", List.of(
      createCurrencyParams("USD", "EUR,GBP,JPY"),
      createCurrencyParams("EUR", "USD,GBP,JPY")));

    defParams.put("crypto", List.of(
      createCryptoParams("bitcoin"),
      createCryptoParams("ethereum")));

    return defParams;
  }

  private QueryParams createWeatherParams(String latitude, String longitude)
  {
    QueryParams p = new QueryParams();

    p.add("latitude", latitude);
    p.add("longitude", longitude);
    p.add("current", "temperature_2m,wind_speed_10m");
    p.add("hourly", "temperature_2m,wind_speed_10m");

    return p;
  }

  private QueryParams createCryptoParams(String coinId)
  {
    QueryParams p = new QueryParams();

    p.add("ids", coinId);
    p.add("vs_currencies", "usd,eur");
    p.add("include_market_cap", "true");
    p.add("include_24hr_vol", "true");

    return p;
  }

  private QueryParams createCurrencyParams(String base, String symbols)
  {
    QueryParams p = new QueryParams();

    p.add("base", base);
    p.add("symbols", symbols);

    return p;
  }
}
