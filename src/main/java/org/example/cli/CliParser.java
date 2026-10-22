package org.example.cli;

import org.example.storage.WriteMode;

import java.util.List;

public class CliParser
{
  public CliOptions parse(String[] args)
  {
    if (args.length == 0)
    {
      throw new IllegalArgumentException("Передаваемые параметры не должны быть пустыми");
    }

    CliOptions options = new CliOptions();
    String firstArg = args[0];

    if (firstArg.equalsIgnoreCase("poll"))
    {
      options.setCommand(Command.POLL);
    }
    else if (firstArg.equalsIgnoreCase("show"))
    {
      options.setCommand(Command.SHOW);
    }
    else
    {
      throw new IllegalArgumentException("Вы ввели неизвестную команду: " + firstArg);
    }

    for (int i = 1; i < args.length; ++i)
    {
      String arg = args[i];
      String[] twoArg = arg.split("=");

      if (twoArg.length < 2)
      {
        throw new IllegalArgumentException("Аргумент должен быть в формате --ключ=значение: " + arg);
      }

      String first = twoArg[0];
      String second = twoArg[1];

      switch (first)
      {
        case "--apis" ->
        {
          String[] apis = second.split(",");
          options.setApis(List.of(apis));
        }
        case "--format" -> options.setFormat(second);
        case "--mode" ->
        {
          if (WriteMode.APPEND == WriteMode.valueOf(second.toUpperCase()))
          {
            options.setMode(WriteMode.APPEND);
          }
          else
          {
            options.setMode(WriteMode.CREATE);
          }
        }
        case "--file" -> options.setFilePath(second);
        case "--source" -> options.setSourceFilter(second);
      }
    }

    options.validate();

    return options;
  }
}
