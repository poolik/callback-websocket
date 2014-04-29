package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;

import java.util.Map;

public interface WebSocketRequest {

  String getUrl();
  RequestType getRequestType();
  String getRequestBody();
  Map<String, String> getHeaders();
}
