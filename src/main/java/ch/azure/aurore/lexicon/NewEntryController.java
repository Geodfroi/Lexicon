package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewEntryController implements Initializable {
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextField labelTextField;
    @FXML
    public VBox root;

    private final List<EntryContent> entries;
    private ChangeListener<String> labelEventListener;

    public NewEntryController(ObservableList<EntryContent> entries) {
        this.entries = entries;
    }

    //region methods
    public EntryContent createItem() {
        String content =contentTextArea.getText();
        String labels =labelTextField.getText();
        int id =  DataAccess.getInstance().createEntry(content,labels);
        return new EntryContent(id, content, labels);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelEventListener = (observableValue, s, t1) -> validate();
        labelTextField.textProperty().addListener(labelEventListener);
        labelTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
               if (!t1)
                   reorderLabels();
        });
    }

    private boolean matchExistingLabel(String label) {
        String regex ="^.*\\b" + label + "[sx]?\\b.*$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        for (EntryContent entry: entries) {

            Matcher matcher = pattern.matcher(entry.getLabels());
            if (matcher.matches())
                return true;
        }

        return false;
    }

    private void reorderLabels() {
        String reorderedLabels = EntryContent.reorderLabels(labelTextField.getText());
        if (!reorderedLabels.equals(labelTextField.getText())){
            labelTextField.textProperty().removeListener(labelEventListener);
            labelTextField.setText(reorderedLabels);
            labelTextField.textProperty().addListener(labelEventListener);
        }
    }

    private void validate() {
        String str = labelTextField.getText();
        boolean invalid = str == null || str.isBlank() || str.isBlank();
        if (!invalid)
        {
            String[] labels = labelTextField.getText().split(", *");
            for (String label:labels) {
                if (matchExistingLabel(label))
                {
                    invalid = true;
                    break;
                }
            }
        }

        DialogPane dialogPane = (DialogPane) root.getParent();
        dialogPane.lookupButton(ButtonType.OK).setDisable(invalid);
    }
    //endregion
}