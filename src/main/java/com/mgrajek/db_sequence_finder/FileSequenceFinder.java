package com.mgrajek.db_sequence_finder;

import com.mgrajek.db_sequence_finder.gb_format.model.GbFileData;
import com.mgrajek.db_sequence_finder.gb_format.parser.GbFileParser;
import com.mgrajek.db_sequence_finder.output.model.NucleotydeMatch;
import com.mgrajek.db_sequence_finder.output.model.OuputRangeMatch;
import com.mgrajek.db_sequence_finder.output.model.OuputSequenceMatch;
import com.mgrajek.db_sequence_finder.output.model.OutputRaport;
import com.mgrajek.db_sequence_finder.range.RangeData;
import com.mgrajek.db_sequence_finder.range.RangesFilter;
import com.mgrajek.db_sequence_finder.sgr_format.model.SgrIndex;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class FileSequenceFinder {

  private GbFileData gbFileData;
  private SgrIndex sgrIndex;
  private RangesFilter rangesFilter;

  private OutputRaport htmlRaport = new OutputRaport();

  public static void main(String[] args) throws IOException, TemplateException {
    if (args == null || args.length != 1) {
      System.out.println("Wrong line arguments, please provide directory with txt files.");
      return;
    }

    FileSequenceFinder finder = new FileSequenceFinder();

    GbFileParser gbFileParser = new GbFileParser();

    final File rootDir = new File(args[0]);
    System.out.println("Reading directory: " + rootDir.getAbsolutePath());

    final File rangesFilterFile = new File(rootDir, "ranges.txt");
    finder.rangesFilter = RangesFilter.loadFrom(rangesFilterFile);

    File[] result = rootDir.listFiles(o -> o.getName().toLowerCase().endsWith(".gb"));
    if (result != null && result.length != 1) {
      System.out.println("Only one file *.gb must be present in directory!");
      return;
    }

    finder.gbFileData = gbFileParser.loadData(result[0]);
    System.out.println("Loaded file: " + result[0].getAbsolutePath());
    finder.gbFileData.indexData();
    finder.gbFileData.printStats();

    File[] resultIndexes = rootDir.listFiles(o -> o.getName().toLowerCase().endsWith(".sgr"));
    if (resultIndexes != null && resultIndexes.length != 1) {
      System.out.println("Only one file *.sgr must be present in directory!");
      return;
    }

    finder.sgrIndex = SgrIndex.fromFile(resultIndexes[0]);


    finder.computeOutput();

    finder.htmlRaport.write(rootDir);
  }

  private OuputRangeMatch currentRangeMatch = new OuputRangeMatch();

  private OuputSequenceMatch currentSequence;

  int wholeSum = 0;
  int partialSum = 0;
  int nearestSum = 0;

  private void computeOutput() {
    System.out.println("Processing data...");

    for (RangeData range : rangesFilter.getRanges()) {
      currentRangeMatch.setRangeName(range.getName());
      System.out.println("");
      System.out.println("\tRANGE: " + range.getName());
      boolean wasMatching = false;
      for (int index = 0; index < gbFileData.genomeLength(); index++) {
        if (index % 100_000 == 0) {
          System.out.print(".");
        }
        Integer valueForIndex = sgrIndex.getValueForIndex(index);
        if (valueForIndex == null || !range.inRange(valueForIndex)) {
          if (wasMatching) {
            wasMatching = false;
            stopMatchingSequence(index - 1);
          }
        } else {
          if (wasMatching) {
            nextMatchedToSequence(index, valueForIndex);
          } else {
            startMatchingSequence(index, valueForIndex);
            wasMatching = true;
          }
        }
      }

      if (currentRangeMatch.hasMatches()) {
        htmlRaport.addRangeMatch(currentRangeMatch);
      }
      currentRangeMatch = new OuputRangeMatch();
    }

    System.out.printf("\nGene match stats:\n\twhole %d\n\tpartial: %d\n\tnearest: %d\n", wholeSum, partialSum, nearestSum);
  }

  private void startMatchingSequence(int index, Integer valueForIndex) {
    currentSequence = new OuputSequenceMatch();
    currentSequence.setStartIndex(index);

    nextMatchedToSequence(index, valueForIndex);
  }


  private void nextMatchedToSequence(int index, Integer valueForIndex) {
    String nucleotyde = gbFileData.getNucleotydeForIndex(index);
    currentSequence.addNucleotyde(NucleotydeMatch.of(nucleotyde, valueForIndex));
  }


  private void stopMatchingSequence(int lastIncludedIndex) {
    if (currentSequence == null) {
      return;
    }
    currentSequence.setEndIndex(lastIncludedIndex);

//    System.out.println("\t\tFound seq match: " + currentSequence.getStartIndex() + "-" + currentSequence.getEndIndex());
    gbFileData.addGeneMatches(currentSequence);

    wholeSum += currentSequence.getWholeGenesMatch().size();
    partialSum += currentSequence.getPartialGenesMatch().size();
    nearestSum += currentSequence.getRightNearestGene().size();

    currentRangeMatch.addMatchedSequence(currentSequence);
    currentSequence = null;
  }

}
