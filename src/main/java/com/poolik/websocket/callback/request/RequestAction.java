package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.RequestHandlerResolver;
import com.poolik.websocket.callback.WebSocketRequestHandler;
import com.poolik.websocket.callback.filter.WebSocketFilter;

import java.util.Collection;
import java.util.concurrent.Callable;

public final class RequestAction implements Callable<Response> {
  private final Request request;
  private final Collection<WebSocketFilter> filters;
  private final RequestHandlerResolver requestHandlerResolver;

  public RequestAction(Request request, Collection<WebSocketFilter> filters, RequestHandlerResolver requestHandlerResolver) {
    this.request = request;
    this.filters = filters;
    this.requestHandlerResolver = requestHandlerResolver;
  }

  @Override
  public Response call() throws Exception {
    WebSocketRequestHandler handler = requestHandlerResolver.getHandlerFor(request.url, request.type);
    Exception exception = applyFilters();
    if (exception != null) return new ErrorResponse(request.callbackId, exception);
    return new Response(request.callbackId, handler.handle(request));
  }

  private Exception applyFilters() {
    for (WebSocketFilter filter : filters) {
      if (filter.accepts(request))
        if (!filter.filter(request)) return filter.getError(request.url);
    }
    return null;
  }
}