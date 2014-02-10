package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.WebsocketResponse;

import java.util.concurrent.Callable;

public final class RequestResolver implements Callable<Response> {
  private final Request request;

  public RequestResolver(Request request) {
    this.request = request;
  }

  @Override
  public Response call() throws Exception {
    WebsocketResponse response = RequestMappings.getRequestAction(request.url, request.type).handle(request);
    return new Response(request.callbackId, response);
  }
}