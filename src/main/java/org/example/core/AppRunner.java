package org.example.core;

import org.example.cli.CliOptions;
import org.example.cli.Command;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.storage.CsvStorage;
import org.example.storage.JsonStorage;
import org.example.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AppRunner
{
  private static final Logger log = LoggerFactory.getLogger(AppRunner.class);
  private final AppFactory factory;

  public AppRunner(AppFactory factory)
  {
    this.factory = factory;
  }

  public void run(CliOptions options)
  {
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
      executePoll(storage, options);
    }
    else if (options.getCommand() == Command.SHOW)
    {
      executeShow(storage, options);
    }
  }

  private void executePoll(Storage storage, CliOptions options)
  {
    AggregateService service = factory.createService();
    Map< String, QueryParams > queryParamsMap = factory.createDefaultParams();

    log.info("Запуск опроса API: {}", options.getApis());
    List< AggregatedRecord > records = service.aggregate(options.getApis(), queryParamsMap);

    storage.write(records, options.getMode());
    log.info("Успешно сохранено записей: {} в файл {}", records.size(), options.getFilePath());
  }

  private void executeShow(Storage storage, CliOptions options)
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
