package org.example.core;

import org.example.model.QueryParams;
import org.example.source.ApiSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppFactoryTest
{
  private AppFactory factory;

  @BeforeEach
  void setUp()
  {
    AppProperties props = new AppProperties();

    props.setSources(Map.of(
      "weather", List.of(
        Map.of("latitude", "52.52", "longitude", "13.41"),
        Map.of("latitude", "55.75", "longitude", "37.62")
      ),
      "currency", List.of(
        Map.of("base", "USD", "symbols", "EUR")
      )
    ));

    this.factory = new AppFactory(props);
  }

  @Test
  void testDefaultParams()
  {
    Map< String, List< QueryParams > > params = this.factory.defaultParams();

    assertNotNull(params);
    assertEquals(2, params.size());

    assertTrue(params.containsKey("weather"));
    assertEquals(2, params.get("weather").size());
    assertEquals("52.52", params.get("weather").getFirst().getParams().get("latitude"));

    assertTrue(params.containsKey("currency"));
    assertEquals(1, params.get("currency").size());
    assertEquals("USD", params.get("currency").getFirst().getParams().get("base"));
  }

  @Test
  void testEmptySources()
  {
    AppProperties emptyProps = new AppProperties();
    emptyProps.setSources(null);

    AppFactory emptyFactory = new AppFactory(emptyProps);
    Map< String, List< QueryParams > > params = emptyFactory.defaultParams();

    assertNotNull(params);
    assertTrue(params.isEmpty());
  }

  @Test
  void testApiSources()
  {
    Map< String, ApiSource> sources = this.factory.apiSources();

    assertEquals(3, sources.size());
    assertTrue(sources.containsKey("weather"));
    assertTrue(sources.containsKey("currency"));
    assertTrue(sources.containsKey("crypto"));
  }
}
