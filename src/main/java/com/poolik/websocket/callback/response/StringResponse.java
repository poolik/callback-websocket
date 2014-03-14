package com.poolik.websocket.callback.response;

import com.poolik.websocket.callback.WebsocketResponse;

public class StringResponse implements WebsocketResponse {
  public final String response;

  public StringResponse(String response) {
    this.response = response;
  }
}
