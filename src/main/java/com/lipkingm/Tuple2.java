package com.lipkingm;

import java.util.Objects;

public class Tuple2 {
  public int field1;
  public int field2;

  public Tuple2(int field1, int field2) {
    this.field1 = field1;
    this.field2 = field2;
  }

  @Override
  public boolean equals(Object peer) {
    if (!(peer instanceof Tuple2)) {
      return false;
    }

    Tuple2 rhs = (Tuple2)peer;

    return (this.field1 == rhs.field1) && (this.field2 == rhs.field2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.field1, this.field2);
  }

  @Override
  public String toString() {
    return "Tuple2(" + this.field1 + ", " + this.field2 + ")";
  }
}

