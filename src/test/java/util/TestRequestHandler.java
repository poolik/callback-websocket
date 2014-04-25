package util;

import com.poolik.websocket.callback.RequestHandler;
import com.poolik.websocket.callback.WebSocketResponse;
import com.poolik.websocket.callback.WebSocketRequest;
import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.response.Ok;
import com.poolik.websocket.callback.util.Pair;

import java.util.Arrays;
import java.util.List;

public class TestRequestHandler implements RequestHandler {
  @Override
  public Pair<String, List<RequestType>> getRequestMappings() {
    return Pair.of("/test", Arrays.asList(RequestType.GET));
  }

  @Override
  public WebSocketResponse handle(WebSocketRequest request) throws Exception {
    return new Ok();
  }
}
