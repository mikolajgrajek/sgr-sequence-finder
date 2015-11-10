package com.mgrajek.db_sequence_finder.output.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OuputRangeMatch {
  private String rangeName;
  private List<OuputSequenceMatch> matchedSequences = new ArrayList<>();

  public boolean hasMatches() {
    return !matchedSequences.isEmpty();
  }

  public void addMatchedSequence(OuputSequenceMatch currentSequence) {
    matchedSequences.add(currentSequence);
  }
}
