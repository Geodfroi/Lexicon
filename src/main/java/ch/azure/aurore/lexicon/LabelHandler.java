package ch.azure.aurore.lexicon;

import ch.azure.aurore.Strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        EntryContent entry = main.getCurrentEntry();
        if (entry == null)
            return;
        String text = main.labelsTextField.getText();
        if (!Strings.isNullOrEmpty(text)) {

            Map<String, Pattern> patterns = Arrays.stream(text.split(", *")).
                    map(Strings::camel).
                    collect(Collectors.toMap(s -> s, LabelHandler::getSearchPattern));

            List<String> validLabels = patterns.keySet().stream().
                    filter(s -> main.getEntries().stream().noneMatch(entryContent -> {
                        Matcher matcher = patterns.get(s).matcher(entryContent.getLabels());
                        return entryContent != entry && matcher.matches();
                    })).
                    collect(Collectors.toList());
            entry.setLabels(validLabels);

            //if (entry.save()){ ;
            // main.entriesListView.getSelectionModel().getSelectedItem().
        }
        main.labelsTextField.setText(entry.getLabels());
    }
}
