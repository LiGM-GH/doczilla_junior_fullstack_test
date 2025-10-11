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

    assertBubbles(1, input, output);
  }

  public void testBubblesSecond() {
    int[][] input = {
        { 1, 2 },
        {},
    };

    Tuple2[] output = {
        new Tuple2(0, 1)
    };

    assertBubbles(2, input, output);
  }

  public void testBottlePairs() {
    assertBottlePair(
        2,
        new ArrayList<>(
            Arrays.asList(
                new ArrayList<>(Arrays.asList(1, 2)),
                new ArrayList<>())),
        new BottlePair(0, 1));
  }

  static void assertBottlePair(int volume, ArrayList<ArrayList<Integer>> input, BottlePair output) {
    BottlePair real_output = Bubbles.findBottlePair(input, volume);

    assert real_output.equals(output)
        : "Couldn't find a bottle pair: expected " + output
            + "; found " + real_output;
  }

  public void testBubbles() {
    int[][] input = {
        { 2, 2, 1, 1 },
        { 3, 3, 3, 1 },
        { 1, 3, 2, 2 },
        {},
        {}
    };
    assertBubblesOk(input, 4);
  }

  void assertBubbles(int volume, int[][] input, Tuple2[] output) {
    Tuple2[] real_output = Bubbles.bubble(input, volume);

    assert Arrays.deepEquals(real_output, output)
        : "Bubbles.bubble(" +
            printMatrix(input)
            + ") expected to result in " + printTarray(output)
            + " but resulted in " + printTarray(real_output);
  }

  void assertBubblesOk(int[][] input, int volume) {
    assert Bubbles.bubble(input, volume) != null;
  }

  String printMatrix(int[][] input) {
    return Arrays.toString(Arrays.stream(input).map((val) -> Arrays.toString(val)).toArray());
  }

  String printTarray(Tuple2[] input) {
    return Arrays.toString(Arrays.stream(input).map((val) -> "(" + val.field1 + ", " + val.field2 + ")").toArray());
  }
}
