package com.poolik.websocket.callback;

import com.google.gson.Gson;
import com.poolik.websocket.callback.filter.WebSocketFilter;
import com.poolik.websocket.callback.request.Request;
import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.request.Response;
import com.poolik.websocket.callback.response.Ok;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Contains;
import util.TestFilter;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class WebSocketRequestMarshallerTest {

  private Gson gson = new Gson();

  @Test
  public void doesNothingWhenReceivesPINGRequest() {
    Session session = mock(Session.class);
    new WebSocketRequestMarshaller().handleRequest(session, "PING");

    verify(session).getId();
    verifyNoMoreInteractions(session);
  }

  @Test
  public void sendsResponseViaAsyncRemote() throws Exception {
    Session session = mock(Session.class);
    RemoteEndpoint.Async asyncRemote = mock(RemoteEndpoint.Async.class);

    when(session.isOpen()).thenReturn(true);
    when(session.getAsyncRemote()).thenReturn(asyncRemote);
    new WebSocketRequestMarshaller().handleRequest(session, gson.toJson(new Request(RequestType.GET, "/test", "", new HashMap<String, String>(), "1"))).get(1, TimeUnit.SECONDS);
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
      new WebSocketRequestMarshaller().handleRequest(session, gson.toJson(new Request(RequestType.GET, "/error", "", new HashMap<String, String>(), "1"))).get(1, TimeUnit.SECONDS);
    } catch (ExecutionException e) {
      if (!e.getCause().getClass().equals(IllegalStateException.class)) throw e;
    }
    Thread.sleep(50);
    verify(asyncRemote).sendText(Mockito.argThat(new Contains("{\"error\":\"Request failed with: Error!\"")));
    verifyNoMoreInteractions(asyncRemote);
  }

  @Test
  public void passesFiltersOnToRequestResolver() throws Exception {
    TestFilter testFilter = new TestFilter("/test", false);
    new WebSocketRequestMarshaller(Arrays.<WebSocketFilter>asList(testFilter)).handleRequest(mock(Session.class), gson.toJson(new Request(RequestType.GET, "/test", "", new HashMap<String, String>(), "1")));
    Thread.sleep(50);
    assertTrue(testFilter.filterCalled);
  }

  @Test
  public void parsesHeadersCorrectly() throws Exception {
    TestFilter testFilter = new TestFilter("/test", false);
    new WebSocketRequestMarshaller(Arrays.<WebSocketFilter>asList(testFilter)).handleRequest(mock(Session.class), "{\"type\":\"POST\",\"url\":\"/test\",\"data\":\"\",\"headers\":{\"token\":\"234\"},\"callbackId\":1}");
    Thread.sleep(50);
    assertNotNull(testFilter.request);
    assertThat(testFilter.request.getHeaders().get("token"), is("234"));
  }
}
