package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TextLink{

    private final int endIndex;
    private final int startIndex;
    private final EntryContent entry;

    public EntryContent getEntry() {
        return entry;
    }

    public TextLink(EntryContent entry, int startIndex, int endIndex) {
        this.entry=entry;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    @Override
    public String toString() {
        return entry.getLabels() + "@"+ startIndex;
    }
}

public class TextLoader {

    private final MainController main;
    ChangeListener<String> textListener;

    public TextLoader(MainController main) {
        this.main = main;

        main.contentTextFlow.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                switchToEdit();
            }
        });
        textListener = (observableValue, s, t1) -> {
            if (main.getCurrentEntry() != null)
                main.getCurrentEntry().setContent(t1);
        };
        main.contentTextArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            textAreaFocus(t1);
        });
    }

    int count =0;
    private void textAreaFocus(Boolean hasFocus) {
        if (hasFocus){
            main.contentTextArea.setText(main.getCurrentEntry().getContent());
            main.contentTextArea.textProperty().addListener(textListener);
            count++;

            main.contentTextFlow.getChildren().clear();
            main.contentTextFlow.setFocusTraversable(false);
            main.contentTextFlow.setMouseTransparent(true);

        }else{
            System.out.println(main.getCurrentEntry().getContent());
            main.getCurrentEntry().save();

            main.contentTextArea.textProperty().removeListener(textListener);
            main.contentTextArea.setText("");

            main.contentTextFlow.setMouseTransparent(false);
            setTextFlow();
        }
    }

    public void setTextFlow() {

        var currentEntry = main.getCurrentEntry();

        main.contentTextFlow.getChildren().clear();
        if (currentEntry == null || currentEntry.isEmpty()) {
            return;
        }
        var currentEntryContent = currentEntry.getContent();
        List<TextLink>links = new ArrayList<>();

        for (EntryContent entry: main.getEntries()) {
            for (String label: entry.getLabels().split(", *"))
            {
                Pattern pattern = Pattern.compile("\\b("+label + "[sx]?)\\b");

                Matcher matcher = pattern.matcher(currentEntryContent);
                while (matcher.find())
                {
                    TextLink link = new TextLink(entry, matcher.start(), matcher.end());
                    links.add(link);
                    System.out.println(label + ": " + matcher.start());
                }
            }
        }
        links.sort(Comparator.comparingInt(TextLink::getStartIndex));

        int currentIndex =0;

        for (TextLink link :links) {
            if (currentIndex < link.getStartIndex()){
                String str = currentEntryContent.substring(currentIndex, link.getStartIndex());
                Text text = new Text(str);
                main.contentTextFlow.getChildren().add(text);
            }

            String linkStr = currentEntryContent.substring(link.getStartIndex(), link.getEndIndex());
            if (link.getEntry() != currentEntry) {
                Hyperlink hyperlink = new Hyperlink(linkStr);
                hyperlink.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(link.getEntry()));
                main.contentTextFlow.getChildren().add(hyperlink);
            }
            else{
                main.contentTextFlow.getChildren().add(new Text(linkStr));
            }
            currentIndex = link.getEndIndex();
        }
        Text endText = new Text(currentEntryContent.substring(currentIndex));
        main.contentTextFlow.getChildren().add(endText);
    }

    private void switchToEdit() {
        main.contentTextArea.requestFocus();
        main.contentTextArea.end();
    }
}