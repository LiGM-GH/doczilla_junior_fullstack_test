package com.lipkingm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

class BottlePair {
  public int donor;
  public int recipient;

  public BottlePair(int donor, int recipient) {
    this.donor = donor;
    this.recipient = recipient;
  }

  public BottlePair() {
    donor = -1;
    recipient = -1;
  }

  @Override
  public boolean equals(Object peer) {
    if (!(peer instanceof BottlePair)) {
      return false;
    }

    BottlePair rhs = (BottlePair) peer;

    return (this.donor == rhs.donor) && (this.recipient == rhs.recipient);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.donor, this.recipient);
  }

  @Override
  public String toString() {
    return "BottlePair(" + this.donor + " -> " + this.recipient + ")";
  }
}

public class Bubbles {
  public static void main(String[] _args) throws IOException {
    ArrayList<ArrayList<Integer>> args = new ArrayList<ArrayList<Integer>>();

    InputStreamReader reader = new InputStreamReader(System.in);
    BufferedReader stream = new BufferedReader(reader);
    String vol = stream.readLine();
    int volume = Integer.parseInt(vol);

    stream.lines().forEach(line -> {
      System.out.println("Line: " + line);
      ArrayList<Integer> list = new ArrayList<Integer>();

      for (String elem : line.split(" ")) {
        Integer result = null;

        try {
          result = Integer.parseInt(elem);
        } catch (NumberFormatException e) {
        }

        if (result == null) {
          list.add(elem.charAt(0) - 'a');
        } else {
          list.add(result);
        }
      }

      args.add(list);
    });

    int[][] val = new int[args.size()][];

    for (int i = 0; i < args.size(); i++) {
      val[i] = new int[args.get(i).size()];

      for (int j = 0; j < args.get(i).size(); j++) {
        val[i][j] = args.get(i).get(j);
      }
    }

    bubble(val, volume);
  }

  public static Tuple2[] bubble(int[][] args, int volume) {
    ArrayList<Tuple2> result = new ArrayList<Tuple2>();

    ArrayList<ArrayList<Integer>> input = new ArrayList<>(args.length);

    for (int i = 0; i < args.length; i++) {
      input.add(new ArrayList<>(volume));
      for (int j = 0; j < args[i].length; j++) {
        input.get(i).add(args[i][j]);
      }
    }

    System.out.println(
        "The input is "
            + input.stream()
                .map((val) -> "["
                    + val.stream().map((inner) -> inner.toString()).reduce((a, b) -> a + ", " + b).orElse("") + "]")
                .reduce((a, b) -> a + ", " + b).orElse(""));

    int iter_counter = 0;

    while (!sorted(input)) {
      System.out.println("Not sorted yet! state: " + Arrays.toString(
          input.stream()
              .map((val) -> "["
                  + val.stream().map((inner) -> inner.toString()).reduce((a, b) -> a + ", " + b).orElse("") + "]")
              .toArray()));
      // Let's find a pair of bottles that need mixing

      BottlePair bottlepair = findBottlePair(input, volume);

      if (bottlepair == null) {
        System.out.println("Couldn't find the bottlepair; this is current state: " + Arrays.toString(
            input.stream()
                .map((val) -> "["
                    + val.stream().map((inner) -> inner.toString()).reduce((a, b) -> a + ", " + b).orElse("") + "]")
                .toArray()));
        return null;
      }

      System.out.println("This is our bottlepair: "
          + bottlepair.donor + " -> " + bottlepair.recipient);

      ArrayList<Integer> donor_bottle = input.get(bottlepair.donor);
      ArrayList<Integer> recipient_bottle = input.get(bottlepair.recipient);

      int upper_color = donor_bottle.get(donor_bottle.size() - 1);

      for (int i = donor_bottle.size() - 1; i >= 0 && donor_bottle.get(i) == upper_color; i--) {
        int removed_color = donor_bottle.remove(i);
        recipient_bottle.add(removed_color);
      }
      result.add(new Tuple2(bottlepair.donor, bottlepair.recipient));

      iter_counter++;

      if (iter_counter > 1000) {
        break;
      }
    }

    System.out.println(
        "The mutated input is "
            + Arrays.toString(
                input.stream()
                    .map((val) -> "["
                        + val.stream().map((inner) -> inner.toString()).reduce((a, b) -> a + ", " + b).orElse("") + "]")
                    .toArray()));

    return result.toArray(new Tuple2[0]);
  }

