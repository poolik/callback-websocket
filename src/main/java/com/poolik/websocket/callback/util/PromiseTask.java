package com.poolik.websocket.callback.util;

import java.util.concurrent.Callable;

public class PromiseTask<V> implements Runnable {

  private final Callable<V> callable;
  private final F.Promise<V> promise;

  public PromiseTask(Callable<V> callable, F.Promise<V> promise) {
    this.callable = callable;
    this.promise = promise;
  }

  @Override
  public void run() {
    try {
      V result = callable.call();
      promise.invoke(result);
    } catch (Throwable t) {
      promise.invokeWithException(t);
    }
  }
}
