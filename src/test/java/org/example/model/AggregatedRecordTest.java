package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AggregatedRecordTest
{
  private AggregatedRecord record;
  private LocalDateTime time;

  @BeforeEach
  public void setUp()
  {
    this.time = LocalDateTime.now();
    this.record = new AggregatedRecord(
      "weather",
      "Berlin",
      "temperature",
      25.5,
      this.time);
  }

  @Test
  public void testValidRecordCreation()
  {
    assertEquals("weather", this.record.getSource());
    assertEquals("Berlin", this.record.getParam());
    assertEquals("temperature", this.record.getField());
    assertEquals(25.5, this.record.getValue(), 0.001);
    assertEquals(this.time, this.record.getTimestamp());
  }

  @ParameterizedTest
  @CsvSource(value =
  {
    "NULL, Berlin, temp",
    "weather, NULL, temp",
    "weather, Berlin, NULL"
  }, nullValues = "NULL")
  public void testInvalidRecordCreation(String source, String param, String field)
  {
    assertThrows(IllegalArgumentException.class, () ->
      new AggregatedRecord(source, param, field, 25.5, this.time));
  }

  @Test
  public void testToString()
  {
    this.time = LocalDateTime.of(2026, 11, 17, 12, 0);
    this.record.setTimestamp(this.time);

    String result = this.record.toString();

    assertTrue(result.contains("source='weather'"));
    assertTrue(result.contains("param='Berlin'"));
    assertTrue(result.contains("field='temperature'"));
    assertTrue(result.contains("value=25.5"));
    assertTrue(result.contains("timestamp=2026-11-17T12:00"));
  }
}
