package util;

import com.poolik.websocket.callback.RequestHandler;
import com.poolik.websocket.callback.WebSocketRequest;
import com.poolik.websocket.callback.WebSocketResponse;
import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.util.Pair;

import java.util.Arrays;
import java.util.List;

public class TestErrorRequesthandler implements RequestHandler {
  @Override
  public Pair<String, List<RequestType>> getRequestMappings() {
    return Pair.of("/error", Arrays.asList(RequestType.GET));
  }

  @Override
  public WebSocketResponse handle(WebSocketRequest request) throws Exception {
    throw new IllegalStateException("Error!");
  }
}
