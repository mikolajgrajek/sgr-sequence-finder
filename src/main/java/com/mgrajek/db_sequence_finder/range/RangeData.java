package com.mgrajek.db_sequence_finder.range;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class RangeData {
  private int fromIndex = 0;
  private int toIndex = Integer.MAX_VALUE;

  private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)(\\.\\.(\\d+))?");

  public static RangeData from(String range) {
    final RangeData result = new RangeData();
    String trim = range.trim();
    Matcher matcher = RANGE_PATTERN.matcher(trim);
    if (!matcher.matches()) {
      return result;
    }
    result.fromIndex = Integer.parseInt(matcher.group(1));

    if (matcher.groupCount() == 3) {
      String group = matcher.group(3);
      if (group != null) {
        result.toIndex = Integer.parseInt(group);
      }
    }
    if (result.toIndex < result.fromIndex) {
      throw new RuntimeException("Unconsistent data!");
    }
    return result;
  }

  public static RangeData of(int start, int end) {
    RangeData result = new RangeData();
    result.setFromIndex(start);
    result.setToIndex(end);
    return result;
  }

  public boolean inRange(int valueForIndex) {
    return fromIndex <= valueForIndex && toIndex >= valueForIndex;
  }

  public List<Integer> getIndexes(int maxValue) {
    final List<Integer> result = new ArrayList<>();
    int toValue = Math.min(maxValue, toIndex);
    for (int i = fromIndex; i <= toValue; i++) {
      result.add(i);
    }
    return result;
  }

  public String getName() {
    return fromIndex + "-" + ((toIndex == Integer.MAX_VALUE) ? "x" : toIndex);
  }
}
