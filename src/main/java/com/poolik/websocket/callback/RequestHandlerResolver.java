package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;

public interface RequestHandlerResolver {
  void findHandlers();
  WebSocketRequestHandler getHandlerFor(String url, RequestType type);
}
