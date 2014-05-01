package com.poolik.websocket.callback;

import com.google.gson.Gson;
import com.poolik.websocket.callback.filter.WebSocketFilter;
import com.poolik.websocket.callback.request.*;
import com.poolik.websocket.callback.util.F;
import com.poolik.websocket.callback.util.PromiseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketRequestMarshaller {

  private final Collection<WebSocketFilter> filters;
  private final RequestHandlerResolver handlerResolver;
  private static final Gson gson = new Gson();
  private static final String PING = "PING";
  private static final Logger log = LoggerFactory.getLogger(WebSocketRequestMarshaller.class);
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  public WebSocketRequestMarshaller() {
    this(new ArrayList<WebSocketFilter>(), new DynamicRequestHandlerResolver());
  }

  public WebSocketRequestMarshaller(Collection<WebSocketFilter> filters) {
    this(filters, new DynamicRequestHandlerResolver());
  }

  public WebSocketRequestMarshaller(Collection<WebSocketFilter> filters, RequestHandlerResolver handlerResolver) {
    this.filters = filters;
    this.handlerResolver = handlerResolver;
    this.handlerResolver.findHandlers();
  }

  public F.Promise<Response> handleRequest(Session session, String message) {
    F.Promise<Response> promise = new F.Promise<>();
    if (PING.equals(message)) {
      log.trace("Received ping, session ID: " + session.getId());
      promise.invoke(null);
    } else {
      Request request = gson.fromJson(message, Request.class);
      promise.onRedeem(new ResponseAction(session, request));
      executor.submit(new PromiseTask<>(
          new RequestAction(request, filters, handlerResolver.getHandlerFor(request.url, request.type)),
          promise));
    }
    return promise;
  }
}