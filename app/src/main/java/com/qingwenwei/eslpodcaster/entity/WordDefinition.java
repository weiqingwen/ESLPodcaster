package com.qingwenwei.eslpodcaster.entity;

import java.util.ArrayList;
import java.util.List;

public class WordDefinition {
    private String definition = null;
    private List<String> examples = new ArrayList<>();

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        if (this.definition == null)
            this.definition = definition;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void addExample(String example) {
        this.examples.add(example);
    }
}
