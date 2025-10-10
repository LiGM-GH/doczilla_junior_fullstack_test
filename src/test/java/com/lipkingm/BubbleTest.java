package com.lipkingm;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class BubbleTest
    extends TestCase {
  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public BubbleTest(String testName) {
    super(testName);
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite() {
    return new TestSuite(BubbleTest.class);
  }

  public void testBubblesFirst() {
    int[][] input = {
        { 1 },
        {}
    };

    Tuple2[] output = {};

    assertBubbles(input, output);
  }

  public void testBubblesSecond() {
    int[][] input = {
        { 1, 2 },
        {},
    };

    Tuple2[] output = {
        new Tuple2(0, 1)
    };

    assertBubbles(input, output);
  }

  public void testBottlePairs() {
    assertBottlePair(
        new ArrayList<>(
            Arrays.asList(
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>())),
        new Integer[] { 0, 1 });
  }

  static void assertBottlePair(ArrayList<ArrayList<Integer>> input, Integer[] output) {
    Integer[] real_output = Bubbles.findBottlePair(input).toArray(new Integer[0]);

    assert Arrays.equals(real_output, output)
        : "Couldn't find a bottle pair: expected " + Arrays.toString(output)
            + "; found " + Arrays.toString(real_output);
  }

  // public void testBubbles() {
  // int[][] input = {
  // { 1, 1, 2, 2 },
  // { 1, 3, 3, 3 },
  // { 2, 2, 2, 1 },
  // {},
  // {}
  // };
  // Tuple2[] output = {
  // new Tuple2(0, 3),
  // new Tuple2(1, 3),
  // new Tuple2(2, 1),
  // new Tuple2(2, 0),
  // new Tuple2(3, 2)
  // };
  // assertBubbles(input, output);
  // }

  void assertBubbles(int[][] input, Tuple2[] output) {
    Tuple2[] real_output = Bubbles.bubble(input, 4);

    assert Arrays.deepEquals(real_output, output)
        : "Bubbles.bubble(" +
            printMatrix(input)
            + ") expected to result in " + printTarray(output)
            + " but resulted in " + printTarray(real_output);
  }

  String printMatrix(int[][] input) {
    return Arrays.toString(Arrays.stream(input).map((val) -> Arrays.toString(val)).toArray());
  }

  String printTarray(Tuple2[] input) {
    return Arrays.toString(Arrays.stream(input).map((val) -> "(" + val.field1 + ", " + val.field2 + ")").toArray());
  }
}
