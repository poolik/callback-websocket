package com.poolik.websocket.callback.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PairTest {

  @Test
  public void comparesFirstElementsFirst() throws Exception {
    Pair<Integer, Integer> firstPair = Pair.of(1, 2);
    Pair<Integer, Integer> secondPair = Pair.of(3, 2);

    assertThat(firstPair.compareTo(secondPair), is(-1));
    assertThat(secondPair.compareTo(firstPair), is(1));
    assertThat(secondPair.compareTo(secondPair), is(0));
  }

  @Test
  public void ifFirstElementsAreEqualComparesSecondElement() throws Exception {
    Pair<Integer, Integer> firstPair = Pair.of(1, 2);
    Pair<Integer, Integer> secondPair = Pair.of(1, 3);

    assertThat(firstPair.compareTo(secondPair), is(-1));
    assertThat(secondPair.compareTo(firstPair), is(1));
  }

  @Test
  public void handlesNullObjects() throws Exception {
    Pair<Integer, Integer> firstPair = Pair.of(null, 2);
    Pair<Integer, Integer> secondPair = Pair.of(1, 3);
    assertThat(firstPair.compareTo(secondPair), is(-1));
  }

  @Test
  public void ifBothAreNullThenEqual() throws Exception {
    Pair<Integer, Integer> firstPair = Pair.of(1, null);
    Pair<Integer, Integer> secondPair = Pair.of(1, null);
    assertThat(firstPair.compareTo(secondPair), is(0));
  }

  @Test
  public void equalsFalseIfNotPair() throws Exception {
    assertFalse(Pair.of(1, null).equals(new Object()));
  }

  @Test
  public void equalsTrueIfSameObject() throws Exception {
    Pair<Integer, Integer> firstPair = Pair.of(1, null);
    assertTrue(firstPair.equals(firstPair));
  }
}