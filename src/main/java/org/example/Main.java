package org.example;

import org.example.cli.CliOptions;
import org.example.cli.CliParser;
import org.example.core.AppFactory;
import org.example.core.AppRunner;
import org.example.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
  public static void main(String[] args)
  {
    Logger log = LoggerFactory.getLogger(Main.class);
    try
    {
      CliOptions options = new CliParser().parse(args);
      AppFactory factory = new AppFactory();

      AppRunner runner = new AppRunner(factory);

      runner.run(options);
    }
    catch (AppException | IllegalArgumentException e)
    {
      log.error("Ошбика выполнения: {} ", e.getMessage());
    }
    catch (RuntimeException e)
    {
      log.error("Непридвиденная ошибка: {} ", e.getMessage());
    }
  }
}
