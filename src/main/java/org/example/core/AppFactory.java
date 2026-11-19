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

  private final AppProperties appProperties;

  public AppFactory(AppProperties appProperties)
  {
    this.appProperties = appProperties;
  }

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
    return appProperties.convertSourcesToQueryParams();
  }
}
