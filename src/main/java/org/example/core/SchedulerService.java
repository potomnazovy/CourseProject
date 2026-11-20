package org.example.core;

import jakarta.annotation.PreDestroy;
import org.example.exception.StorageException;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.storage.Storage;
import org.example.storage.StorageFactory;
import org.example.storage.WriteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SchedulerService
{
  private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);
  private final AggregateService aggregateService;

  private ScheduledExecutorService scheduledExecutor;

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

    this.scheduledExecutor = Executors.newScheduledThreadPool(n);

    Storage storage = StorageFactory.createStorage(format, filePath);

    for (String apiName : apis)
    {
      List< QueryParams > currParams = params.get(apiName);

      if (currParams == null)
      {
        continue;
      }

      for (QueryParams query : currParams)
      {
        scheduledExecutor.scheduleWithFixedDelay(
          () -> executePollingTask(apiName, query, storage, mode),
          0,
          t,
          TimeUnit.SECONDS
        );
      }
    }

    log.info("Планировщик запущен. Потоков: {}, интервал: {} сек.", n, t);
  }

  private void executePollingTask(String apiName,
                                  QueryParams query,
                                  Storage storage,
                                  WriteMode mode)
  {
    try
    {
      log.info("[START] {} {}", apiName, query.getParams());

      List<AggregatedRecord> records = aggregateService.aggregate(
        Collections.singletonList(apiName),
        Map.of(apiName, query));

      storage.write(records, mode);

      log.info("[END] {} OK", apiName);
    }
    catch (StorageException e)
    {
      log.error("[END] {} ERROR (Storage): {}", apiName, e.getMessage());
    }
    catch (RuntimeException e)
    {
      log.error("[END] {} ERROR ({}): {}", apiName, e.getClass().getSimpleName(), e.getMessage());
    }
  }

  public void stop()
  {
    if (!this.isRunning.get())
    {
      return;
    }

    log.info("Остановка планировщика...");

    this.scheduledExecutor.shutdownNow();

    try
    {
      if (!this.scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS))
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

  public boolean isRunning()
  {
    return isRunning.get();
  }
}
