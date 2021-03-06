package com.poolik.websocket.callback.request;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorResponse extends Response {
  public final String error;
  public final String stacktrace;

  public ErrorResponse(String callbackId, Throwable error) {
    super(callbackId, null);
    this.error = getErrorMessage(error);
    this.stacktrace = stackTraceToString(error);
  }

  private String getErrorMessage(Throwable error) {
    if (error.getMessage() == null) return "Request failed with: " + error.getClass().getSimpleName();
    return "Request failed with: " + error.getMessage();
  }

  private String stackTraceToString(Throwable error) {
    try (StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw)) {
      error.printStackTrace(pw);
      return sw.toString();
    } catch (IOException e) {
      return "";
    }
  }
}