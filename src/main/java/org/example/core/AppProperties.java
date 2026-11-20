package org.example.core;

import org.example.model.QueryParams;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("unused")
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties
{
  private int defaultN;
  private int defaultT;
  private String defaultFilePath;
  private String defaultFormat;
  private String defaultMode;
  private List< String > defaultApis;
  private Map< String, List< Map< String, String > > > sources;

  public int getDefaultN()
  {
    return this.defaultN;
  }

  public int getDefaultT()
  {
    return this.defaultT;
  }

  public String getDefaultFilePath()
  {
    return this.defaultFilePath;
  }

  public String getDefaultFormat()
  {
    return this.defaultFormat;
  }

  public String getDefaultMode()
  {
    return this.defaultMode;
  }

  public List< String > getDefaultApis()
  {
    return this.defaultApis;
  }

  public Map< String, List< Map< String, String > > > getSources()
  {
    return sources;
  }

  public void setDefaultN(int defaultN)
  {
    this.defaultN = defaultN;
  }

  public void setDefaultT(int defaultT)
  {
    this.defaultT = defaultT;
  }

  public void setDefaultFilePath(String defaultFilePath)
  {
    this.defaultFilePath = defaultFilePath;
  }

  public void setDefaultFormat(String defaultFormat)
  {
    this.defaultFormat = defaultFormat;
  }

  public void setDefaultMode(String defaultMode)
  {
    this.defaultMode = defaultMode;
  }

  public void setDefaultApis(List< String > defaultApis)
  {
    this.defaultApis = defaultApis;
  }

  public void setSources(Map< String, List< Map< String, String > > > sources)
  {
    this.sources = sources;
  }

  public Map< String, List< QueryParams > > convertSourcesToQueryParams()
  {
    Map< String, List< QueryParams > > result = new TreeMap<>();
    if (this.sources == null)
    {
      return result;
    }

    for (Map.Entry< String, List< Map< String, String > > > entry : this.sources.entrySet())
    {
      List< QueryParams > list = new ArrayList<>();
      for (Map< String, String > map : entry.getValue())
      {
        QueryParams qp = new QueryParams();
        map.forEach(qp::add);
        list.add(qp);
      }

      result.put(entry.getKey(), list);
    }

    return result;
  }
}
