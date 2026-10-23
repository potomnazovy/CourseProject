package org.example.core;

import com.google.gson.JsonSyntaxException;
import org.example.exception.AppException;
import org.example.http.HttpClient;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.source.ApiSource;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregateService
{
  private static final Logger logger = LoggerFactory.getLogger(AggregateService.class);

  private final Map< String, ApiSource > sources;
  private final HttpClient httpClient;

  public AggregateService(Map< String, ApiSource > sources, HttpClient httpClient)
  {
    if (sources == null)
    {
      throw new IllegalArgumentException("Передаваемое дерево API не должно быть пустым");
    }

    if (httpClient == null)
    {
      throw new IllegalArgumentException("Параметр httpclient не должен быть пустым");
    }

    this.sources = new TreeMap<>(sources);
    this.httpClient = httpClient;
  }

  public List< AggregatedRecord > aggregate(List< String > apiNames, Map< String, QueryParams > paramsMap)
  {
    if (apiNames == null)
    {
      throw new IllegalArgumentException("Передаваемые имена API не должны быть пустыми");
    }

    List< AggregatedRecord > records = new ArrayList<>();

    for (String name : apiNames)
    {
      ApiSource api = this.sources.get(name);
      if (api == null)
      {
        logger.error("Неизвестный источник API: {}", name);
        continue;
      }

      QueryParams params = paramsMap.getOrDefault(name, new QueryParams());

      try
      {
        String url = api.buildUrl(params);
        String json = this.httpClient.get(url);
        List< AggregatedRecord > curr = api.parse(json, params);

        records.addAll(curr);
      }
      catch (IOException e)
      {
        logger.error("Сетевая ошибка при опросе {} из-за HTTP клиента: {}", name, e.getMessage());
      }
      catch (InterruptedException e)
      {
        logger.error("Опрос API {} был прерван: {}", name, e.getMessage());
        Thread.currentThread().interrupt();
      }
      catch (AppException e)
      {
        logger.error("Ошибка приложения при опросе {}: {}", name, e.getMessage());
      }
      catch (JsonSyntaxException e)
      {
        logger.error("Ошибка при опросе {} из-за парсинга Gson: {}", name, e.getMessage());
      }
      catch (DateTimeParseException e)
      {
        logger.error("Ошибка при опросе {} из-за парсинга даты: {}", name, e.getMessage());
      }
      catch (RuntimeException e)
      {
        logger.error("Непредвиденная ошибка при опросе {}: [{}] {}",
          name, e.getClass().getSimpleName(), e.getMessage());
      }
    }

    return records;
  }
}
