package util;

import com.poolik.websocket.callback.RequestHandler;
import com.poolik.websocket.callback.WebsocketRequest;
import com.poolik.websocket.callback.WebsocketResponse;
import com.poolik.websocket.callback.request.RequestType;
import com.poolik.websocket.callback.util.Pair;

import java.util.Arrays;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class TestErrorRequesthandler implements RequestHandler {
  @Override
  public Pair<String, List<RequestType>> getRequestMappings() {
    return Pair.of("/error", Arrays.asList(RequestType.GET));
  }

  @Override
  public WebsocketResponse handle(WebsocketRequest request) throws Exception {
    throw new IllegalStateException("Error!");
  }
}
