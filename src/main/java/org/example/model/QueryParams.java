package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class QueryParams
{
  private final Map< String, String > params;

  public QueryParams()
  {
    this.params = new HashMap<>();
  }

  public Map< String, String > getParams()
  {
    return this.params;
  }

  public void add(String key, String value)
  {
    if (key == null || value == null)
    {
      throw new IllegalArgumentException("Добавляемый параметр не должен быть пустым");
    }

    this.params.put(key, value);
  }

  public String toQueryString()
  {
    if (params.isEmpty())
    {
      return "";
    }

    boolean flag = true;

    StringBuilder sb = new StringBuilder();
    for (Map.Entry< String, String > map : this.params.entrySet())
    {
      if (!flag)
      {
        sb.append("&");
      }

      sb.append(map.getKey()).append("=").append(map.getValue());
      flag = false;
    }

    return sb.toString();
  }
}
