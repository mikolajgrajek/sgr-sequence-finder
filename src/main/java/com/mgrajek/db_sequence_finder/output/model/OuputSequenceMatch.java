package com.mgrajek.db_sequence_finder.output.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OuputSequenceMatch {
  private int startIndex;
  private int endIndex;
  private List<NucleotydeMatch> nucleotydes = new ArrayList<>();

  private List<OutputGeneInformation> wholeGenesMatch = new ArrayList<>();
  private List<OutputGeneInformation> partialGenesMatch = new ArrayList<>();

  private List<NearestGeneOutput> leftNearestGene = new ArrayList<>();
  private List<NearestGeneOutput> rightNearestGene = new ArrayList<>();

  public boolean isWholeGeneMatchedPresent() {
    return !wholeGenesMatch.isEmpty();
  }

  public boolean isPartialGeneMatchedPresent() {
    return !partialGenesMatch.isEmpty();
  }

  public boolean isLeftNearestGeneMatchedPresent() {
    return !leftNearestGene.isEmpty();
  }

  public boolean isRightNearestGeneMatchedPresent() {
    return !rightNearestGene.isEmpty();
  }

  public void addNucleotyde(NucleotydeMatch nucleotyde) {
    nucleotydes.add(nucleotyde);
  }


  public void addPartialMatched(OutputGeneInformation gene) {
    partialGenesMatch.add(gene);
  }

  public void addWholeMatched(OutputGeneInformation outputGeneInformation) {
    wholeGenesMatch.add(outputGeneInformation);
  }

  public String getNucleotydesText() {
    StringBuilder sb = new StringBuilder();
    for (NucleotydeMatch n : nucleotydes) {
      sb.append(n.getName().toUpperCase());
    }
    return sb.toString();
  }
}
