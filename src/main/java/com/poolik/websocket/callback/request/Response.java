package com.poolik.websocket.callback.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.poolik.websocket.callback.WebSocketResponse;

public class Response {

  private static final Gson gson = new GsonBuilder().create();

  public final String data;
  protected final String callbackId;

  public Response(String callbackId, WebSocketResponse data) {
    this.callbackId = callbackId;
    this.data = gson.toJson(data);
  }

  @Override
  public String toString() {
    return "Response{" +
        "callbackId='" + callbackId + '\'' +
        ", data='" + data + '\'' +
        '}';
  }
}