package ch.azure.aurore.lexicon.data;

import java.util.ArrayList;

public class JSONEntry {
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<>();
    String key;
    String content;
    private int id;

    public int getID() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addLink(String string) {
        links.add(string);
    }

    public void addLabel(String string) {
        labels.add(string);
    }

    public void setId(int id) {
        this.id = id;
    }
}