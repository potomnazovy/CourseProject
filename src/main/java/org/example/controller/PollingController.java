package org.example.controller;

import org.example.core.AppProperties;
import org.example.core.SchedulerService;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.model.StartRequest;
import org.example.storage.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/polling")
public class PollingController
{
  private final SchedulerService schedulerService;
  private final Map< String, List< QueryParams > > defParams;
  private final AppProperties appProperties;

  public PollingController(
    SchedulerService schedulerService,
    Map< String, List< QueryParams > > defParams,
    AppProperties appProperties)
  {
    if (schedulerService == null)
    {
      throw new IllegalArgumentException("Переданный планировщик не должен быть null");
    }

    if (defParams == null)
    {
      throw new IllegalArgumentException("Переданные параметры не должны быть null");
    }

    this.schedulerService = schedulerService;
    this.defParams = defParams;
    this.appProperties = appProperties;
  }

  @PostMapping("/start")
  public ResponseEntity< String > start(@RequestBody(required = false) StartRequest request)
  {
    if (request == null)
    {
      request = new StartRequest();
    }

    int n = (request.getN() != null)
      ? request.getN()
      : this.appProperties.getDefaultN();

    int t = (request.getT() != null)
      ? request.getT()
      : this.appProperties.getDefaultT();

    String filePath = (request.getFile() != null)
      ? request.getFile()
      : this.appProperties.getDefaultFilePath();

    String modeStr = (request.getMode() != null)
      ? request.getMode()
      : this.appProperties.getDefaultMode();

    WriteMode mode = helperWithWriteMode(modeStr);

    String format = (request.getFormat() != null)
      ? request.getFormat()
      : this.appProperties.getDefaultFormat();

    List< String > apis = (request.getApis() != null)
      ? request.getApis()
      : this.appProperties.getDefaultApis();

    Map< String, List< QueryParams > > params;

    if (request.getSources() != null)
    {
      params = convertToQueryParams(request.getSources());
    }
    else
    {
      params = this.defParams;
    }

    try
    {
      this.schedulerService.start(apis, params, filePath, mode, format, n, t);
      return ResponseEntity.ok("Опрос успешно запущен. Потоков: " + n + ", интервал: " + t + " сек.");
    }
    catch (IllegalArgumentException e)
    {
      return ResponseEntity.status(409).body(e.getMessage());
    }
  }

  @PostMapping("/stop")
  public ResponseEntity< String > stop()
  {
    this.schedulerService.stop();
    return ResponseEntity.ok("Опрос остановлен");
  }

  @GetMapping("/status")
  public ResponseEntity< Map< String, Object > > status()
  {
    return ResponseEntity.ok(Map.of("running", schedulerService.isRunning()));
  }

  @GetMapping("/data")
  public ResponseEntity< List< AggregatedRecord > > getData(@RequestParam(required = false) String source,
                                                            @RequestParam(defaultValue = "data.json") String filePath,
                                                            @RequestParam(defaultValue = "json") String format)
  {
    Storage storage = StorageFactory.createStorage(format, filePath);

    List< AggregatedRecord > list = (source == null)
      ? storage.readAll()
      : storage.readBySource(source);

    return ResponseEntity.ok(list);
  }

  private Map< String, List< QueryParams > > convertToQueryParams(
    Map< String, List< Map< String, String > > > sources)
  {
    Map< String, List< QueryParams > > result = new TreeMap<>();

    for (Map.Entry< String, List< Map< String, String > > > source : sources.entrySet())
    {
      List< QueryParams > list = new ArrayList<>();
      for (Map< String, String > map : source.getValue())
      {
        QueryParams params = new QueryParams();
        for (Map.Entry< String, String > entry : map.entrySet())
        {
          params.add(entry.getKey(), entry.getValue());
        }

        list.add(params);
      }

      result.put(source.getKey(), list);
    }

    return result;
  }

  private WriteMode helperWithWriteMode(String writeModeStr)
  {
    if (writeModeStr == null)
    {
      return WriteMode.APPEND;
    }

    return "create".equalsIgnoreCase(writeModeStr) ? WriteMode.CREATE : WriteMode.APPEND;
  }
}
