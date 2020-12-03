package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexiconDB.EntriesLink;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.scene.control.Hyperlink;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LinkHandler {

    private final MainController main;
    private String linksLabel;
    private Set<EntriesLink> links;

    public LinkHandler(MainController mainController) {
        this.main = mainController;

        main.linksTextFlow.setOnMouseClicked(mouseEvent -> switchToEdit());
        main.linksTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> textFieldFocus(t1));
    }

    private void updateLinks(String linkStr) {

        List<Pattern> patterns = Arrays.stream(linkStr.split(", *")).
                map(s -> Pattern.compile("^.*\\b"+ s +"\\b.*$")).collect(Collectors.toList());

        Set<EntriesLink> newLinks = main.getEntries().stream().filter(entryContent -> {
            for (Pattern pattern:patterns) {
                Matcher matcher = pattern.matcher(entryContent.getLabels());
                if (matcher.matches())
                    return true;
            }
            return false;
        }).map(e -> new EntriesLink(main.getCurrentEntry().getId(), e.getId())).collect(Collectors.toSet());

        Set<EntriesLink> toRemove = new HashSet<>(links);
        toRemove.removeAll(newLinks);

        for (var link:toRemove) {
            LexiconDatabase.getInstance().removeLink(link);
        }

        Set<EntriesLink> toRecord = new HashSet<>(newLinks);
        toRecord.removeAll(links);
        for (var link:toRecord) {
            LexiconDatabase.getInstance().insertLink(link);
        }
    }

    private void textFieldFocus(Boolean hasFocus) {
        if (hasFocus){
            main.linksTextField.setText(linksLabel);
            main.linksTextFlow.getChildren().clear();
        }else{
            updateLinks(main.linksTextField.getText());
            main.linksTextField.clear();
            setTextFlow();
        }
    }

    private void switchToEdit() {
        main.linksTextField.requestFocus();
        main.linksTextField.end();
    }

    public void setTextFlow() {
        EntryContent currentEntry = main.getCurrentEntry();
        main.linksTextFlow.getChildren().clear();
        if (currentEntry == null || currentEntry.isEmpty()) {
            return;
        }

        links = LexiconDatabase.getInstance().queryLinks(currentEntry.getId());
        if (links.size() == 0)
            return;

        List<Integer> linkEntriesID = links.stream().
                map(e -> e.getOtherID(currentEntry.getId())).collect(Collectors.toList());

        linksLabel = "";
        main.getEntries().stream().
                filter(entryContent -> linkEntriesID.contains(entryContent.getId())).
                map(this::createLink).
                forEach(hyperlink -> main.linksTextFlow.getChildren().add(hyperlink));
    }

    private Hyperlink createLink(EntryContent entry) {
        String label = entry.getLabels().split(", *")[0] + ", ";
        linksLabel +=label;
        Hyperlink link = new Hyperlink(label);
        link.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(entry));
        return link;
    }
}