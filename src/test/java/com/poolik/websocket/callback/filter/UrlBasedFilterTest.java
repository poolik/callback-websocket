package com.poolik.websocket.callback.filter;

import com.poolik.websocket.callback.WebSocketRequest;
import org.junit.Test;
import util.TestFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlBasedFilterTest {

  @Test
  public void acceptsIfUrlMatchesGenerally() throws Exception {
    TestFilter filter = new TestFilter("/", true);
    assertTrue(filter.accepts(getRequestWithURL("/test")));
  }

  @Test
  public void acceptsIfUrlMatchesExactly() throws Exception {
    TestFilter filter = new TestFilter("/test", true);
    assertTrue(filter.accepts(getRequestWithURL("/test")));
  }

  @Test
  public void rejectsIfUrlDoesNotMatch() throws Exception {
    TestFilter filter = new TestFilter("/test/user", true);
    assertFalse(filter.accepts(getRequestWithURL("/test")));
  }

  @Test
  public void canMatchGenericUrls() throws Exception {
    TestFilter filter = new TestFilter("/test/*", true);
    assertTrue(filter.accepts(getRequestWithURL("/test/user")));
  }

  private WebSocketRequest getRequestWithURL(String s) {
    WebSocketRequest request = mock(WebSocketRequest.class);
    when(request.getUrl()).thenReturn(s);
    return request;
  }
}