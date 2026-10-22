package org.example.storage;

import org.example.model.AggregatedRecord;

import java.util.List;

public interface Storage
{
  void write(List< AggregatedRecord > record, WriteMode mode);
  List< AggregatedRecord > readAll();
  List< AggregatedRecord > readBySource(String source);
}
