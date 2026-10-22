package org.example;

import org.example.cli.CliOptions;
import org.example.cli.CliParser;
import org.example.cli.Command;
import org.example.core.AggregateService;
import org.example.core.AppFactory;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.storage.CsvStorage;
import org.example.storage.JsonStorage;
import org.example.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Main
{
  public static void main(String[] args)
  {
    Logger log = LoggerFactory.getLogger(Main.class);
    try
    {
      CliParser cli = new CliParser();
      CliOptions options = cli.parse(args);

      AppFactory factory = new AppFactory();
      AggregateService service = factory.createService();

      Map< String, QueryParams > params = factory.createDefaultParams();

      Storage storage;
      if (options.getFormat().equalsIgnoreCase("csv"))
      {
        storage = new CsvStorage(options.getFilePath());
      }
      else
      {
        storage = new JsonStorage(options.getFilePath());
      }

      if (options.getCommand() == Command.POLL)
      {
        log.info("Запуск опроса API: {}", options.getApis());
        List< AggregatedRecord > records = service.aggregate(options.getApis(), params);

        storage.write(records, options.getMode());
        log.info("Успешно сохранено записей: {} в файл {}", records.size(), options.getFilePath());
      }
      else if (options.getCommand() == Command.SHOW)
      {
        log.info("Чтение файла: {}", options.getFilePath());

        List< AggregatedRecord > records;

        if (options.getSourceFilter() != null)
        {
          records = storage.readBySource(options.getSourceFilter());
        }
        else
        {
          records = storage.readAll();
        }

        log.info("Найдено записей: {}", records.size());
        for (AggregatedRecord record : records)
        {
          System.out.println(record);
        }
      }
    }
    catch (RuntimeException e)
    {
      log.error("Ошибка выполнения: {} ", e.getMessage());
    }
  }
}
