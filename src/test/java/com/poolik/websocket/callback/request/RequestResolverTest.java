package com.poolik.websocket.callback.request;

import com.google.gson.Gson;
import com.poolik.websocket.callback.WebSocketResponse;
import com.poolik.websocket.callback.filter.WebSocketFilter;
import com.poolik.websocket.callback.response.Ok;
import com.poolik.websocket.callback.response.StringResponse;
import org.junit.Test;
import util.TestFilter;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RequestResolverTest {

  private Gson gson = new Gson();

  @Test
  public void ifFilterReturnsFalseReturnsFilterResponse() throws Exception {
    WebSocketResponse errorResponse = new StringResponse("test");
    Response response = new RequestResolver(new Request(RequestType.GET, "/test", "", "1"), Arrays.<WebSocketFilter>asList(new TestFilter("/test", false, errorResponse))).call();

    assertThat(gson.fromJson(response.data,StringResponse.class).response, is("test"));
  }

  @Test
  public void ifFilterReturnsTrueThenDelegatesToHandler() throws Exception {
    WebSocketResponse errorResponse = new StringResponse("test");
    Response response = new RequestResolver(new Request(RequestType.GET, "/test", "", "1"), Arrays.<WebSocketFilter>asList(new TestFilter("/test", true, errorResponse))).call();

    assertThat(gson.fromJson(response.data,Ok.class).status, is("OK"));
  }
}