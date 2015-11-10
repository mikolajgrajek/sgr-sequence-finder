package com.mgrajek.db_sequence_finder.output.model;

import lombok.Data;

@Data
public class NearestGeneOutput {
  private OutputGeneInformation gene;
  private int distance;

  public static NearestGeneOutput of(OutputGeneInformation gene, int i) {
    NearestGeneOutput result = new NearestGeneOutput();
    result.gene = gene;
    result.distance = i;
    return result;
  }
}
