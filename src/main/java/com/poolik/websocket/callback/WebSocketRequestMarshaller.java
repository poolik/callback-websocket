package com.poolik.websocket.callback;

import com.google.gson.Gson;
import com.poolik.websocket.callback.filter.WebSocketFilter;
import com.poolik.websocket.callback.request.Request;
import com.poolik.websocket.callback.request.RequestResolver;
import com.poolik.websocket.callback.request.Response;
import com.poolik.websocket.callback.request.ResponseAction;
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
  private static final Gson gson = new Gson();
  private static final String PING = "PING";
  private static final Logger log = LoggerFactory.getLogger(WebSocketRequestMarshaller.class);
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  public WebSocketRequestMarshaller() {
    this.filters = new ArrayList<>();
  }

  public WebSocketRequestMarshaller(Collection<WebSocketFilter> filters) {
    this.filters = filters;
  }

  public F.Promise<Response> handleRequest(Session session, String message) {
    F.Promise<Response> promise = new F.Promise<>();
    if (PING.equals(message)) {
      log.trace("Received ping, session ID: " + session.getId());
      promise.invoke(null);
    } else {
      Request request = gson.fromJson(message, Request.class);
      promise.onRedeem(new ResponseAction(session, request));
      executor.submit(new PromiseTask<>(new RequestResolver(request, filters), promise));
    }
    return promise;
  }
}