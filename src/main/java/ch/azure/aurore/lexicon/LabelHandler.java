package ch.azure.aurore.lexicon;

import ch.azure.aurore.Strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
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

            // disable nav while navigating
            main.getMenuHandler().setAllowNavigation(!t1);
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

            Set<String> validLabels = patterns.keySet().stream().
                    filter(s -> main.getEntries().stream().noneMatch(entryContent -> {
                        String labelStr = EntryContent.toLabelStr(entryContent.getLabels());
                        Matcher matcher = patterns.get(s).matcher(labelStr);
                        return entryContent != entry && matcher.matches();
                    })).
                    collect(Collectors.toSet());
            entry.setLabels(validLabels);

            //if (entry.save()){ ;
            // main.entriesListView.getSelectionModel().getSelectedItem().
        }
        String labelStr = EntryContent.toLabelStr(entry.getLabels());
        main.labelsTextField.setText(labelStr);
    }
}
