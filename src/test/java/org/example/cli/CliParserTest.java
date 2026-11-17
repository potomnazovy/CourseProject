package org.example.cli;

import org.example.storage.WriteMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CliParserTest
{
  private CliParser parser;

  @BeforeEach
  void setUp()
  {
    this.parser = new CliParser();
  }

  @Test
  void testParsePollCommand()
  {
    String[] args = {"poll", "--apis=weather,currency", "--format=csv", "--mode=append", "--file=data.csv"};
    CliOptions options = this.parser.parse(args);

    assertEquals(Command.POLL, options.getCommand());
    assertEquals(2, options.getApis().size());
    assertEquals("weather", options.getApis().getFirst());
    assertEquals("csv", options.getFormat());
    assertEquals(WriteMode.APPEND, options.getMode());
    assertEquals("data.csv", options.getFilePath());
  }

  @Test
  void testParseShowCommand()
  {
    String[] args = {"show", "--file=data.json", "--source=weather"};
    CliOptions options = this.parser.parse(args);

    assertEquals(Command.SHOW, options.getCommand());
    assertEquals("data.json", options.getFilePath());
    assertEquals("weather", options.getSourceFilter());
  }

  @Test
  void testParseWithDefaultValues()
  {
    String[] args = {"poll", "--apis=weather", "--file=data.json", "--format=json"};
    CliOptions options = this.parser.parse(args);

    assertEquals(Command.POLL, options.getCommand());
    assertEquals("data.json", options.getFilePath());
    assertEquals(1, options.getApis().size());
    assertEquals("weather", options.getApis().getFirst());

    assertEquals("json", options.getFormat());
    assertEquals(WriteMode.CREATE, options.getMode());
  }

  @Test
  void testParseInvalidCommand()
  {
    String[] args = {"invalid", "--file=data.json"};

    assertThrows(IllegalArgumentException.class, () -> this.parser.parse(args));
  }

  @Test
  void testParseEmptyArgs()
  {
    String[] args = {};

    assertThrows(IllegalArgumentException.class, () -> this.parser.parse(args));
  }

  @Test
  void testParseInvalidArgFormat()
  {
    String[] args = {"poll", "invalid_arg"};

    assertThrows(IllegalArgumentException.class, () -> this.parser.parse(args));
  }
}