  // TODO: Check if the bottle has enouhg place for the drops
  static BottlePair findBottlePair(ArrayList<ArrayList<Integer>> input, int volume) {
    BottlePair bottlepair = new BottlePair();
    int bottlepair_color = -1;

    int spare_emptybottle = -1;
    int space_needed = 0;
    int singlecolor_count = 0;
    int singlecolor_nonfull_volume = -1;

    for (int i = 0; i < input.size(); i++) {
      if (input.get(i).size() == 0) {
        singlecolor_count++;
        continue;
      }

      // Let's find drop donor first - it must not be an empty or single-color bottle

      boolean singlecolor = true;
      int first = input.get(i).get(0);
      for (int j = 1; j < input.get(i).size(); j++) {
        if (input.get(i).get(j) != first) {
          singlecolor = false;
          space_needed = j;
          break;
        }
      }

      if (singlecolor) {
        if (input.get(i).size() < volume) {
          singlecolor_nonfull_volume = i;
        }
        singlecolor_count++;
        continue;
      }

      // Now we know it's not single-color and not empty.
      bottlepair.donor = i;
      bottlepair_color = input.get(i).get(input.get(i).size() - 1);
    }

    // Yet, if we didn't find non-single-color and also found a non-full-volume one,
    // we can make him the donor
    if ((singlecolor_count == input.size()) && (singlecolor_nonfull_volume != -1)) {
      bottlepair.donor = singlecolor_nonfull_volume;
      bottlepair_color = input.get(singlecolor_nonfull_volume).get(0);
      space_needed = input.get(singlecolor_nonfull_volume).size();
    }

    for (int i = 0; i < input.size(); i++) {
      if (input.get(i).size() == 0) {
        // A potential recipient, if couldn't find any better.
        // Better is a bottle with the same color on top and some space.
        spare_emptybottle = i;

        continue;
      }

      if (i == bottlepair.donor) {
        continue;
      }

      if (input.get(i).get(input.get(i).size() - 1) != bottlepair_color) {
        continue;
      }

      if (volume - input.get(i).size() >= space_needed) {
        bottlepair.recipient = i;
      }
    }

    System.out.println("Currently the bottlepair is " + bottlepair.donor + " -> " + bottlepair.recipient);

    if (bottlepair.recipient == -1) {
      bottlepair.recipient = spare_emptybottle;
    }

    if (bottlepair.donor == -1 || bottlepair.recipient == -1) {
      return null;
    }

    return bottlepair;
  }

  static boolean sorted(ArrayList<ArrayList<Integer>> args) {
    System.out.println("Entered sorted; args.size() = " + args.size());
    HashSet<Integer> unique = new HashSet<>();

    for (int i = 0; i < args.size(); i++) {
      if (args.get(i).size() == 0) {
        continue;
      }

      int first = args.get(i).get(0);

      System.out.println("First is " + first);

      if (unique.contains(first)) {
        System.out.println("Found non-unique: " + first);
        return false;
      }

      for (int j = 1; j < args.get(i).size(); j++) {
        unique.add(args.get(i).get(j));

        if (first != args.get(i).get(j)) {
          System.out.println("Obviously, " + args.get(i).get(j) + " is not equal to " + first);
          return false;
        }

        System.out.println("Indeed, " + args.get(i).get(j) + " is the same as " + first);
      }
    }

    return true;
  }
}
