package org.example.storage;

import org.example.exception.StorageException;
import org.example.model.AggregatedRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CsvStorage implements Storage
{
  private final String path;

  public CsvStorage(String path)
  {
    if (path == null || path.isBlank())
    {
      throw new IllegalArgumentException("Переданный файл не должен быть пустым");
    }

    this.path = path;
  }

  @Override
  public void write(List< AggregatedRecord > record, WriteMode mode)
  {
    if (record == null)
    {
      throw new IllegalArgumentException("Передаваемый объект для записи в файл не должен быть пустым");
    }

    Path currPath = Path.of(this.path);

    try
    {
      boolean fileExists = Files.exists(currPath) && Files.size(currPath) > 0;
      StandardOpenOption openOption = (mode == WriteMode.APPEND) ?
        StandardOpenOption.APPEND : StandardOpenOption.CREATE;

      try (BufferedWriter writer = Files.newBufferedWriter(currPath, StandardCharsets.UTF_8, openOption))
      {
        if ((!fileExists && mode == WriteMode.APPEND) || mode == WriteMode.CREATE)
        {
          writer.write("source,param,field,value,timestamp");
        }

        for (AggregatedRecord curr : record)
        {
          writer.newLine();
          writer.write(curr.getSource() + "," + curr.getParam() + "," +
            curr.getField() + "," + curr.getValue() + "," + curr.getTimestamp());
        }
      }
    }
    catch (IOException e)
    {
      throw new StorageException("Ошибка в записи в файл: " + this.path, e);
    }
  }

  @Override
  public List< AggregatedRecord > readAll()
  {
    Path currPath = Path.of(this.path);
    if (!Files.exists(currPath))
    {
      return new ArrayList<>();
    }

    try
    {
      List< AggregatedRecord > record = new ArrayList<>();
      List< String > lines = Files.readAllLines(currPath, StandardCharsets.UTF_8);

      for (int i = 1; i < lines.size(); ++i)
      {
        String line = lines.get(i);
        if (line.isBlank())
        {
          continue;
        }

        String[] parts = line.split(",");

        String source = parts[0];
        String param = parts[1];
        String field = parts[2];
        double value = Double.parseDouble(parts[3]);
        LocalDateTime time = LocalDateTime.parse(parts[4]);

        record.add(new AggregatedRecord(source, param, field, value, time));
      }

      return record;
    }
    catch (IOException e)
    {
      throw new StorageException("Ошибка в чтении csv файла: " + this.path, e);
    }
  }

  @Override
  public List< AggregatedRecord > readBySource(String source)
  {
    if (source == null || source.isBlank())
    {
      throw new IllegalArgumentException("Источник должен быть указан");
    }

    Path currPath = Path.of(this.path);
    if (!Files.exists(currPath))
    {
      return new ArrayList<>();
    }

    try
    {
      List< AggregatedRecord > record = new ArrayList<>();
      List< String > lines = Files.readAllLines(currPath, StandardCharsets.UTF_8);

      for (int i = 1; i < lines.size(); ++i)
      {
        String line = lines.get(i);
        if (line.isBlank())
        {
          continue;
        }

        String[] parts = line.split(",");
        if (parts[0].equals(source))
        {
          String yaSource = parts[0];
          String param = parts[1];
          String field = parts[2];
          double value = Double.parseDouble(parts[3]);
          LocalDateTime time = LocalDateTime.parse(parts[4]);

          record.add(new AggregatedRecord(yaSource, param, field, value, time));
        }
      }

      return record;
    }
    catch (IOException e)
    {
      throw new StorageException("Ошибка в чтении csv файла по заданному источнику: " + this.path, e);
    }
  }
}
