package org.example.source;

import com.google.gson.Gson;
import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import org.example.source.dto.CurrencyResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurrencySource implements ApiSource
{
  private static final String BASE_URL = "https://api.frankfurter.dev/v1/latest";

  @Override
  public String getName()
  {
    return "currency";
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

    CurrencyResponse currency = gson.fromJson(json, CurrencyResponse.class);

    List< AggregatedRecord > records = new ArrayList<>();

    String base = currency.getBase();

    LocalDate date = LocalDate.parse(currency.getDate());
    LocalDateTime time = date.atStartOfDay();

    for (Map.Entry< String, Double > entry : currency.getRates().entrySet())
    {
      records.add(new AggregatedRecord("currency", base, entry.getKey(), entry.getValue(), time));
    }

    return records;
  }
}
