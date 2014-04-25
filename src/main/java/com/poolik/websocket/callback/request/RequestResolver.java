package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.WebSocketResponse;
import com.poolik.websocket.callback.filter.WebSocketFilter;

import java.util.Collection;
import java.util.concurrent.Callable;

public final class RequestResolver implements Callable<Response> {
  private final Request request;
  private final Collection<WebSocketFilter> filters;

  public RequestResolver(Request request, Collection<WebSocketFilter> filters) {
    this.request = request;
    this.filters = filters;
  }

  @Override
  public Response call() throws Exception {
    WebSocketResponse response = applyFilters();
    if (response != null) return new Response(request.callbackId, response);
    response = RequestMappings.getRequestAction(request.url, request.type).handle(request);
    return new Response(request.callbackId, response);
  }

  private WebSocketResponse applyFilters() {
    for (WebSocketFilter filter : filters) {
      if (filter.accepts(request))
        if (!filter.filter(request)) return filter.getErrorResponse();
    }
    return null;
  }
}