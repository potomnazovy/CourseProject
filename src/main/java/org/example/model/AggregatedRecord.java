package org.example.model;

import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class AggregatedRecord
{
  private String source;
  private String param;
  private String field;
  private double value;
  private LocalDateTime timestamp;

  public AggregatedRecord()
  {}

  public AggregatedRecord(String source, String param,
                          String field, double value, LocalDateTime timestamp)
  {
    if (source == null || source.isBlank())
    {
      throw new IllegalArgumentException("Источник сообщения не должен быть пустым");
    }

    if (param == null || param.isBlank())
    {
      throw new IllegalArgumentException("Параметр запроса не должен быть пустым");
    }

    if (field == null || field.isBlank())
    {
      throw new IllegalArgumentException("Показатель запроса не должен быть пустым");
    }

    this.source = source;
    this.param = param;
    this.field = field;
    this.value = value;
    this.timestamp = timestamp;
  }

  public String getSource()
  {
    return this.source;
  }

  public String getParam()
  {
    return this.param;
  }

  public String getField()
  {
    return this.field;
  }

  public double getValue()
  {
    return this.value;
  }

  public LocalDateTime getTimestamp()
  {
    return this.timestamp;
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public void setParam(String param)
  {
    this.param = param;
  }

  public void setField(String field)
  {
    this.field = field;
  }

  public void setValue(double value)
  {
    this.value = value;
  }

  public void setTimestamp(LocalDateTime timestamp)
  {
    this.timestamp = timestamp;
  }

  @Override
  public String toString()
  {
    return "AggregatedRecord{" +
      "source='" + source + '\'' +
      ", param='" + param + '\'' +
      ", field='" + field + '\'' +
      ", value=" + value +
      ", timestamp=" + timestamp +
      '}';
  }
}
