package org.example.source;

import org.example.model.AggregatedRecord;
import org.example.model.QueryParams;
import java.util.List;

public interface ApiSource
{
  String getName();
  String buildUrl(QueryParams params);
  List< AggregatedRecord > parse(String json, QueryParams params);
}
