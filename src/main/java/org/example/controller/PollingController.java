package org.example.controller;

import org.example.core.SchedulerService;
import org.example.model.QueryParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/polling")
public class PollingController
{
  private final SchedulerService schedulerService;
  private final Map< String, QueryParams > defParams;

  public PollingController(SchedulerService schedulerService, Map< String, QueryParams > defParams)
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
  }

  @PostMapping("/start")
  public ResponseEntity< String > start(@RequestParam(defaultValue = "weather,currency,crypto") List< String > apis,
                                        @RequestParam(defaultValue = "data.json") String filePath,
                                        @RequestParam(defaultValue = "3") int n,
                                        @RequestParam(defaultValue = "10") int t)
  {
    try
    {
      this.schedulerService.start(apis, this.defParams, filePath, n, t);
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
}
