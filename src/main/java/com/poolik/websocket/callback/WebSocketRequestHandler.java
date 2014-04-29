package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.util.Pair;

import java.util.List;

public interface WebSocketRequestHandler {
  public Pair<String, List<RequestType>> getRequestMappings();
  public WebSocketResponse handle(WebSocketRequest request) throws Exception;
}