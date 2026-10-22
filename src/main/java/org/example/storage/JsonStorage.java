package org.example.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.adaptor.LocalDateTimeAdaptor;
import org.example.model.AggregatedRecord;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage implements Storage
{
  private final Gson gson = new GsonBuilder()
    .setPrettyPrinting()
    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdaptor())
    .create();

  private final String path;

  public JsonStorage(String path)
  {
    if (path == null || path.isBlank())
    {
      throw new IllegalArgumentException("Переданный файл не должен быть пустым");
    }

    this.path = path;
  }

  @Override
  public void write(List< AggregatedRecord > records, WriteMode mode)
  {
    if (records == null)
    {
      throw new IllegalArgumentException("Передаваемый объект для записи в файл не должен быть пустым");
    }

    try
    {
      List< AggregatedRecord > toWrite = records;
      if (mode == WriteMode.APPEND)
      {
        List< AggregatedRecord > all = readAll();
        all.addAll(records);
        toWrite = all;
      }

      String json = this.gson.toJson(toWrite);
      Files.writeString(Path.of(this.path), json);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Ошибка в записи в файл: " + e);
    }
  }

  @Override
  public List< AggregatedRecord > readAll()
  {
    try
    {
      Path currPath = Path.of(this.path);
      if (!Files.exists(currPath))
      {
        return new ArrayList<>();
      }

      String json = Files.readString(currPath);

      Type aggragatedType = new TypeToken< List< AggregatedRecord > >(){}.getType();
      return this.gson.fromJson(json, aggragatedType);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Ошибка в чтении json файла: " + e);
    }
  }

  @Override
  public List< AggregatedRecord > readBySource(String source)
  {
    if (source == null || source.isBlank())
    {
      throw new IllegalArgumentException("Источник должен быть указан");
    }

    try
    {
      Path currPath = Path.of(this.path);
      if (!Files.exists(currPath))
      {
        return new ArrayList<>();
      }

      String json = Files.readString(currPath);

      Type aggregatedType = new TypeToken< List< AggregatedRecord > >(){}.getType();
      List< AggregatedRecord > records = this.gson.fromJson(json, aggregatedType);

      if (records == null)
      {
        return new ArrayList<>();
      }

      List< AggregatedRecord > result = new ArrayList<>();

      for (AggregatedRecord record : records)
      {
        if (record.getSource().equals(source))
        {
          result.add(record);
        }
      }

      return result;
    }
    catch (IOException e)
    {
      throw new RuntimeException("Ошибка в чтении json файла по заданному источнику: " + e);
    }
  }
}
