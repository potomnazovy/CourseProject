package org.example.core;

import jakarta.annotation.PreDestroy;
import org.example.exception.StorageException;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.storage.CsvStorage;
import org.example.storage.JsonStorage;
import org.example.storage.Storage;
import org.example.storage.WriteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
@Service
public class SchedulerService
{
  private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
  private final AggregateService aggregateService;
  private ExecutorService executorService;

  private final AtomicBoolean isRunning = new AtomicBoolean(false);

  public SchedulerService(AggregateService aggregateService)
  {
    this.aggregateService = aggregateService;
  }

  public synchronized void start(List< String > apis,
                                 Map< String, List< QueryParams > > params,
                                 String filePath,
                                 WriteMode mode,
                                 String format,
                                 int n,
                                 int t)
  {
    if (this.isRunning.get())
    {
      throw new IllegalArgumentException("Опрос API уже запущен");
    }

    this.isRunning.set(true);

    this.executorService = Executors.newFixedThreadPool(n);

    Storage storage;

    if ("csv".equalsIgnoreCase(format))
    {
      storage = new CsvStorage(filePath);
    }
    else
    {
      storage = new JsonStorage(filePath);
    }

    for (String apiName : apis)
    {
      List< QueryParams > currParams = params.get(apiName);

      if (currParams == null)
      {
        continue;
      }

      for (QueryParams query : currParams)
      {
        this.executorService.submit(new Runnable()
        {
          @Override
          public void run()
          {
            while (!Thread.currentThread().isInterrupted())
            {
              try
              {
                log.info("[START] {} params={}", apiName, query.getParams());

                List< AggregatedRecord > records = aggregateService.aggregate(
                  Collections.singletonList(apiName),
                  Map.of(apiName, query));

                storage.write(records, mode);

                log.info("[END] {} OK, saved {} records", apiName, records.size());

                Thread.sleep(t * 1000L);
              }
              catch (InterruptedException e)
              {
                log.info("Поток для {} был прерван. Завершаем работу.", apiName);
                Thread.currentThread().interrupt();
                break;
              }
              catch (StorageException e)
              {
                log.error("[END] {} ERROR (Storage): {}", apiName, e.getMessage());
                sleepQuietly(t);
              }
              catch (RuntimeException e)
              {
                log.error("[END] {} ERROR (Runtime): {}", apiName, e.getMessage());
                sleepQuietly(t);
              }
            }
          }
        });
      }
    }
  }

  public void stop()
  {
    if (!this.isRunning.get())
    {
      return;
    }

    log.info("Остановка планировщика...");

    this.executorService.shutdownNow();

    try
    {
      if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS))
      {
        log.warn("Потоки не завершились за 5 секунд");
      }
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }

    this.isRunning.set(false);
  }

  @PreDestroy
  public void cleanUp()
  {
    stop();
  }

  private void sleepQuietly(int t)
  {
    try
    {
      Thread.sleep(t * 1000L);
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    }
  }

  public boolean isRunning()
  {
    return isRunning.get();
  }
}
