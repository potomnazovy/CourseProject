package org.example.http;

import org.example.exception.AppException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaHttpClient implements HttpClient
{
  private final java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();

  @Override
  public String get(String url) throws IOException, InterruptedException
  {
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

    HttpResponse< String > response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() < 200 || response.statusCode() >= 300)
    {
      throw new AppException("HTTP error: " + response.statusCode());
    }

    return response.body();
  }
}
