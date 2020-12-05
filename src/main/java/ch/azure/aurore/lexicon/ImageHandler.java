package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.Disk;
import ch.azure.aurore.images.API.Images;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;

public class ImageHandler {

    public static final String DEFAULT_IMAGE_PATH = "images/imageIcon.png";
    public static final String COPY_IMAGE_PATH = "images/copyIcon.png";
    public static final String IMAGE_EXPORT_FOLDER_PATH = "export";

    private final MainController main;

    private MenuItem extractImageMenu;
    private MenuItem clearImageMenu;

    private Image defaultImage;
    private Image copyIcon;
    private Image entryImage;
    private int entryImageID = -1;

    public ImageHandler(MainController main) {
        this.main = main;

        //region image
        URL url = App.class.getResource(DEFAULT_IMAGE_PATH);
        defaultImage = new Image(url.toString());
        url = App.class.getResource(COPY_IMAGE_PATH);
        copyIcon = new Image(url.toString());
        main.imageView.setImage(defaultImage);

        ContextMenu imageMenu = new ContextMenu();
        main.imageStackPane.setOnMouseClicked(mouseEvent -> imageMenu.show(main.imageStackPane, Side.TOP, 40,40));

        clearImageMenu = new MenuItem("Clear image");
        clearImageMenu.setOnAction(actionEvent -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Clear image ?");
            Optional<ButtonType> result1 = alert.showAndWait();
            if (result1.isPresent() && result1.get() == ButtonType.OK) {
                main.getCurrentEntry().setImage(null);
             //   if (LexiconDatabase.getInstance().updateEntry(main.getCurrentEntry())){
                main.imageView.setImage(defaultImage);
            //    }
            }
            enableManipulateImageMenu(main.getCurrentEntry().hasImage());
        });
        imageMenu.getItems().add(clearImageMenu);
        extractImageMenu = new MenuItem("Extract image");
        extractImageMenu.setOnAction(actionEvent -> {
            byte[] array = main.getCurrentEntry().getImage();
            String exportPath = IMAGE_EXPORT_FOLDER_PATH + "/" + main.getCurrentEntry().getId() + ".png";
            File file = Images.toFile(array, exportPath);
            Disk.openFile(file.getParent());
        });
        imageMenu.getItems().add(extractImageMenu);

        //endregion

        //region drag events
        main.imageStackPane.setOnDragOver(event -> {
            if (event.getGestureSource() != main.imageStackPane &&
                    event.getDragboard().hasFiles() &&
                    main.getCurrentEntry() != null) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });
        main.imageStackPane.setOnDragEntered(event -> {
            if (event.getGestureSource() != main.imageStackPane &&
                    event.getDragboard().hasFiles()) {

                main.imageView.setImage(copyIcon);
            }
            event.consume();
        });
        main.imageStackPane.setOnDragExited(event -> {
            displayImage();
            event.consume();
        });
        main.imageStackPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (files.size() >0){
                    System.out.println(files.get(0).toString());

                    Optional<byte[]> imgArray = Images.toByteArray(files.get(0));
                    // LexiconDatabase.getInstance().updateEntry(main.getCurrentEntry());
                    imgArray.ifPresent(bytes -> main.getCurrentEntry().setImage(bytes));

                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
        //endregion
    }

    void displayImage() {
        if (main.getCurrentEntry().hasImage()){
            if (entryImageID != main.getCurrentEntry().getId()) {
                entryImageID = main.getCurrentEntry().getId();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(main.getCurrentEntry().getImage());
                entryImage = new Image(inputStream);
            }
            main.imageView.setImage(entryImage);
        }
        else{
            setDefaultImage();
        }
    }

    public void enableManipulateImageMenu(boolean hasImage) {
        extractImageMenu.setDisable(!hasImage);
        clearImageMenu.setDisable(!hasImage);
    }

    public void setDefaultImage() {
        main.imageView.setImage(defaultImage);
    }
}
