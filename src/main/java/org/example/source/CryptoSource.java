package org.example.source;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.source.dto.CoinData;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CryptoSource implements ApiSource
{
  private static final String BASE_URL = "https://api.coingecko.com/api/v3/simple/price";

  @Override
  public String getName()
  {
    return "crypto";
  }

  @Override
  public String buildUrl(QueryParams params)
  {
    return BASE_URL + "?" + params.toQueryString();
  }

  @Override
  public List< AggregatedRecord > parse(String json, QueryParams params)
  {
    Gson gson = new Gson();

    Type type = new TypeToken< Map< String, CoinData > >(){}.getType();

    Map< String, CoinData > coinMap = gson.fromJson(json, type);

    List< AggregatedRecord > records = new ArrayList<>();

    for (Map.Entry< String, CoinData > entry : coinMap.entrySet())
    {
      records.add(new AggregatedRecord("crypto", entry.getKey(),
        "usd", entry.getValue().getUsd(), LocalDateTime.now().withNano(0)));

      records.add(new AggregatedRecord("crypto", entry.getKey(),
        "eur", entry.getValue().getEur(), LocalDateTime.now().withNano(0)));

      records.add(new AggregatedRecord("crypto", entry.getKey(),
        "market_cap", entry.getValue().getUsd_market_cap(), LocalDateTime.now().withNano(0)));

      records.add(new AggregatedRecord("crypto", entry.getKey(),
        "24h_vol", entry.getValue().getUsd_24h_vol(), LocalDateTime.now().withNano(0)));
    }

    return records;
  }
}
