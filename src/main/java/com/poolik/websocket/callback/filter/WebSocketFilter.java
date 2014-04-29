package com.poolik.websocket.callback.filter;

import com.poolik.websocket.callback.WebSocketRequest;

public interface WebSocketFilter {
  boolean accepts(WebSocketRequest request);
  boolean filter(WebSocketRequest request);
  Exception getError();
}
