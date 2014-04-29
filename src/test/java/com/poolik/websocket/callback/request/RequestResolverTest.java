package com.poolik.websocket.callback.request;

import com.google.gson.Gson;
import com.poolik.websocket.callback.filter.WebSocketFilter;
import com.poolik.websocket.callback.response.Ok;
import org.junit.Test;
import util.TestFilter;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RequestResolverTest {

  private Gson gson = new Gson();

  @Test
  public void ifFilterReturnsFalseReturnsFilterResponse() throws Exception {
    Exception errorResponse = new Exception("test");
    Response response = new RequestResolver(new Request(RequestType.GET, "/test", "", new HashMap<String, String>(), "1"), Arrays.<WebSocketFilter>asList(new TestFilter("/test", false, errorResponse))).call();

    assertThat(response, instanceOf(ErrorResponse.class));
    assertThat(((ErrorResponse) response).error, is("Request failed with: test"));
  }

  @Test
  public void ifFilterReturnsTrueThenDelegatesToHandler() throws Exception {
    Exception errorResponse = new Exception("test");
    Response response = new RequestResolver(new Request(RequestType.GET, "/test", "", new HashMap<String, String>(), "1"), Arrays.<WebSocketFilter>asList(new TestFilter("/test", true, errorResponse))).call();

    assertThat(gson.fromJson(response.data,Ok.class).status, is("OK"));
  }
}