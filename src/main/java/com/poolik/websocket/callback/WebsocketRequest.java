package com.poolik.websocket.callback;

import com.poolik.websocket.callback.request.RequestType;

public interface WebsocketRequest {

  String getUrl();
  RequestType getRequestType();
  String getRequestData();
}
