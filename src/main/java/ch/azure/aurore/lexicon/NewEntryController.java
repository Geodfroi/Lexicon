package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexicon.data.DataAccess;
import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewEntryController {
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextField labelTextField;

    public EntryContent createItem() {
       // var list = EntryContent.labelsFromStr(labelTextField.getText());
        String content =contentTextArea.getText();
        String labels =labelTextField.getText();
        int id =  DataAccess.getInstance().createEntry(content,labels);
        return new EntryContent(id, content, labels);
    }
}
