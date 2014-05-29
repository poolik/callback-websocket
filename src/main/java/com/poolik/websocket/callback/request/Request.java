package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.WebSocketRequest;

import javax.websocket.Session;
import java.util.Map;

public class Request implements WebSocketRequest {
  public final RequestType type;
  public final String url;
  public final String data;
  public final Map<String, String> headers;
  protected final String callbackId;
  public Session session;

  public Request(RequestType type, String url, String data, Map<String, String> headers, String callbackId) {
    this.type = type;
    this.url = url;
    this.data = data;
    this.headers = headers;
    this.callbackId = callbackId;
  }

  @Override
  public String toString() {
    return "WebSocketRequest{" +
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
  public String getRequestBody() {
    return data;
  }

  @Override
  public Map<String, String> getHeaders() {
    return headers;
  }

  @Override
  public Session getSession() {
    return session;
  }
}