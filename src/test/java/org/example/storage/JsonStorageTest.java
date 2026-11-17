package org.example.storage;

import org.example.model.AggregatedRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonStorageTest
{
  private static final WriteMode WRITE_MODE_CREATE = WriteMode.CREATE;
  private static final WriteMode WRITE_MODE_APPEND = WriteMode.APPEND;

  private JsonStorage storage;

  @TempDir
  Path tempDir;

  @BeforeEach
  public void setUp()
  {
    String filePath = tempDir.resolve("test_data.json").toString();
    this.storage = new JsonStorage(filePath);
  }

  @Test
  public void testIncorrectCreate()
  {
    assertThrows(IllegalArgumentException.class, () -> new JsonStorage(null));
    assertThrows(IllegalArgumentException.class, () -> new JsonStorage(""));
  }

  @Test
  public void testWriteWithBadArg()
  {
    List< AggregatedRecord > record = null;

    assertThrows(IllegalArgumentException.class, () -> this.storage.write(record, WRITE_MODE_CREATE));
  }

  @Test
  public void testWriteAndReadAll()
  {
    AggregatedRecord record1 = helper();

    this.storage.write(List.of(record1), WRITE_MODE_CREATE);

    List< AggregatedRecord > res1 = this.storage.readAll();

    assertEquals(1, res1.size());
    assertEquals("weather", res1.getFirst().getSource());

    AggregatedRecord record2 = helper();

    this.storage.write(List.of(record2), WRITE_MODE_APPEND);

    List< AggregatedRecord > res2 = this.storage.readAll();

    assertEquals(2, res2.size());
    assertEquals("weather", res2.getFirst().getSource());
  }

  @Test
  public void testCreateOverwrites()
  {
    AggregatedRecord record1 = helper();
    AggregatedRecord record2 = helper();

    this.storage.write(List.of(record1, record2), WRITE_MODE_CREATE);

    assertEquals(2, this.storage.readAll().size());

    this.storage.write(List.of(record1), WRITE_MODE_CREATE);

    assertEquals(1, this.storage.readAll().size());
  }

  @Test
  public void testReadBySourceWithBadArgs()
  {
    assertThrows(IllegalArgumentException.class, () -> this.storage.readBySource(null));
    assertThrows(IllegalArgumentException.class, () -> this.storage.readBySource(""));
  }

  @Test
  public void testReadBySource()
  {
    AggregatedRecord record1 = helper();

    this.storage.write(List.of(record1), WRITE_MODE_CREATE);

    List< AggregatedRecord > weatherList = this.storage.readBySource("weather");

    assertEquals(1, weatherList.size());
    assertEquals("weather", weatherList.getFirst().getSource());

    assertTrue(this.storage.readBySource("unknown").isEmpty());
  }

  @Test
  public void testFromNonExistentFile()
  {
    assertTrue(storage.readAll().isEmpty());
    assertTrue(storage.readBySource("weather").isEmpty());
  }

  private AggregatedRecord helper()
  {
    return new AggregatedRecord(
      "weather",
      "Berlin",
      "temp",
      20.0,
      LocalDateTime.now().plusNanos(System.nanoTime()));
  }
}
