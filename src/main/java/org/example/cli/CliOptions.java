package org.example.cli;

import org.example.storage.WriteMode;

import java.util.ArrayList;
import java.util.List;

public class CliOptions
{
  private Command command;
  private List< String > apis = new ArrayList<>();
  private String format;
  private WriteMode mode = WriteMode.CREATE;
  private String filePath;
  private String sourceFilter;

  public CliOptions()
  {
  }

  public Command getCommand()
  {
    return this.command;
  }

  public List< String > getApis()
  {
    return this.apis;
  }

  public String getFormat()
  {
    return this.format;
  }

  public WriteMode getMode()
  {
    return this.mode;
  }

  public String getFilePath()
  {
    return this.filePath;
  }

  public String getSourceFilter()
  {
    return this.sourceFilter;
  }

  public void setCommand(Command command)
  {
    this.command = command;
  }

  public void setApis(List< String > apis)
  {
    this.apis = apis;
  }

  public void setFormat(String format)
  {
    this.format = format;
  }

  public void setMode(WriteMode mode)
  {
    this.mode = mode;
  }

  public void setFilePath(String filePath)
  {
    this.filePath = filePath;
  }

  public void setSourceFilter(String sourceFilter)
  {
    this.sourceFilter = sourceFilter;
  }

  public void validate()
  {
    if (this.command == null)
    {
      throw new IllegalArgumentException("Команда должна быть указана (poll или show)");
    }

    if (this.command == Command.POLL)
    {
      if (this.apis == null || this.apis.isEmpty())
      {
        throw new IllegalArgumentException("Для команды poll необходимо указать API");
      }

      if (this.format == null || this.format.isBlank())
      {
        throw new IllegalArgumentException("Формат файла должен быть указан (json или csv)");
      }

      if (this.sourceFilter != null)
      {
        throw new IllegalArgumentException("Флаг --source нельзя использовать с командой poll");
      }
    }
    else if (this.command == Command.SHOW)
    {
      if (this.apis != null && !this.apis.isEmpty())
      {
        throw new IllegalArgumentException("Флаг --apis нельзя использовать с командой show." +
          "Используйте --source=...");
      }
    }

    if (this.filePath == null || this.filePath.isBlank())
    {
      throw new IllegalArgumentException("Путь к файлу должен быть указан (--file=...)");
    }
  }
}
