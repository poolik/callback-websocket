package com.poolik.websocket.callback;

import com.google.gson.Gson;
import com.poolik.websocket.callback.request.Request;
import com.poolik.websocket.callback.request.RequestResolver;
import com.poolik.websocket.callback.request.Response;
import com.poolik.websocket.callback.request.ResponseAction;
import com.poolik.websocket.callback.util.F;
import com.poolik.websocket.callback.util.PromiseTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketRequestHandler {
  private static final Gson gson = new Gson();
  private static final String PING = "PING";
  private static final Logger log = LoggerFactory.getLogger(WebsocketRequestHandler.class);
  private static final ExecutorService executor = Executors.newCachedThreadPool();

  public static void handleRequest(Session session, String message) {
    if (PING.equals(message)) log.trace("Recieved ping, session ID: " + session.getId());
    else {
      Request request = gson.fromJson(message, Request.class);
      F.Promise<Response> promise = new F.Promise<>();
      promise.onRedeem(new ResponseAction(session, request));
      executor.submit(new PromiseTask<>(new RequestResolver(request), promise));
    }
  }
}