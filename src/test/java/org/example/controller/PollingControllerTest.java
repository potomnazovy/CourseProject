package org.example.controller;

import org.example.core.AppProperties;
import org.example.core.SchedulerService;
import org.example.model.AggregatedRecord;
import org.example.storage.JsonStorage;
import org.example.storage.WriteMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PollingController.class)
class PollingControllerTest
{
  @TempDir
  Path tempDir;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SchedulerService schedulerService;

  @MockBean
  private AppProperties appProperties;

  @BeforeEach
  public void setUp()
  {
    when(this.appProperties.getDefaultN()).thenReturn(3);
    when(this.appProperties.getDefaultT()).thenReturn(10);
    when(this.appProperties.getDefaultFilePath()).thenReturn("data.json");
    when(this.appProperties.getDefaultFormat()).thenReturn("json");
    when(this.appProperties.getDefaultMode()).thenReturn("append");
    when(this.appProperties.getDefaultApis()).thenReturn(List.of("weather", "currency", "crypto"));
  }

  @Test
  void testStartEndpoint() throws Exception
  {
    this.mockMvc.perform(post("/polling/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isOk());

    verify(this.schedulerService).start(any(), any(), any(), any(), any(), anyInt(), anyInt());
  }

  @Test
  void testStartConflict() throws Exception
  {
    doThrow(new IllegalArgumentException("Уже запущен"))
      .when(this.schedulerService).start(any(), any(), any(), any(), any(), anyInt(), anyInt());

    this.mockMvc.perform(post("/polling/start")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isConflict());
  }

  @Test
  void testStopEndpoint() throws Exception
  {
    this.mockMvc.perform(post("/polling/stop"))
      .andExpect(status().isOk());

    verify(this.schedulerService).stop();
  }

  @Test
  void testStatusEndpoint() throws Exception
  {
    when(this.schedulerService.isRunning()).thenReturn(true);

    this.mockMvc.perform(get("/polling/status"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.running").value(true));
  }

  @Test
  void testGetDataEndpoint() throws Exception
  {
    Path tempFile = tempDir.resolve("test_data.json");
    String filePath = tempFile.toString();

    AggregatedRecord record = new AggregatedRecord(
      "weather", "Berlin", "temp", 20.0, LocalDateTime.now());

    JsonStorage storage = new JsonStorage(filePath);
    storage.write(List.of(record), WriteMode.CREATE);

    this.mockMvc.perform(get("/polling/data")
        .param("format", "json")
        .param("filePath", filePath))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].source").value("weather"))
      .andExpect(jsonPath("$[0].param").value("Berlin"))
      .andExpect(jsonPath("$[0].value").value(20.0));
  }
}
