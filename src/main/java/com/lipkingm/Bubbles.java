package com.lipkingm;

import java.util.ArrayList;
import java.util.Arrays;

public class Bubbles {
  public static void main(String[] args) {
  }

  public static Tuple2[] bubble(int[][] args, int volume) {
    ArrayList<Tuple2> result = new ArrayList<Tuple2>();

    ArrayList<ArrayList<Integer>> input = new ArrayList<>();

    for (int i = 0; i < args.length; i++) {
      for (int j = 0; j < args[i].length; j++) {
        input.add(new ArrayList<>());
        input.get(i).add(args[i][j]);
      }
    }

    int iter_counter = 0;
    while (!sorted(input)) {
      System.out.println("Not sorted yet!");
      // Let's find a pair of bottles that need mixing

      ArrayList<Integer> bottlepair = findBottlePair(input);

      if (bottlepair == null) {
        return null;
      }

      // Now we found the bottlepair
      // we'll need to mix them
      ArrayList<Integer> left_bottle  = input.get(bottlepair.get(0));
      ArrayList<Integer> right_bottle = input.get(bottlepair.get(1));

      if (left_bottle.size() == 0) {
        int upper_color = right_bottle.get(right_bottle.size() - 1);

        for (int i = right_bottle.size() - 1; i >= 0 && right_bottle.get(i) == upper_color; i--) {
          int removed_color = right_bottle.remove(i);
          left_bottle.add(removed_color);
        }
        result.add(new Tuple2(bottlepair.get(1), bottlepair.get(0)));
      } else if (right_bottle.size() == 0) {
        int upper_color = left_bottle.get(left_bottle.size() - 1);

        for (int i = left_bottle.size() - 1; i >= 0 && left_bottle.get(i) == upper_color; i--) {
          int removed_color = left_bottle.remove(i);
          right_bottle.add(removed_color);
        }
        result.add(new Tuple2(bottlepair.get(0), bottlepair.get(1)));
      } else if (left_bottle.size() < volume) {
        // We here are sure that left_bottle has enouhg space for all that we're going to push there - it must be checked in the findBottlePair (but it's not now)
        int upper_color = right_bottle.get(right_bottle.size() - 1);

        for (int i = right_bottle.size() - 1; i >= 0 && right_bottle.get(i) == upper_color; i--) {
          int removed_color = right_bottle.remove(i);
          left_bottle.add(removed_color);
        }
        result.add(new Tuple2(bottlepair.get(1), bottlepair.get(0)));
      } else {
        // Now right_bottle.size() < volume
        // checked by findBottlePair
        int upper_color = left_bottle.get(left_bottle.size() - 1);

        for (int i = left_bottle.size() - 1; i >= 0 && left_bottle.get(i) == upper_color; i--) {
          int removed_color = left_bottle.remove(i);
          right_bottle.add(removed_color);
        }
        result.add(new Tuple2(bottlepair.get(0), bottlepair.get(1)));
      }

      iter_counter++;

      if (iter_counter > 1000) {
        break;
      }
    }

    return result.toArray(new Tuple2[0]);
  }

  // TODO: Check if the bottle has enouhg place for the drops
  static ArrayList<Integer> findBottlePair(ArrayList<ArrayList<Integer>> input) {
    ArrayList<Integer> bottlepair = new ArrayList<>(2);
    int bottlepair_color = -1;

    for (int i = 0; i < input.size(); i++) {
      ArrayList<Integer> current_bottle = input.get(i);

      if (current_bottle.size() == 0) {
        if (bottlepair.size() == 1 && input.get(bottlepair.get(0)).size() == 0) {
          continue;
        }

        bottlepair.add(i);

        if (bottlepair.size() == 2) {
          return bottlepair;
        }
      }

      int current_bottle_last = current_bottle.get(current_bottle.size() - 1);

      if (bottlepair_color == -1) {
        bottlepair_color = current_bottle_last;
      }

      if (current_bottle_last == bottlepair_color) {
        bottlepair.add(i);

        if (bottlepair.size() == 2) {
          return bottlepair;
        }
      }
    }

    System.out.println("Currently the bottlepair is " + Arrays.toString(bottlepair.toArray(new Integer[0])));

    return null;
  }

  static boolean sorted(ArrayList<ArrayList<Integer>> args) {
    System.out.println("Entered sorted; args.size() = " + args.size());

    for (int i = 0; i < args.size(); i++) {
      if (args.get(i).size() == 0) {
        continue;
      }

      int first = args.get(i).get(0);

      System.out.println("First is " + first);

      for (int j = 1; j < args.get(i).size(); j++) {
        System.out.println("Obviously, " + args.get(i).get(j) + " is not equal to " + first);
        if (first != args.get(i).get(j)) {
          return false;
        }
      }
    }

    return true;
  }
}
