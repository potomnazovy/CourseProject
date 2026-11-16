package org.example.storage;

public class StorageFactory
{
  private StorageFactory() {}

  public static Storage createStorage(String format, String filePath)
  {
    if ("csv".equalsIgnoreCase(format))
    {
      return new CsvStorage(filePath);
    }
    else
    {
      return new JsonStorage(filePath);
    }
  }
}
