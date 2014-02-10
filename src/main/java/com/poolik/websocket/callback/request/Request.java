package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.WebsocketRequest;

public class Request implements WebsocketRequest {
  public final RequestType type;
  public final String url;
  public final String data;
  protected final String callbackId;

  public Request(RequestType type, String url, String data, String callbackId) {
    this.type = type;
    this.url = url;
    this.data = data;
    this.callbackId = callbackId;
  }

  @Override
  public String toString() {
    return "WebsocketRequest{" +
        "type=" + type +
        ", url='" + url + '\'' +
        ", data='" + data + '\'' +
        '}';
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public RequestType getRequestType() {
    return type;
  }

  @Override
  public String getRequestData() {
    return data;
  }
}
