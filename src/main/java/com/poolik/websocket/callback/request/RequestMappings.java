package com.poolik.websocket.callback.request;

import com.poolik.classfinder.info.ClassInfo;
import com.poolik.websocket.callback.WebSocketRequestHandler;
import com.poolik.websocket.callback.util.ClassUtils;
import com.poolik.websocket.callback.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class RequestMappings {
  private static final Logger log = LoggerFactory.getLogger(RequestMappings.class);
  private static final Map<Pair<String, RequestType>, WebSocketRequestHandler> mappings = getRequestMappings();

  private static Map<Pair<String, RequestType>, WebSocketRequestHandler> getRequestMappings() {
    List<WebSocketRequestHandler> webSocketRequestHandlers = getRequestHandlers();
    Map<Pair<String, RequestType>, WebSocketRequestHandler> requestMappings = new HashMap<>();
    for (WebSocketRequestHandler webSocketRequestHandler : webSocketRequestHandlers) {
      Pair<String, List<RequestType>> handlerRequestMappings = webSocketRequestHandler.getRequestMappings();
      for (RequestType requestType : handlerRequestMappings.second) {
        requestMappings.put(Pair.of(handlerRequestMappings.first, requestType), webSocketRequestHandler);
      }
    }
    return requestMappings;
  }

  private static List<WebSocketRequestHandler> getRequestHandlers() {
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

  private static Collection<ClassInfo> getRequestHandlerImplementations() {
    Collection<ClassInfo> foundClasses = new ArrayList<>();
    try {
      foundClasses = ClassUtils.getImplementingInterface(WebSocketRequestHandler.class);
    } catch (Exception e) {
      log.error("Failed to retrieve all implementations of " + WebSocketRequestHandler.class.getSimpleName(), e);
    }
    return foundClasses;
  }

  protected static WebSocketRequestHandler getRequestAction(String url, RequestType type) {
    WebSocketRequestHandler action = mappings.get(Pair.of(url, type));
    if (action == null) throw new IllegalArgumentException("Invalid request URL: " + url + "!");
    return action;
  }
}