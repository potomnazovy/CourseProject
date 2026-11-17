package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QueryParamsTest
{
  private QueryParams params;

  @BeforeEach
  public void setUp()
  {
    this.params = new QueryParams();
  }

  @Test
  public void testCorrectAdd()
  {
    String key1 = "latitude";
    String value1 = "52.52";

    String key2 = "longitude";
    String value2 = "13.41";

    this.params.add(key1, value1);

    assertEquals(1, this.params.getParams().size());

    String mapValueAfterAdd1 = this.params.getParams().get("latitude");
    assertEquals("52.52", mapValueAfterAdd1);

    this.params.add(key2, value2);

    assertEquals(2, this.params.getParams().size());

    String mapValueAfterAdd2 = this.params.getParams().get("longitude");
    assertEquals("13.41", mapValueAfterAdd2);
  }

  @Test
  public void testIncorrectAdd()
  {
    assertThrows(IllegalArgumentException.class, () ->
      this.params.add(null, "value"));

    assertThrows(IllegalArgumentException.class, () ->
      this.params.add("key", null));
  }

  @Test
  public void testToQueryString()
  {
    assertEquals("", this.params.toQueryString());

    params.add("base", "USD");
    assertEquals("base=USD", params.toQueryString());

    params.add("symbols", "EUR,GBP");
    String result = params.toQueryString();

    assertTrue(result.contains("base=USD"));
    assertTrue(result.contains("symbols=EUR,GBP"));
    assertTrue(result.contains("&"));
  }
}
