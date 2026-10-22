package org.example.source.dto;

@SuppressWarnings("unused")
public class CoinData
{
  private double usd;
  private double eur;
  private double usd_market_cap;
  private double usd_24h_vol;

  public double getUsd()
  {
    return this.usd;
  }

  public double getEur()
  {
    return this.eur;
  }

  public double getUsd_market_cap()
  {
    return this.usd_market_cap;
  }

  public double getUsd_24h_vol()
  {
    return this.usd_24h_vol;
  }

  public void setUsd(double usd)
  {
    this.usd = usd;
  }

  public void setEur(double eur)
  {
    this.eur = eur;
  }

  public void setUsd_market_cap(double usd_market_cap)
  {
    this.usd_market_cap = usd_market_cap;
  }

  public void setUsd_24h_vol(double usd_24h_vol)
  {
    this.usd_24h_vol = usd_24h_vol;
  }
}
