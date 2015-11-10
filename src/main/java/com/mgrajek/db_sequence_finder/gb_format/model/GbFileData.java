package com.mgrajek.db_sequence_finder.gb_format.model;

import com.mgrajek.db_sequence_finder.output.model.NearestGeneOutput;
import com.mgrajek.db_sequence_finder.output.model.OuputSequenceMatch;
import com.mgrajek.db_sequence_finder.range.RangeData;

import java.util.*;

public class GbFileData {
  private List<GeneInformation> genesArray = new ArrayList<>();
  private StringBuilder genome = new StringBuilder();

  private TreeMap<Integer, Set<String>> geneNameForIndex = new TreeMap<>();
  private Map<String, GeneInformation> geneByName = new HashMap<>();

  public void appendGenome(String s) {
    genome.append(s);
  }

  public Integer genomeLength() {
    return genome.length();
  }

  public void addGene(GeneInformation currentGeneInfo) {
    genesArray.add(currentGeneInfo);
  }

  public void printStats() {
    System.out.println("Genes informations readed: " + genesArray.size());
    System.out.println("Genome length: " + genomeLength());
    System.out.println("Nucleotydes with genes: " + geneNameForIndex.size());
  }

  public void indexData() {
    System.out.println("indexing...");
    int counter = 0;
    for (GeneInformation gene : genesArray) {
      if (gene.getName() == null || gene.getName().length() == 0) {
        continue;
      }
      counter++;
      if (counter % 100 == 0) {
        System.out.println("\tgene " + counter + " / " + genesArray.size());
      }
      geneByName.put(gene.getName(), gene);
      for (RangeData rangeData : gene.getRanges()) {
        for (int index : rangeData.getIndexes(genome.length())) {
          Set<String> genes = geneNameForIndex.get(index);
          if (genes == null) {
            genes = new HashSet<>();
            geneNameForIndex.put(index, genes);
          }
          genes.add(gene.getName());
        }
      }
    }
  }

  public String getNucleotydeForIndex(int index) {
    return Character.toString(genome.charAt(index));
  }

  public void addGeneMatches(OuputSequenceMatch currentSequence) {
    Set<String> partialyMatched = new HashSet<>();
    Set<String> wholeMatched = new HashSet<>();

    for (int i = currentSequence.getStartIndex(); i < currentSequence.getEndIndex(); i++) {
      Set<String> genes = geneNameForIndex.get(i);
      if (genes == null || genes.isEmpty()) {
        wholeMatched.clear();
        continue;
      } else {
        wholeMatched.retainAll(genes);
      }
      partialyMatched.addAll(genes);
      if (i == currentSequence.getStartIndex()) {
        wholeMatched.addAll(genes);
      }
    }
    partialyMatched.removeAll(wholeMatched);

    for (String geneName : partialyMatched) {
      GeneInformation geneInformation = geneByName.get(geneName);
      currentSequence.addPartialMatched(geneInformation.toOutput());
    }

    for (String geneName : wholeMatched) {
      GeneInformation geneInformation = geneByName.get(geneName);
      currentSequence.addWholeMatched(geneInformation.toOutput());
    }
//    log.debug("\t\t\tpartial gene matches: " + partialyMatched);
//    System.out.println("\t\t\twhole gene matches: " + wholeMatched);

    Set<String> blackList = new HashSet<>();
    blackList.addAll(partialyMatched);
    blackList.addAll(wholeMatched);

    if (blackList.isEmpty()) {
      List<NearestGeneOutput> nearRight = findNearestGenes(currentSequence.getStartIndex(), blackList);
      currentSequence.setRightNearestGene(nearRight);

      List<NearestGeneOutput> nearLeft = findNearestComplementGenes(currentSequence.getStartIndex(), blackList);
      currentSequence.setLeftNearestGene(nearLeft);
    }
  }

  private List<NearestGeneOutput> findNearestGenes(int endIndex, Set<String> blackList) {
    for (int i = endIndex; i < genome.length(); i++) {
      Set<String> genes = geneNameForIndex.get(i);
      if (genes == null || genes.isEmpty()) {
        continue;
      }
      final List<NearestGeneOutput> result = new ArrayList<>();
      for (String geneName : genes) {
        if (blackList.contains(geneName)) {
          continue;
        }
        GeneInformation geneInformation = geneByName.get(geneName);
        if (!geneInformation.isComplement()) {
          result.add(NearestGeneOutput.of(geneInformation.toOutput(), i - endIndex));
        }
      }
      if (result.isEmpty()) {
        continue;
      }
      return result;
    }
    return null;
  }

  private List<NearestGeneOutput> findNearestComplementGenes(int startIndex, Set<String> blackList) {
    final List<NearestGeneOutput> result = new ArrayList<>();
    for (int i = startIndex; i >= 0; i--) {
      Set<String> genes = geneNameForIndex.get(i);
      if (genes == null || genes.isEmpty()) {
        continue;
      }
      for (String geneName : genes) {
        if (blackList.contains(geneName)) {
          continue;
        }
        GeneInformation geneInformation = geneByName.get(geneName);
        if (geneInformation.isComplement()) {
          result.add(NearestGeneOutput.of(geneInformation.toOutput(), startIndex - i));
        }
      }
      if (result.isEmpty()) {
        continue;
      }
      return result;
    }
    return null;
  }

}
