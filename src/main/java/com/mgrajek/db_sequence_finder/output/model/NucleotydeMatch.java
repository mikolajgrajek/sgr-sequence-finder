package com.mgrajek.db_sequence_finder.output.model;

import lombok.Data;

@Data
public class NucleotydeMatch {
  String name;
  int value;

  public static NucleotydeMatch of(String nucleotyde, Integer valueForIndex) {
    NucleotydeMatch result = new NucleotydeMatch();
    result.name = nucleotyde.toUpperCase();
    result.value = valueForIndex;
    return result;
  }
}
