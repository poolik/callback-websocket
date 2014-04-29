package com.poolik.websocket.callback.request;

import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RequestTest {
  @Test
  public void correctStringRepresentation() throws Exception {
    assertThat(new Request(RequestType.GET, "/url", "", new HashMap<String, String>(), "1").toString(), is("WebSocketRequest{type=GET, url='/url', data=''}"));
  }

  @Test
  public void retrievesRequestType() throws Exception {
    assertThat(new Request(RequestType.GET, "/url", "", new HashMap<String, String>(), "1").getRequestType(), is(RequestType.GET));
  }

  @Test
  public void retrievesRequestBody() throws Exception {
    assertThat(new Request(RequestType.GET, "/url", "test", new HashMap<String, String>(), "1").getRequestBody(), is("test"));
  }
}