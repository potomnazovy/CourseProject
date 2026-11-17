package org.example.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliOptionsTest
{
  private CliOptions options;

  @BeforeEach
  void setUp()
  {
    this.options = new CliOptions();
  }

  @Test
  void testValidateSuccessForPoll()
  {
    this.options.setCommand(Command.POLL);
    this.options.setApis(List.of("weather"));
    this.options.setFormat("json");
    this.options.setFilePath("data.json");

    assertDoesNotThrow(this.options::validate);
  }

  @Test
  void testValidateSuccessForShow()
  {
    this.options.setCommand(Command.SHOW);
    this.options.setFilePath("data.json");

    assertDoesNotThrow(this.options::validate);
  }

  @Test
  void testValidateMissingCommand()
  {
    assertThrows(IllegalArgumentException.class, this.options::validate);
  }

  @Test
  void testValidateMissingFile()
  {
    this.options.setCommand(Command.POLL);
    this.options.setApis(List.of("weather"));
    this.options.setFormat("json");

    assertThrows(IllegalArgumentException.class, this.options::validate);
  }

  @Test
  void testValidateMissingApisForPoll()
  {
    this.options.setCommand(Command.POLL);
    this.options.setFilePath("data.json");
    this.options.setFormat("json");

    assertThrows(IllegalArgumentException.class, this.options::validate);
  }

  @Test
  void testValidateMissingFormatForPoll()
  {
    this.options.setCommand(Command.POLL);
    this.options.setApis(List.of("weather"));
    this.options.setFilePath("data.json");

    assertThrows(IllegalArgumentException.class, this.options::validate);
  }
}
