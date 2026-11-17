package org.example.core;

import org.example.model.QueryParams;
import org.example.storage.WriteMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SchedulerServiceTest
{
  @TempDir
  Path tempDir;

  @Mock
  private AggregateService serviceMock;

  private SchedulerService schedulerService;

  @BeforeEach
  public void setUp()
  {
    when(this.serviceMock.aggregate(anyList(), anyMap())).thenReturn(Collections.emptyList());
    this.schedulerService = new SchedulerService(this.serviceMock);
  }

  @Test
  public void testStartAndStop()
  {
    helper();

    assertTrue(this.schedulerService.isRunning());

    this.schedulerService.stop();

    assertFalse(this.schedulerService.isRunning());
  }

  @Test
  public void testDoubleStart()
  {
    helper();

    assertThrows(IllegalArgumentException.class, this::helper);

    this.schedulerService.stop();
  }

  @Test
  public void testStopWithoutStart()
  {
    assertFalse(this.schedulerService.isRunning());

    assertDoesNotThrow(() -> this.schedulerService.stop());

    assertFalse(this.schedulerService.isRunning());
  }

  @Test
  public void testStartAndStopWithEmptyApis()
  {
    assertDoesNotThrow(() -> this.schedulerService.start(
      Collections.emptyList(),
      Collections.emptyMap(),
      tempDir.resolve("empty_apis.json").toString(),
      WriteMode.APPEND,
      "json",
      1,
      1));

    assertTrue(this.schedulerService.isRunning());

    assertDoesNotThrow(() -> this.schedulerService.stop());

    assertFalse(this.schedulerService.isRunning());
  }

  private void helper()
  {
    String filePath = tempDir.resolve("scheduler_test.json").toString();

    this.schedulerService.start(
      List.of("weather"),
      Map.of("weather", List.of(new QueryParams())),
      filePath,
      WriteMode.APPEND,
      "json",
      1,
      1);
  }
}
