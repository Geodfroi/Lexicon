package ch.azure.aurore.lexicon;

import ch.azure.aurore.Strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LinkHandler {

    private final MainController main;
    private EntryContent currentEntry;

    public LinkHandler(MainController mainController) {
        this.main = mainController;

        main.linksTextFlow.setOnMouseClicked(this::switchToEdit);
        main.linksTextArea.focusedProperty().
                addListener((observableValue, aBoolean, t1) -> textFieldFocus(t1));
    }

    private Hyperlink createLink(EntryContent entry) {
        String label = entry.getFirstLabel() + ", ";
        Hyperlink link = new Hyperlink(label);
        link.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(entry));
        return link;
    }

    private void switchToEdit(MouseEvent event) {
        main.linksTextArea.requestFocus();
        main.linksTextArea.end();

        event.consume();
    }

    public void setTextFlow(EntryContent current) {
        main.linksTextFlow.getChildren().clear();
        if (currentEntry == null) {
            return;
        }

        List<Hyperlink> toSort = new ArrayList<>();
        for (int i : LexiconDatabase.getInstance().queryEntryLinks(current.getId())) {
            Optional<EntryContent> entry = LexiconDatabase.getInstance().queryEntry(i);
            if (entry.isPresent()){
                Hyperlink hyperlink = createLink(entry.get());
                toSort.add(hyperlink);
            }
        }
        toSort.sort((o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));
        toSort.forEach(hyperlink -> main.linksTextFlow.getChildren().add(hyperlink));
    }

    private void textFieldFocus(Boolean hasFocus) {
        main.getMenuHandler().setAllowNavigation(!hasFocus);
        if (hasFocus){
            currentEntry = main.getCurrentEntry();
            Stream<String> st = LexiconDatabase.getInstance().
                    queryEntryLinks(currentEntry.getId()).stream().
                    map(main::getByID).
                    map(EntryContent::getFirstLabel);

            String linksLabel = Strings.toString(st, ", ");

            main.linksTextArea.setText(linksLabel);
            main.linksTextFlow.getChildren().clear();
        }else{
            updateLinks(main.linksTextArea.getText());
            main.linksTextArea.clear();
            setTextFlow(currentEntry);
        }
    }

    private void updateLinks(String linkStr) {

        List<Pattern> patterns = Arrays.stream(linkStr.split(", *")).
                map(s -> LabelHandler.getSearchPattern(Strings.camel(s))).
                collect(Collectors.toList());

        Set<EntryContent> newLinks = main.getEntries().stream().
                filter(e -> {
                    for (Pattern pattern:patterns) {

                        Matcher matcher = pattern.matcher(e.getLabelStr());
                        if (matcher.matches())
                            return true;
                    }
                    return false;
                }).collect(Collectors.toSet());
        Set<Integer> newLinkIds = newLinks.stream().
                map(EntryContent::getId).
                collect(Collectors.toSet());

        Set<Integer> oldLinkIds = LexiconDatabase.getInstance().
                queryEntryLinks(currentEntry.getId());

        Set<Integer> toRemove = new HashSet<>(oldLinkIds);
        toRemove.removeAll(newLinkIds);

        Set<Integer> toRecord = new HashSet<>(newLinkIds);
        toRecord.removeAll(oldLinkIds);

        for (int id:toRemove) {
            LexiconDatabase.getInstance().removeLink(id, currentEntry.getId());
        }
        for (int id:toRecord){
            LexiconDatabase.getInstance().insertLink(id, currentEntry.getId());
        }
    }
}