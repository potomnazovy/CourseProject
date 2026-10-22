package org.example.source.dto;

import java.util.Map;

@SuppressWarnings("unused")
public class CurrencyResponse
{
  private String base;
  private String date;
  private Map< String, Double > rates;

  public String getBase()
  {
    return this.base;
  }

  public String getDate()
  {
    return this.date;
  }

  public Map< String, Double > getRates()
  {
    return this.rates;
  }

  public void setBase(String base)
  {
    this.base = base;
  }

  public void setDate(String date)
  {
    this.date = date;
  }

  public void setRates(Map<String, Double> rates)
  {
    this.rates = rates;
  }
}
