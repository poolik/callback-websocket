package com.poolik.websocket.callback.util;

import java.util.*;
import java.util.concurrent.*;

public class F {

  public static class Promise<V> implements Future<V>, F.Action<V> {

    protected final CountDownLatch taskLock = new CountDownLatch(1);
    protected boolean cancelled = false;

    public boolean cancel(boolean mayInterruptIfRunning) {
      return false;
    }

    public boolean isCancelled() {
      return false;
    }

    public boolean isDone() {
      return invoked;
    }

    public V getOrNull() {
      try {
        taskLock.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      return result;
    }

    public Throwable getException() { return exception; }

    public V get() throws InterruptedException, ExecutionException {
      taskLock.await();
      if (exception != null) {
        // The result of the promise is an exception - throw it
        throw new ExecutionException(exception);
      }
      return result;
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      if(!taskLock.await(timeout, unit)) {
        throw new TimeoutException(String.format("Promise didn't redeem in %s %s", timeout, unit));
      }

      if (exception != null) {
        // The result of the promise is an exception - throw it
        throw new ExecutionException(exception);
      }
      return result;
    }
    protected List<Action<Promise<V>>> callbacks = new ArrayList<Action<Promise<V>>>();
    protected boolean invoked = false;
    protected V result = null;
    protected Throwable exception = null;

    public void invoke(V result) {
      invokeWithResultOrException(result, null);
    }

    public void invokeWithException(Throwable t) {
      invokeWithResultOrException(null, t);
    }

    protected void invokeWithResultOrException(V result, Throwable t) {
      synchronized (this) {
        if (!invoked) {
          invoked = true;
          this.result = result;
          this.exception = t;
          taskLock.countDown();
        } else {
          return;
        }
      }
      for (F.Action<Promise<V>> callback : callbacks) {
        callback.invoke(this);
      }
    }

    public void onRedeem(F.Action<Promise<V>> callback) {
      synchronized (this) {
        if (!invoked) {
          callbacks.add(callback);
        }
      }
      if (invoked) {
        callback.invoke(this);
      }
    }

    public static <T> Promise<List<T>> waitAll(final Promise<T>... promises) {
      return waitAll(Arrays.asList(promises));
    }

    public static <T> Promise<List<T>> waitAll(final Collection<Promise<T>> promises) {
      final CountDownLatch waitAllLock = new CountDownLatch(promises.size());
      final Promise<List<T>> result = new Promise<List<T>>() {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
          boolean r = true;
          for (Promise<T> f : promises) {
            r = r & f.cancel(mayInterruptIfRunning);
          }
          return r;
        }

        @Override
        public boolean isCancelled() {
          boolean r = true;
          for (Promise<T> f : promises) {
            r = r & f.isCancelled();
          }
          return r;
        }

        @Override
        public boolean isDone() {
          boolean r = true;
          for (Promise<T> f : promises) {
            r = r & f.isDone();
          }
          return r;
        }

        @Override
        public List<T> get() throws InterruptedException, ExecutionException {
          waitAllLock.await();
          List<T> r = new ArrayList<T>();
          for (Promise<T> f : promises) {
            r.add(f.get());
          }
          return r;
        }

        @Override
        public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
          if(!waitAllLock.await(timeout, unit)) {
            throw new TimeoutException(String.format("Promises didn't redeem in %s %s", timeout, unit));
          }

          return get();
        }
      };
      final F.Action<Promise<T>> action = new F.Action<Promise<T>>() {

        public void invoke(Promise<T> completed) {
          waitAllLock.countDown();
          if (waitAllLock.getCount() == 0) {
            try {
              result.invoke(result.get());
            } catch (Exception e) {
              result.invokeWithException(e);
            }
          }
        }
      };
      for (Promise<T> f : promises) {
        f.onRedeem(action);
      }
      if(promises.isEmpty()) {
        result.invoke(Collections.<T>emptyList());
      }
      return result;
    }

    public static <T> List<T> getSuccessful(List<Promise<T>> promises) {
      List<T> successful = new ArrayList<>();
      for (F.Promise<T> promise : promises) {
        assert promise.isDone();
        T result = promise.getOrNull();
        if (result != null) successful.add(result);
      }
      return successful;
    }

    public static <T> Promise<T> waitAny(final Promise<T>... futures) {
      final Promise<T> result = new Promise<T>();

      final F.Action<Promise<T>> action = new F.Action<Promise<T>>() {

        public void invoke(Promise<T> completed) {
          synchronized (this) {
            if (result.isDone()) {
              return;
            }
          }
          T resultOrNull = completed.getOrNull();
          if(resultOrNull != null) {
            result.invoke(resultOrNull);
          }
          else {
            result.invokeWithException(completed.exception);
          }
        }
      };

      for (Promise<T> f : futures) {
        f.onRedeem(action);
      }

      return result;
    }
  }

  public static interface Action<T> {

    void invoke(T result);
  }


}
