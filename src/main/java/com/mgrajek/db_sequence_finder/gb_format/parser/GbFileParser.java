package com.mgrajek.db_sequence_finder.gb_format.parser;

import com.mgrajek.db_sequence_finder.gb_format.model.GbFileData;
import com.mgrajek.db_sequence_finder.gb_format.model.GeneInformation;
import com.mgrajek.db_sequence_finder.range.RangeData;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class GbFileParser {
  final GbFileData result = new GbFileData();
  GeneInformation currentGeneInfo = null;

  public GbFileData loadData(File file) throws FileNotFoundException {
    int lineIndex = 0;
    try (Scanner scanner = new Scanner(file, "UTF-8")) {
      boolean sequenceStarted = false;
      while (scanner.hasNext()) {
        final String line = scanner.nextLine();
        lineIndex++;
        String trim = line.trim();
        if (trim.startsWith("//")) {
          return result;
        }

        if (trim.startsWith("gene")) {
          currentGeneInfo = new GeneInformation();
          result.addGene(currentGeneInfo);
          fillInRange(currentGeneInfo, trim);
          continue;
        }
        if (trim.startsWith("/gene=")) {
          if (currentGeneInfo == null) {
            continue;
          }
          currentGeneInfo.setName(extractName(trim));
          continue;
        }
        if (trim.startsWith("ORIGIN")) {
          sequenceStarted = true;
          continue;
        }
        if (sequenceStarted) {
          appendGeneSequence(trim);
        }
      }
    } catch (Exception e) {
      System.out.println("Cannot read file: LINE " + lineIndex + "\n message: " + e.getMessage());
    }
    return null;
  }

  private static final Pattern SPACES = Pattern.compile("\\s+");

  //      181 acaacatcca tgaaacgcat tagcaccacc attaccacca ccatcaccat taccacaggt
  private void appendGeneSequence(String trim) {
    String[] split = SPACES.split(trim);
    final Integer currentLength = Integer.parseInt(split[0]);

    if (result.genomeLength() != currentLength.intValue() - 1) {
      throw new RuntimeException("Mimatched genome length: from file: " + currentLength + " readed in data: " + result.genomeLength());
    }
    for (int i = 1; i < split.length; i++) {
      result.appendGenome(split[i]);
    }
  }

  // /gene="yjjX"
  private String extractName(String trim) {
    return trim.substring("/gene=\"".length(), trim.length() - 1);
  }

  private final static Pattern SPLIT_SEQUENCE = Pattern.compile(",");

  // CDS             complement(4631256..4631768)
  private void fillInRange(GeneInformation currentGeneInfo, String line) {
    line = line.substring(4).trim();
    String internal = line;
    if (internal.contains("complement")) {
      internal = internal.substring("complement(".length(), internal.length() - 1);
      currentGeneInfo.setComplement(true);
    }
    if (internal.contains("join")) {
      internal = internal.substring("join(".length(), internal.length() - 1);
      String[] split = SPLIT_SEQUENCE.split(internal);
      for (String range : split) {
        currentGeneInfo.addRange(RangeData.from(range));
      }
    } else {
      currentGeneInfo.addRange(RangeData.from(internal));
    }
  }
}
