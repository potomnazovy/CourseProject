package org.example.source;

import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SourceTest
{
  private static final String BASE_URL_WEATHER = "https://api.open-meteo.com/v1/forecast";
  private static final String BASE_URL_CURRENCY = "https://api.frankfurter.dev/v1/latest";
  private static final String BASE_URL_CRYPTO = "https://api.coingecko.com/api/v3/simple/price";

  private QueryParams query;

  @BeforeEach
  public void setUp()
  {
    this.query = new QueryParams();
  }

  @Test
  public void testBuildUrlWeather()
  {
    this.query.add("latitude", "52.52");
    this.query.add("longitude", "13.41");

    String yaString = BASE_URL_WEATHER + "?" + this.query.toQueryString();

    WeatherSource source = new WeatherSource();

    assertEquals(yaString, source.buildUrl(this.query));
  }

  @Test
  public void testBuildUrlCurrency()
  {
    this.query.add("base", "USD");
    this.query.add("symbols", "EUR,GBP");

    String yaString = BASE_URL_CURRENCY + "?" + this.query.toQueryString();

    CurrencySource source = new CurrencySource();

    assertEquals(yaString, source.buildUrl(this.query));
  }

  @Test
  public void testBuildUrlCrypto()
  {
    this.query.add("ids", "bitcoin");
    this.query.add("vs_currencies", "usd,eur");

    String yaString = BASE_URL_CRYPTO + "?" + this.query.toQueryString();

    CryptoSource source = new CryptoSource();

    assertEquals(yaString, source.buildUrl(this.query));
  }

  @Test
  public void testWeatherSourceParse()
  {
    String json = """
    {
      "current": {
        "time": "2026-11-17T12:00",
        "temperature_2m": 15.5,
        "wind_speed_10m": 5.2
      },
      "hourly": {
        "time": ["2026-11-17T13:00"],
        "temperature_2m": [16.0],
        "wind_speed_10m": [6.0]
      }
    }
    """;

    this.query.add("latitude", "52.52");
    this.query.add("longitude", "13.41");

    WeatherSource source = new WeatherSource();
    List< AggregatedRecord > result = source.parse(json, this.query);

    assertEquals(4, result.size());
    assertEquals("weather", result.getFirst().getSource());
    assertEquals("temperature_2m", result.getFirst().getField());
    assertEquals(15.5, result.getFirst().getValue(), 0.001);
  }

  @Test
  void testCurrencySourceParse()
  {
    String json = """
    {
      "base": "USD",
      "date": "2026-11-17",
      "rates": { "EUR": 0.91, "GBP": 0.75 }
    }
    """;

    CurrencySource source = new CurrencySource();
    List< AggregatedRecord > result = source.parse(json, this.query);

    assertEquals(2, result.size());
    assertEquals("currency", result.getFirst().getSource());
    assertEquals("USD", result.getFirst().getParam());
    assertEquals("EUR", result.getFirst().getField());
    assertEquals(0.91, result.getFirst().getValue(), 0.001);
  }

  @Test
  void testCryptoSourceParse()
  {
    String json = """
    {
      "bitcoin": {
        "usd": 60000,
        "eur": 55000,
        "usd_market_cap": 1200000000000,
        "usd_24h_vol": 30000000000
      }
    }
    """;

    CryptoSource source = new CryptoSource();
    List< AggregatedRecord > result = source.parse(json, this.query);

    assertEquals(4, result.size());
    assertEquals("crypto", result.getFirst().getSource());
    assertEquals("bitcoin", result.getFirst().getParam());
  }
}
