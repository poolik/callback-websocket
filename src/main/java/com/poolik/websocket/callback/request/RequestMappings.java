package com.poolik.websocket.callback.request;

import com.poolik.classfinder.info.ClassInfo;
import com.poolik.websocket.callback.RequestHandler;
import com.poolik.websocket.callback.util.ClassUtils;
import com.poolik.websocket.callback.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class RequestMappings {
  private static final Logger log = LoggerFactory.getLogger(RequestMappings.class);
  private static final Map<Pair<String, RequestType>, RequestHandler> mappings = getRequestMappings();

  private static Map<Pair<String, RequestType>, RequestHandler> getRequestMappings() {
    List<RequestHandler> requestHandlers = getRequestHandlers();
    Map<Pair<String, RequestType>, RequestHandler> requestMappings = new HashMap<>();
    for (RequestHandler requestHandler : requestHandlers) {
      Pair<String, List<RequestType>> handlerRequestMappings = requestHandler.getRequestMappings();
      for (RequestType requestType : handlerRequestMappings.second) {
        requestMappings.put(Pair.of(handlerRequestMappings.first, requestType), requestHandler);
      }
    }
    return requestMappings;
  }

  private static List<RequestHandler> getRequestHandlers() {
    List<RequestHandler> requestHandlers = new ArrayList<>();
    for (ClassInfo classInfo : getRequestHandlerImplementations()) {
      try {
        requestHandlers.add((RequestHandler) Class.forName(classInfo.getClassName()).newInstance());
      } catch (Exception e) {
        log.error("Failed to initate requestHandler! ", e);
      }
    }
    return requestHandlers;
  }

  private static Collection<ClassInfo> getRequestHandlerImplementations() {
    Collection<ClassInfo> foundClasses = new ArrayList<>();
    try {
      foundClasses = ClassUtils.getImplementingInterface(RequestHandler.class);
    } catch (Exception e) {
      log.error("Failed to retrieve all implementations of " + RequestHandler.class.getSimpleName(), e);
    }
    return foundClasses;
  }

  protected static RequestHandler getRequestAction(String url, RequestType type) {
    RequestHandler action = mappings.get(Pair.of(url, type));
    if (action == null) throw new IllegalArgumentException("Invalid request URL: " + url + "!");
    return action;
  }
}