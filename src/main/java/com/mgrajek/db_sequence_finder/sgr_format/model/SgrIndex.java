package com.mgrajek.db_sequence_finder.sgr_format.model;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class SgrIndex {
  final private TreeMap<Integer, Integer> valueByIndex = new TreeMap<>();

  private static final Pattern SPLIT = Pattern.compile("\\s+");

  public static SgrIndex fromFile(File resultIndex) throws IOException {
    SgrIndex result = new SgrIndex();
    List<String> strings = FileUtils.readLines(resultIndex);
    for (String line : strings) {
      String[] split = SPLIT.split(line);
      if (!split[0].equals("chr")) {
        continue;
      }
      result.valueByIndex.put(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }
    System.out.println("SGR index loaded: " + result.valueByIndex.size() + " entries.");
    return result;
  }

  public Integer getValueForIndex(int index) {
    return valueByIndex.get(index);
  }
}
