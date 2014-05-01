package com.poolik.websocket.callback.request;

import com.poolik.classfinder.info.ClassInfo;
import com.poolik.websocket.callback.RequestHandlerResolver;
import com.poolik.websocket.callback.WebSocketRequestHandler;
import com.poolik.websocket.callback.util.ClassUtils;
import com.poolik.websocket.callback.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DynamicRequestHandlerResolver implements RequestHandlerResolver {
  private static final Logger log = LoggerFactory.getLogger(DynamicRequestHandlerResolver.class);
  private final Map<Pair<String, RequestType>, WebSocketRequestHandler> mappings = new HashMap<>();

  @Override
  public WebSocketRequestHandler getHandlerFor(String url, RequestType type) {
    WebSocketRequestHandler handler = mappings.get(Pair.of(url, type));
    if (handler == null) throw new IllegalArgumentException("Invalid request URL: " + url + "!");
    return handler;
  }

  @Override
  public void findHandlers() {
    for (WebSocketRequestHandler webSocketRequestHandler : getRequestHandlers()) {
      Pair<String, List<RequestType>> handlerRequestMappings = webSocketRequestHandler.getRequestMappings();
      for (RequestType requestType : handlerRequestMappings.second) {
        mappings.put(Pair.of(handlerRequestMappings.first, requestType), webSocketRequestHandler);
      }
    }
  }

  private List<WebSocketRequestHandler> getRequestHandlers() {
    List<WebSocketRequestHandler> webSocketRequestHandlers = new ArrayList<>();
    for (ClassInfo classInfo : getRequestHandlerImplementations()) {
      try {
        webSocketRequestHandlers.add((WebSocketRequestHandler) Class.forName(classInfo.getClassName()).newInstance());
      } catch (Exception e) {
        log.error("Failed to initiate requestHandler! ", e);
      }
    }
    return webSocketRequestHandlers;
  }

  private Collection<ClassInfo> getRequestHandlerImplementations() {
    Collection<ClassInfo> foundClasses = new ArrayList<>();
    try {
      foundClasses = ClassUtils.getImplementingInterface(WebSocketRequestHandler.class);
    } catch (Exception e) {
      log.error("Failed to retrieve all implementations of " + WebSocketRequestHandler.class.getSimpleName(), e);
    }
    return foundClasses;
  }
}