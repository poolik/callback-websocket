package com.poolik.websocket.callback.response;

import com.poolik.websocket.callback.WebSocketResponse;

public class StringResponse implements WebSocketResponse {
  public final String response;

  public StringResponse(String response) {
    this.response = response;
  }
}
