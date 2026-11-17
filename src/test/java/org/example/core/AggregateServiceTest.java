package org.example.core;

import org.example.http.HttpClient;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.source.ApiSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AggregateServiceTest
{
  @Mock
  private HttpClient httpMock;

  @Mock
  private ApiSource apiMock;

  @Test
  public void testCorrectAggregate() throws Exception
  {
    when(this.httpMock.get(anyString())).thenReturn("{}");
    when(this.apiMock.buildUrl(any())).thenReturn("https://test.com");
    when(this.apiMock.parse(anyString(), any())).thenReturn(
      List.of(new AggregatedRecord(
        "test",
        "p",
        "f",
        0.0,
        null))
    );

    AggregateService service = new AggregateService(Map.of("test", this.apiMock), this.httpMock);
    List< AggregatedRecord > list = service.aggregate(
      List.of("test"),
      Map.of("test", new QueryParams()));

    assertEquals(1, list.size());
    assertEquals("test", list.getFirst().getSource());
  }

  @Test
  public void testIncorrectAggregateBecauseOfUnknownApi()
  {
    AggregateService service = new AggregateService(Collections.emptyMap(), this.httpMock);
    List< AggregatedRecord > list = service.aggregate(
      List.of("unknown"),
      Map.of("unknown", new QueryParams()));

    assertEquals(0, list.size());
  }

  @ParameterizedTest
  @CsvSource(value =
  {
    "NULL, valid",
    "valid, NULL"
  }, nullValues = "NULL")
  public void testIncorrectCreate(String sourcesType, String httpClientType)
  {
    Map< String, ApiSource > sources = (sourcesType == null)
      ? null
      : Collections.emptyMap();

    HttpClient client = (httpClientType == null)
      ? null
      : this.httpMock;

    assertThrows(IllegalArgumentException.class, () ->
      new AggregateService(sources, client));
  }
}
