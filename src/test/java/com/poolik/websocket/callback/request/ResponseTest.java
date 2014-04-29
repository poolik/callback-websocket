package com.poolik.websocket.callback.request;

import com.poolik.websocket.callback.response.Ok;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResponseTest {

  @Test
  public void correctStringRepresentation() throws Exception {
    assertThat(new Response("1", new Ok()).toString(), is("Response{callbackId='1', data='{\"status\":\"OK\"}'}"));
  }
}