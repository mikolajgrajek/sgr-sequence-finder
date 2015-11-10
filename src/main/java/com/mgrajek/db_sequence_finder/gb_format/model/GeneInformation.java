package com.mgrajek.db_sequence_finder.gb_format.model;

import com.mgrajek.db_sequence_finder.output.model.OutputGeneInformation;
import com.mgrajek.db_sequence_finder.range.RangeData;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GeneInformation {
  private String name;
  private boolean complement;
  private List<RangeData> ranges = new ArrayList<>();

  public void addRange(int start, int end) {
    ranges.add(RangeData.of(start, end));
  }

  public void addRange(RangeData from) {
    ranges.add(from);
  }

  public OutputGeneInformation toOutput() {
    OutputGeneInformation result = new OutputGeneInformation();
    result.setComplement(complement);
    result.setName(name);
    return result;
  }
}
