package org.example.exception;

public class ParsingException extends AppException
{
  public ParsingException(String msg, Throwable throwable)
  {
    super(msg, throwable);
  }
}
