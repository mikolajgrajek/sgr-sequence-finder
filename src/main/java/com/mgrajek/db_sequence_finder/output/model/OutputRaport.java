package com.mgrajek.db_sequence_finder.output.model;

import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Data
public class OutputRaport {
  private List<OuputRangeMatch> rangeMatches = new ArrayList<>();
  private final static Configuration freeMarkerConfig = new Configuration();


  static {
    //freeMarkerConfig.setObjectWrapper(new DefaultObjectWrapper());
    BeansWrapperBuilder beansWrapperBuilder = new BeansWrapperBuilder(new Version(2, 3, 23));
    freeMarkerConfig.setObjectWrapper(beansWrapperBuilder.build());
    freeMarkerConfig.setDefaultEncoding("UTF-8");
    freeMarkerConfig.setTemplateLoader(new ClassTemplateLoader(OutputRaport.class, "/"));
    freeMarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
    freeMarkerConfig.setIncompatibleImprovements(new Version(2, 3, 23));
  }


  public void write(File root) throws IOException, TemplateException {
    try (PrintWriter pwLine = new PrintWriter(new File(root, "output.html"), "UTF-8")) {
      final Template lineTemplate = freeMarkerConfig.getTemplate("com/mgrajek/db_sequence_finder/output/model/freemarker/Raport.ftl", "UTF-8");
      lineTemplate.process(this, pwLine);
      pwLine.flush();
    }
    try (PrintWriter pwLine = new PrintWriter(new File(root, "seqences.txt"), "UTF-8")) {
      for (OuputRangeMatch rangeMatch : rangeMatches) {
        pwLine.write("\n---------------------\n\n" + rangeMatch.getRangeName() + "\n");
        for (OuputSequenceMatch seq : rangeMatch.getMatchedSequences()) {
          pwLine.write(seq.getNucleotydesText() + "\n");
        }
      }
    }
  }

  public void addRangeMatch(OuputRangeMatch currentRangeMatch) {
    rangeMatches.add(currentRangeMatch);
  }
}
