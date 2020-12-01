package ch.azure.aurore.lexicon.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class EntryContent {

    private final int id;
    private final List<String> labels;
    private String content;
    private boolean modified;

    public static List<String> labelsFromStr(String labelStr){
        if (labelStr == null || labelStr.isEmpty() || labelStr.isBlank())
            return new ArrayList<>();

        List<String> labels  = Arrays.asList(labelStr.split(", ").clone());
        labels.sort(Comparator.naturalOrder());
        return labels;
    }

    public EntryContent(int id, String content, String labelStr) {
        this.id = id;
        this.content = content;
        this.labels = labelsFromStr(labelStr);
    }

    public int getId() {
        return id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (!this.content.equals(content)){
            this.content = content;
            modified();
        }
    }

    private void modified() {
        this.modified = true;
    }

    public String getFirstLabel() {
        if (labels.size() == 0)
            return "";
        return labels.get(0);
    }

    public String getLabelStr() {
        StringBuilder str = new StringBuilder();
        for (int n = 0; n < labels.size(); n++) {
            str.append(labels.get(n));
            if (n < labels.size()-1)
                str.append(", ");
        }
        return str.toString();
    }

    public void save() {
        if (modified){
            modified = false;
            DataAccess.getInstance().updateEntry(this);
            System.out.println("saved");
        }
    }

    @Override
    public String toString() {
        return labels.get(0);
    }
}
