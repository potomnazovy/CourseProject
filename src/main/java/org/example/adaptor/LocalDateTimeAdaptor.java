package org.example.adaptor;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.example.exception.ParsingException;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdaptor extends TypeAdapter< LocalDateTime >
{
  @Override
  public void write(JsonWriter writer, LocalDateTime value)
  {
    try
    {
      if (value == null)
      {
        writer.nullValue();
      }
      else
      {
        writer.value(value.toString());
      }
    }
    catch (IOException e)
    {
      throw new ParsingException("Ошибка записи времени в JSON файл: ", e);
    }
  }

  @Override
  public LocalDateTime read(JsonReader reader)
  {
    try
    {
      if (reader.peek() == JsonToken.NULL)
      {
        reader.nextNull();
        return null;
      }

      return LocalDateTime.parse(reader.nextString());
    }
    catch (IOException e)
    {
      throw new ParsingException("Ошибка чтения времени из JSON файла: ", e);
    }
  }
}
