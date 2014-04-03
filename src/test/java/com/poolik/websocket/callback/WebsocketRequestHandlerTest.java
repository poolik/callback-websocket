package com.poolik.websocket.callback;

import com.google.gson.Gson;
import com.poolik.websocket.callback.request.ErrorResponse;
import com.poolik.websocket.callback.request.Request;
import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.request.Response;
import com.poolik.websocket.callback.response.Ok;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Contains;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.*;

public class WebsocketRequestHandlerTest {

  private Gson gson = new Gson();

  @Test
  public void doesNothingWhenReceivesPINGRequest() {
    Session session = mock(Session.class);
    WebsocketRequestHandler.handleRequest(session, "PING");

    verify(session).getId();
    verifyNoMoreInteractions(session);
  }

  @Test
  public void sendsResponseViaAsyncRemote() throws Exception {
    Session session = mock(Session.class);
    RemoteEndpoint.Async asyncRemote = mock(RemoteEndpoint.Async.class);

    when(session.isOpen()).thenReturn(true);
    when(session.getAsyncRemote()).thenReturn(asyncRemote);
    WebsocketRequestHandler.handleRequest(session, gson.toJson(new Request(RequestType.GET, "/test", "", "1"))).get(1, TimeUnit.SECONDS);
    Thread.sleep(50);
    verify(asyncRemote).sendText(gson.toJson(new Response("1", new Ok())));
    verifyNoMoreInteractions(asyncRemote);
  }

  @Test
  public void sendsErrorResponseWhenHandlerThrows() throws Exception {
    Session session = mock(Session.class);
    RemoteEndpoint.Async asyncRemote = mock(RemoteEndpoint.Async.class);

    when(session.isOpen()).thenReturn(true);
    when(session.getAsyncRemote()).thenReturn(asyncRemote);
    try {
      WebsocketRequestHandler.handleRequest(session, gson.toJson(new Request(RequestType.GET, "/error", "", "1"))).get(1, TimeUnit.SECONDS);
    } catch (ExecutionException e) {
      if (!e.getCause().getClass().equals(IllegalStateException.class)) throw e;
    }
    Thread.sleep(50);
    verify(asyncRemote).sendText(Mockito.argThat(new Contains("{\"error\":\"Request failed with: Error!\"")));
    verifyNoMoreInteractions(asyncRemote);
  }
}
