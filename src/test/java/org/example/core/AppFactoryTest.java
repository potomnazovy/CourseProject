package org.example.core;

import org.example.model.QueryParams;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppFactoryTest
{

  @Test
  void testCreateDefaultParams()
  {
    AppFactory factory = new AppFactory();
    Map< String, List< QueryParams > > params = factory.defaultParams();

    assertNotNull(params);
    assertEquals(3, params.size());

    assertTrue(params.containsKey("weather"));
    assertEquals(2, params.get("weather").size());

    assertTrue(params.containsKey("currency"));
    assertEquals(2, params.get("currency").size());

    assertTrue(params.containsKey("crypto"));
    assertEquals(2, params.get("crypto").size());

    QueryParams weather = params.get("weather").getFirst();
    assertNotNull(weather.getParams().get("latitude"));
    assertNotNull(weather.getParams().get("longitude"));

    QueryParams currency = params.get("currency").getFirst();
    assertNotNull(currency.getParams().get("base"));
    assertNotNull(currency.getParams().get("symbols"));

    QueryParams crypto = params.get("crypto").getFirst();
    assertNotNull(crypto.getParams().get("ids"));
    assertNotNull(crypto.getParams().get("vs_currencies"));
  }
}
