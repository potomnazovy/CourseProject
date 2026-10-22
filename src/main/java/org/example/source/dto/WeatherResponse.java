package org.example.source.dto;

import java.util.List;

@SuppressWarnings("unused")
public class WeatherResponse
{
  private double latitude;
  private double longitude;
  private Current current;
  private Hourly hourly;

  public static class Current
  {
    private String time;
    private double temperature_2m;
    private double wind_speed_10m;

    public String getTime()
    {
      return this.time;
    }

    public double getTemperature_2m()
    {
      return this.temperature_2m;
    }

    public double getWind_speed_10m()
    {
      return this.wind_speed_10m;
    }

    public void setTime(String time)
    {
      this.time = time;
    }

    public void setTemperature_2m(double temperature_2m)
    {
      this.temperature_2m = temperature_2m;
    }

    public void setWind_speed_10m(double wind_speed_10m)
    {
      this.wind_speed_10m = wind_speed_10m;
    }
  }

  public static class Hourly
  {
    private List< String > time;
    private List< Double > temperature_2m;
    private List< Double > wind_speed_10m;

    public List< String > getTime()
    {
      return this.time;
    }

    public List< Double > getTemperature_2m()
    {
      return this.temperature_2m;
    }

    public List< Double > getWind_speed_10m()
    {
      return this.wind_speed_10m;
    }

    public void setTime(List<String> time)
    {
      this.time = time;
    }

    public void setTemperature_2m(List<Double> temperature_2m)
    {
      this.temperature_2m = temperature_2m;
    }

    public void setWind_speed_10m(List<Double> wind_speed_10m)
    {
      this.wind_speed_10m = wind_speed_10m;
    }
  }

  public double getLatitude()
  {
    return this.latitude;
  }

  public double getLongitude()
  {
    return this.longitude;
  }

  public Current getCurrent()
  {
    return this.current;
  }

  public Hourly getHourly()
  {
    return this.hourly;
  }

  public void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  public void setCurrent(Current current)
  {
    this.current = current;
  }

  public void setHourly(Hourly hourly)
  {
    this.hourly = hourly;
  }
}
