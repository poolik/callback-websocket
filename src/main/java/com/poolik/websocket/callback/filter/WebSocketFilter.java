package com.poolik.websocket.callback.filter;

import com.poolik.websocket.callback.WebSocketRequest;
import com.poolik.websocket.callback.WebSocketResponse;

public interface WebSocketFilter {
  boolean accepts(WebSocketRequest request);
  boolean filter(WebSocketRequest request);
  WebSocketResponse getErrorResponse();
}
