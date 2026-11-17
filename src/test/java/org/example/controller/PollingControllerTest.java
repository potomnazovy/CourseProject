package org.example.controller;

import org.example.core.SchedulerService;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.storage.JsonStorage;
import org.example.storage.WriteMode;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PollingController.class)
class PollingControllerTest
{
  @TempDir
  Path tempDir;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SchedulerService schedulerService;

  @MockBean(name = "defaultParams")
  private Map< String, List< QueryParams > > defaultParams;

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
