package edu.ncku.model.program.vo;

import java.util.LinkedHashSet;

public class ProgramConfigVO {
    private LinkedHashSet<String> resentFiles;

    public ProgramConfigVO(LinkedHashSet<String> resentFiles) {
        this.resentFiles = resentFiles;
    }

    public LinkedHashSet<String> getResentFiles() {
        return resentFiles;
    }

    public void setResentFiles(LinkedHashSet<String> resentFiles) {
        this.resentFiles = resentFiles;
    }
}
