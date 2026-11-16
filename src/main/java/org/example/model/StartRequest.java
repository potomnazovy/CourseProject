package org.example.model;

import java.util.List;
import java.util.Map;

public class StartRequest
{
  private Integer n;
  private Integer t;
  private String format;
  private String file;
  private String mode;
  private Map< String, List< Map< String, String > > > sources;

  public Integer getN()
  {
    return n;
  }

  public Integer getT()
  {
    return t;
  }

  public String getFile()
  {
    return file;
  }

  public String getFormat()
  {
    return format;
  }

  public String getMode()
  {
    return mode;
  }

  public Map< String, List< Map< String, String > > > getSources()
  {
    return sources;
  }

  public void setT(Integer t)
  {
    this.t = t;
  }

  public void setFile(String file)
  {
    this.file = file;
  }

  public void setFormat(String format)
  {
    this.format = format;
  }

  public void setMode(String mode)
  {
    this.mode = mode;
  }

  public void setSources(Map<String, List<Map<String, String>>> sources)
  {
    this.sources = sources;
  }

  public void setN(Integer n)
  {
    this.n = n;
  }
}
