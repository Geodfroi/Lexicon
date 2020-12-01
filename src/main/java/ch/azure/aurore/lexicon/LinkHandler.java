package ch.azure.aurore.lexicon;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class LinkHandler {
    
    private final MainController main;

    public LinkHandler(MainController mainController) {
        this.main = mainController;

        mainController.linksTextFlow.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                switchToEdit();
            }
        });
    }

    private void switchToEdit() {
        main.linksTextFlow.getChildren().clear();
        main.linksTextFlow.setFocusTraversable(false);
        main.linksTextFlow.setMouseTransparent(true);

//        main.linksTextField.setText(main.getCurrentEntry().getContent());
//        main.contentTextArea.textProperty().addListener(textListener);
//        main.contentTextArea.requestFocus();
//        main.contentTextArea.end();
    }

    public void setTextFlow() {
        var currentEntry = main.getCurrentEntry();
    }
}
