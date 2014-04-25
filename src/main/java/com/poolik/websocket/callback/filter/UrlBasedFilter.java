package com.poolik.websocket.callback.filter;

import com.poolik.websocket.callback.WebSocketRequest;

import java.util.regex.Pattern;

public abstract class UrlBasedFilter implements WebSocketFilter {

  private final Pattern urlPattern;

  public UrlBasedFilter(String urlPattern) {
    this.urlPattern =  Pattern.compile(urlPattern);
  }

  @Override
  public boolean accepts(WebSocketRequest request) {
    return urlPattern.matcher(request.getUrl()).find();
  }
}