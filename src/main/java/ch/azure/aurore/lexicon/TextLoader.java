package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexicon.data.EntryContent;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextLoader {

    private final TextFlow textFlow;
    private final TextArea textArea;
    private EntryContent entry;
    ChangeListener<String> textListener;

    public TextLoader(TextArea textArea, TextFlow textFlow) {
        this.textArea = textArea;
        this.textFlow = textFlow;

        textFlow.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
                switchToEdit();
            }
        });
        textListener = (observableValue, s, t1) -> entry.setContent(t1);
        textArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                lostFocus();
        });
    }

    private void lostFocus() {
        System.out.println("lost focus");
        entry.save();
        setTextFlow();
    }

    private void switchToEdit() {

        textFlow.getChildren().clear();
        textFlow.setFocusTraversable(false);
        textFlow.setMouseTransparent(true);

        textArea.setText(entry.getContent());
        textArea.textProperty().addListener(textListener);
        textArea.requestFocus();
        textArea.end();
    }

    public void loadDisplayText(EntryContent entry) {
        if (this.entry != null)
            this.entry.save();
        this.entry = entry;

        setTextFlow();
    }

    private void setTextFlow() {

        textArea.textProperty().removeListener(textListener);
        textArea.setText("");

        textFlow.setFocusTraversable(true);
        textFlow.setMouseTransparent(false);

        textFlow.getChildren().clear();
        textFlow.getChildren().add(new Text(entry.getContent()));
    }
}

//    public void loadDisplayText(String content) {
///*        Paint paint = Color.BLUE;
//        BackgroundFill fill = new BackgroundFill(paint,null,null);
//        Background background = new Background(fill);
//        textFlow.setBackground(background);*/
//
//        textVbox.getChildren().add(textFlow);
//        loaded = true;
//    }

//    public static void CreateText(TextFlow textFlow, String content) {
//
//        textFlow.getChildren().clear();
//        textFlow.getChildren().add(new Text(content));
//  /*      for (String str: content.split(" ")) {
//            Hyperlink link = new Hyperlink(str);
//            Text text = new Text(str);
//            textFlow.getChildren().add(text);
//        }*/