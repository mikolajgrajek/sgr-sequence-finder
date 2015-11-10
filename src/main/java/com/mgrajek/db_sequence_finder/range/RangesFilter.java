package com.mgrajek.db_sequence_finder.range;

import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RangesFilter {
  @Getter
  private List<RangeData> ranges = new ArrayList<>();

  private static final Pattern SPLIT = Pattern.compile("[,;]");

  public static RangesFilter loadFrom(File rangeFile) throws IOException {
    RangesFilter result = new RangesFilter();
    String s = FileUtils.readFileToString(rangeFile);
    String[] split = SPLIT.split(s);

    for (String range : split) {
      RangeData data = RangeData.from(range);
      result.ranges.add(data);
    }

    return result;
  }
}
