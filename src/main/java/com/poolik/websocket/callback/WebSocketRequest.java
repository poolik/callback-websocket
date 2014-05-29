package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;

import javax.websocket.Session;
import java.util.Map;

public interface WebSocketRequest {
  String getUrl();
  RequestType getRequestType();
  String getRequestBody();
  Map<String, String> getHeaders();
  Session getSession();
}
