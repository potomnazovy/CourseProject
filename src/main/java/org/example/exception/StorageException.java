package org.example.exception;

public class StorageException extends AppException
{
  public StorageException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }
}
