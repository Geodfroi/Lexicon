package ch.azure.aurore.lexicon.data;

import java.util.Arrays;
import java.util.List;

public class EntryContent {

    int id;
    List<String> labels;
    String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = Arrays.asList(labels.split(", ").clone());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return labels.get(0);
    }
}
