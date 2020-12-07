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

    public LinkHandler(MainController mainController) {
        this.main = mainController;

        main.linksTextFlow.setOnMouseClicked(this::switchToEdit);
        main.linksTextArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> textFieldFocus(t1));
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
                queryEntryLinks(main.getCurrentEntry().getId());

//        Set<EntryContent> oldLinks = main.getCurrentEntry().getLinks().stream().
//                map(main::getByID).
//                filter(Objects::nonNull).
//                collect(Collectors.toSet());

        Set<Integer> toRemove = new HashSet<>(oldLinkIds);
        toRemove.removeAll(newLinkIds);

        Set<Integer> toRecord = new HashSet<>(newLinkIds);
        toRecord.removeAll(oldLinkIds);

        for (int id:toRemove) {
            LexiconDatabase.getInstance().removeLink(id, main.getCurrentEntry().getId());
        }
        for (int id:toRecord){
            LexiconDatabase.getInstance().insertLink(id, main.getCurrentEntry().getId());
        }
    }

    private void textFieldFocus(Boolean hasFocus) {
        main.getMenuHandler().setAllowNavigation(!hasFocus);
        if (hasFocus){

            Stream<String> st = LexiconDatabase.getInstance().queryEntryLinks(main.getCurrentEntry().getId()).stream().
                    map(main::getByID).
                    map(EntryContent::getFirstLabel);

            String linksLabel = Strings.toString(st, ", ");

            main.linksTextArea.setText(linksLabel);
            main.linksTextFlow.getChildren().clear();
        }else{
            updateLinks(main.linksTextArea.getText());
            main.linksTextArea.clear();
            setTextFlow();
        }
    }

    private void switchToEdit(MouseEvent event) {
        main.linksTextArea.requestFocus();
        main.linksTextArea.end();

        event.consume();
    }

    public void setTextFlow() {
        EntryContent currentEntry = main.getCurrentEntry();
        main.linksTextFlow.getChildren().clear();
        if (currentEntry == null) {
            return;
        }

        List<Hyperlink> toSort = new ArrayList<>();
        for (Integer i : LexiconDatabase.getInstance().queryEntryLinks(main.getCurrentEntry().getId())) {
            Optional<EntryContent> entry = LexiconDatabase.getInstance().queryEntry(i);
            if (entry.isPresent()){
                Hyperlink hyperlink = createLink(entry.get());
                toSort.add(hyperlink);
            }
        }
        toSort.sort((o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));
        toSort.forEach(hyperlink -> main.linksTextFlow.getChildren().add(hyperlink));
    }

    private Hyperlink createLink(EntryContent entry) {
        String label = entry.getFirstLabel() + ", ";
        Hyperlink link = new Hyperlink(label);
        link.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(entry));
        return link;
    }
}