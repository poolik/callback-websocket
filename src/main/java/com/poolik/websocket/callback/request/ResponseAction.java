package com.poolik.websocket.callback.request;

import com.google.gson.Gson;
import com.poolik.websocket.callback.util.F;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ResponseAction implements F.Action<F.Promise<Response>> {

  private static final Logger log = LoggerFactory.getLogger(ResponseAction.class);
  private static final Gson gson = new Gson();
  private final Request request;

  public ResponseAction(Request request) {
    this.request = request;
  }

  @Override
  public void invoke(F.Promise<Response> result) {
    if (request.session.isOpen()) {
      log.trace("Sending response to " + request.callbackId);
      Response requestResponse = result.getOrNull();
      try {
        if (requestResponse != null) request.session.getAsyncRemote().sendText(gson.toJson(requestResponse));
        else {
          log.error("Request " + request.type + " '" + request.url + "' failed with: ", result.getException());
          request.session.getAsyncRemote().sendText(gson.toJson(new ErrorResponse(request.callbackId, result.getException())));
        }
      } catch (Throwable e) {
        log.error("Failed to send response: " + (requestResponse != null ? requestResponse.toString() : result.getException()), e);
      }
    }
  }
}