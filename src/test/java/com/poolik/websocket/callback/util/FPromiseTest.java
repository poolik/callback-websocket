package com.poolik.websocket.callback.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FPromiseTest {

  private ExecutorService executorService = Executors.newCachedThreadPool();

  public static class DoSomething implements Callable<Collection<String>> {
    long d;
    public DoSomething(long d) {
      this.d = d;
    }

    public Collection<String> call() throws Exception {
      Thread.sleep(d);
      if (d > 200) {
        return Arrays.asList("-> " + d);
      }
      return new ArrayList<>();
    }
  }

  public static class DoSomething2 implements Callable<Collection<String>> {
    long d;
    public DoSomething2(long d) {
      this.d = d;
    }

    public Collection<String> call() throws Exception {
      Thread.sleep(d);
      return Arrays.asList("-> " + d);
    }
  }

  @Test
  public void waitAny() throws Exception {
    boolean p = false;
    for (String s : F.Promise.waitAny(getPromise(new DoSomething(300)), getPromise(new DoSomething(250))).get()) {
      assertEquals("-> 250", s);
      p = true;
    }
    assertTrue("Loop missed?", p);

    for (String s : F.Promise.waitAny(getPromise(new DoSomething(100)), getPromise(new DoSomething(250))).get()) {
      fail("Oops");
    }
  }

  @Test
  public void waitAllIsDoneRequiresAllPromisesToBeDone() throws Exception {
    F.Promise<Object> firstPromise = new F.Promise<>();
    F.Promise<Object> secondPromise = new F.Promise<>();
    F.Promise<Object> thirdPromise = new F.Promise<>();
    firstPromise.invoke(null);
    secondPromise.invoke(null);
    assertTrue(F.Promise.waitAll(firstPromise, secondPromise).isDone());
    assertFalse(F.Promise.waitAll(thirdPromise, secondPromise).isDone());
  }

  @Test(expected = TimeoutException.class)
  public void waitAllThrowsTimeoutException() throws Exception {
    F.Promise<Object> firstPromise = new F.Promise<>();
    F.Promise<Object> secondPromise = new F.Promise<>();
    firstPromise.invoke(null);
    F.Promise.waitAll(firstPromise, secondPromise).get(100, TimeUnit.MILLISECONDS);
  }

  @Test
  public void getSuccessfulRetrievesPromisesThatFinishedWithoutException() throws Exception {
    F.Promise<Object> firstPromise = new F.Promise<>();
    F.Promise<Object> secondPromise = new F.Promise<>();
    F.Promise<Object> thirdPromise = new F.Promise<>();
    firstPromise.invoke(new Object());
    secondPromise.invoke(new Object());
    thirdPromise.invokeWithException(new Throwable());
    assertThat(F.Promise.getSuccessful(Arrays.asList(firstPromise, secondPromise, thirdPromise)).size(), is(2));
  }

  private F.Promise<Collection<String>> getPromise(Callable<Collection<String> > callable) {
    F.Promise<Collection<String>> promise = new F.Promise<>();
    executorService.submit(new PromiseTask<>(callable, promise)); return promise;
  }

  @Test
  public void waitAll() throws Exception {
    List<Collection<String>> s = F.Promise.waitAll(getPromise(new DoSomething2(200)), getPromise(new DoSomething2(10))).get();
    assertEquals(2, s.size());
    assertEquals("-> 200", s.get(0).iterator().next());
    assertEquals("-> 10", s.get(1).iterator().next());

  }
}