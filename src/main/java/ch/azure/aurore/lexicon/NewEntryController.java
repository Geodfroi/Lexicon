package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import ch.azure.aurore.strings.Strings;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEntryController implements Initializable {

    private final MainController main;
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextField labelTextField;
    @FXML
    public VBox root;

    private final String labelStr;
    private final ObservableList<EntryContent> entries;
    private Set<String> labels = new HashSet<>();

    public NewEntryController(MainController main, String label) {
        this.main = main;
        this.entries = main.getDatabaseAccess().getEntries();
        this.labelStr = label;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelTextField.setText(labelStr);

        //labelTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> focusChanged(t1));
        //            switchTextEvent(false);
        //            labelTextField.setText(labelStr);
        //            switchTextEvent(true);
        ChangeListener<String> textChanged = (observableValue, s, t1) -> {
            validate();
//            switchTextEvent(false);
//            labelTextField.setText(labelStr);
//            switchTextEvent(true);
        };
        labelTextField.textProperty().addListener(textChanged);
      //  switchTextEvent(true);
    }

//    private void switchTextEvent(boolean val) {
//        if (val) labelTextField.textProperty().addListener(textChanged);
//        else labelTextField.textProperty().removeListener(textChanged);
//    }

    private void validate() {
        String str = labelTextField.getText();
        labels = new HashSet<>();
        if (Strings.isNullOrEmpty(str)){
            enableOK(false);
        }

        if (str.contains(" ")){
            enableOK(false);
            return;
        }

        for (var label:str.split(", *")) {
            if (matchExistingLabel(label)){
                enableOK(false);
                return;
            }
            labels.add(str);
        }
        enableOK(true);
       // labelStr = Strings.toString(labels, String::compareToIgnoreCase);
    }

    private boolean matchExistingLabel(String label) {

        if (Strings.isNullOrEmpty(label))
            return true;

        String regex ="^.*\\b" + label + "?\\b.*$";
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

    private void enableOK(boolean val) {
        DialogPane dialogPane = (DialogPane) root.getParent();
        dialogPane.lookupButton(ButtonType.OK).setDisable(!val);

        if (val){
            labelTextField.setStyle("-fx-text-fill: black");
        }else {
            labelTextField.setStyle("-fx-text-fill: red");
        }
    }

    public void createItem() {
        String content = Strings.isNullOrEmpty(contentTextArea.getText()) ? "" : contentTextArea.getText();
        Optional<Integer> id = LexiconDatabase.getInstance().insertEntry(labels, content);
        if (id.isPresent()){
            var entry = new EntryContent(id.get(), labels, content);
            entries.add(entry);
            main.getNavigation().selectEntry(entry, true);
        }
    }
}

//    //region methods
//    public Optional<EntryContent> createItem() {
//        String content = contentTextArea.getText();
//        return LexiconDatabase.getInstance().insertEntry(content, this.labels);
//    }

