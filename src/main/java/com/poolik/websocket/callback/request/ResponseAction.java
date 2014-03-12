package com.poolik.websocket.callback.request;

import com.google.gson.Gson;
import com.poolik.websocket.callback.util.F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

public final class ResponseAction implements F.Action<F.Promise<Response>> {

  private static final Logger log = LoggerFactory.getLogger(ResponseAction.class);
  private static final Gson gson = new Gson();
  private final Session session;
  private final Request request;

  public ResponseAction(Session session, Request request) {
    this.session = session;
    this.request = request;
  }

  @Override
  public void invoke(F.Promise<Response> result) {
    if (session.isOpen()) {
      log.trace("Sending response to " + request.callbackId);
      Response requestResponse = result.getOrNull();
      try {
        if (requestResponse != null) session.getAsyncRemote().sendText(gson.toJson(requestResponse));
        else {
          log.error("Request " + request.type + request.url + " failed with: ", result.getException());
          session.getAsyncRemote().sendText(gson.toJson(new ErrorResponse(request.callbackId, result.getException())));
        }
      } catch (Throwable e) {
        log.error("Failed to send response: " + (requestResponse != null ? requestResponse.toString() : result.getException()), e);
      }
    }
  }
}