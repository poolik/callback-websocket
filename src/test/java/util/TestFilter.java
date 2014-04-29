package util;

import com.poolik.websocket.callback.WebSocketRequest;
import com.poolik.websocket.callback.WebSocketResponse;
import com.poolik.websocket.callback.filter.UrlBasedFilter;

public class TestFilter extends UrlBasedFilter {
  public boolean filterCalled = false;
  public WebSocketRequest request = null;
  private final boolean filterResult;
  private final WebSocketResponse errorResponse;
  public TestFilter(String urlPattern, Boolean filterResult) {
    super(urlPattern);
    this.filterResult = filterResult;
    this.errorResponse = null;
  }

  public TestFilter(String urlPattern, Boolean filterResult, WebSocketResponse errorResponse) {
    super(urlPattern);
    this.filterResult = filterResult;
    this.errorResponse = errorResponse;
  }

  @Override
  public boolean filter(WebSocketRequest request) {
    this.request = request;
    this.filterCalled = true;
    return filterResult;
  }

  @Override
  public WebSocketResponse getErrorResponse() {
    return errorResponse;
  }
}