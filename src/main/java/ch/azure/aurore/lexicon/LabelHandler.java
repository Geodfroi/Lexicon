package ch.azure.aurore.lexicon;

import ch.azure.aurore.Collections.CollectionSt;
import ch.azure.aurore.Strings.Strings;
import ch.azure.aurore.lexiconDB.EntriesLink;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LabelHandler {

    public static Pattern getSearchPattern(String str){
        return Pattern.compile("^.*\\b"+ str + "[sx]*\\b.*$");
    }

    private final MainController main;

    public LabelHandler(MainController main) {
        this.main = main;

        main.labelsTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                validateLabels();
        });
    }

    private void validateLabels() {
        System.out.println("lost focus");
        EntryContent entry = main.getCurrentEntry();
        if (entry == null)
            return;
        String text = main.labelsTextField.getText();
        if (Strings.isNullOrEmpty(text)) {
            main.labelsTextField.setText(entry.getLabels());
        } else {

            Map<String,Pattern> patterns = Arrays.stream(text.split(", *")).
                    map(Strings::camel).
                    collect(Collectors.toMap(s -> s, LabelHandler::getSearchPattern));

            List<String> validLabels = patterns.keySet().stream().
                    filter(s -> main.getEntries().stream().noneMatch(entryContent -> {
                        Matcher matcher = patterns.get(s).matcher(entryContent.getLabels());
                        return entryContent !=  entry && matcher.matches();
                    })).
                    collect(Collectors.toList());
            entry.setLabels(validLabels);

           //if (entry.save()){ ;
            main.labelsTextField.setText(entry.getLabels());
            // main.entriesListView.getSelectionModel().getSelectedItem().

        }
    }
}
