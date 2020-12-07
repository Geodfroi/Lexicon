package ch.azure.aurore.lexicon;

import ch.azure.aurore.Strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEntryController implements Initializable {
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextField labelTextField;
    @FXML
    public VBox root;

    private ChangeListener<String> labelEventListener;

    private Set<String> labels;
    private final List<EntryContent> entries;

    public NewEntryController(MainController main) {
        entries = main.getEntries();
    }

    //region methods
    public Optional<EntryContent> createItem() {
        String content = contentTextArea.getText();
        return LexiconDatabase.getInstance().insertEntry(content, this.labels);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelEventListener = (observableValue, s, t1) -> validate();
        labelTextField.textProperty().addListener(labelEventListener);
        labelTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                setLabels();
        });
    }

    private boolean matchExistingLabel(String label) {
        if (Strings.isNullOrEmpty(label))
            return true;

        String regex ="^.*\\b" + label + "?\\b.*$";
        System.out.flush();
        System.out.println(regex);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        for (EntryContent entry: entries) {
            Matcher matcher = pattern.matcher(entry.getLabelStr());
            if (matcher.matches()){
                System.out.println("matches");
                return true;
            }
        }

        return false;
    }

    private void setLabels() {
        labelTextField.textProperty().removeListener(labelEventListener);
        labelTextField.setText(labelTextField.getText());
        labelTextField.textProperty().addListener(labelEventListener);
    }

    private void validate() {
        labels = new HashSet<>();
        String str = labelTextField.getText();
        boolean invalid = str == null || str.isBlank() || str.isBlank();
        if (!invalid) {
            String[] labels = labelTextField.getText().split(", *");
            for (String label:labels) {
                if (matchExistingLabel(label))
                {
                    invalid = true;
                    break;
                }
                this.labels.add(label);
            }
        }

        DialogPane dialogPane = (DialogPane) root.getParent();
        dialogPane.lookupButton(ButtonType.OK).setDisable(invalid);
    }
    //endregion
}