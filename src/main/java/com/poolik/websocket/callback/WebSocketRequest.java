package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;

public interface WebSocketRequest {

  String getUrl();
  RequestType getRequestType();
  String getRequestBody();
}
