package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.images.API.Images;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainController implements Initializable {

    //region constants

    private static final String CURRENT_ENTRIES = "currentEntries";
    public static final String FILE_CURRENT_PROPERTY = "currentFile";
    public static final String FILES_LIST_PROPERTY = "filesList";
    private static final String SHOW_EMPTY_PROPERTY = "showEmptyEntries";

    //endregion

    //region fields

    //region FXML fields

    @FXML
    public BorderPane root;

    @FXML
    public Menu fileMenu;
    @FXML
    public MenuItem lastMenuItem;
    @FXML
    public MenuItem nextMenuItem;
    @FXML
    public ImageView imageView;
    @FXML
    public StackPane imageStackPane;
    @FXML
    ListView<EntryContent> entriesListView;
    @FXML
    public TextArea contentTextArea;
    @FXML
    public TextFlow contentTextFlow;
    @FXML
    public TextField labelsTextField;
    @FXML
    public TextField linksTextField;
    @FXML
    public CheckMenuItem showEmptyCheckMenu;
    @FXML
    public TextField searchTextField;
    @FXML
    public TextFlow linksTextFlow;

    //endregion

    private TextLoader textLoader;
    private LinkHandler linkHandler;
    private String filterStr = "";

    private String currentDatabase;
    private EntryContent currentEntry;
    private ObservableList<EntryContent> entries;

    private MenuItem selectDatabaseMenu;
    private MenuItem clearDataMenu;
    private MenuItem closeMenu;

    private final NavStack<EntryContent> navStack = new NavStack<>();
    private Image defaultImage;
    private Image copyIcon;
    private Image entryImage;

    //endregion

    //region getters

    EntryContent getCurrentEntry() {
        return currentEntry;
    }

    List<EntryContent> getEntries() {
        return entries;
    }

    //endregion

    //region methods

    int count =0;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textLoader = new TextLoader(this);
        linkHandler = new LinkHandler(this);

        ChangeListener<EntryContent> listViewListener = (observableValue, oldValue, newValue) -> entrySelected(newValue);
        entriesListView.getSelectionModel().selectedItemProperty().addListener(listViewListener);

        //entries context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> deleteEntry());
        menu.getItems().add(delete);

        entriesListView.setContextMenu(menu);

        // show empty entries menu
        Optional<Boolean> result = LocalSave.getBoolean(SHOW_EMPTY_PROPERTY);
        result.ifPresent(aBoolean -> showEmptyCheckMenu.setSelected(aBoolean));

        // search box
        searchTextField.textProperty().addListener((observableValue, s, t1) -> {
            filterStr = t1;
            showEntriesList();
        });

        // file menus
        selectDatabaseMenu = new MenuItem("Select Database");
        selectDatabaseMenu.setOnAction(actionEvent -> openDiskDatabase());
        clearDataMenu = new MenuItem("Reset application");
        clearDataMenu.setOnAction(this::clearData);
        closeMenu = new MenuItem("Close application");
        closeMenu.setOnAction(actionEvent -> Platform.exit());

        // navigation menus
        lastMenuItem.setOnAction(actionEvent -> navStack(Direction.backward));
        nextMenuItem.setOnAction(actionEvent -> navStack(Direction.forward));

        //default image
        // URL url = App.class.getResource("images/imageIcon.png");
        URL url = App.class.getResource("images/wf.png");
        defaultImage = new Image(url.toString());
        url = App.class.getResource("images/copyIcon.png");
        copyIcon = new Image(url.toString());
        imageView.setImage(defaultImage);

        //drag events
        imageStackPane.setOnDragOver(event -> {
            if (event.getGestureSource() != imageStackPane &&
                    event.getDragboard().hasFiles() &&
                    currentEntry != null) {
                event.acceptTransferModes(TransferMode.COPY);
            }

            event.consume();
        });

        imageStackPane.setOnDragEntered(event -> {
            if (event.getGestureSource() != imageStackPane &&
                    event.getDragboard().hasFiles()) {

                imageView.setImage(copyIcon);
            }
            event.consume();
        });
        imageStackPane.setOnDragExited(event -> {

            displayImage();

            event.consume();
        });
        imageStackPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (files.size() >0){
                    System.out.println(files.get(0).toString());

                    Optional<byte[]> imgArray = Images.toByteArray(files.get(0));
                    if (imgArray.isPresent()){
                        currentEntry.setImage(imgArray.get());
                        LexiconDatabase.getInstance().updateEntry(currentEntry);
                    }

//                    var file = Images.getFile(res.get());
//                    var uri = file.toURI();
//                    var img = new Image(uri.toString());
//                   defaultImage = img;
//                    imageView.setImage(img);

//                    var res = Images.toByteArray(files.get(0));
//                    if (res.isPresent()){
//                        var fxImg = Images.toFXImage(res.get());
//                        if (fxImg.isPresent()){
//
//                            BufferedImage bImage = SwingFXUtils.fromFXImage(fxImg.get(), null);
//                            try {
//                                File outputFile = new File("testOutput.png");
//                                ImageIO.write(bImage, "png", outputFile);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//
////                            imageIcon = fxImg.get();
////                            imageView.setImage(imageIcon);
//                        }
//                    }

                    //Image image = SwingFXUtils.toFXImage(img, null);
//                        FileInputStream fi = new FileInputStream(files.get(0));
//                        imageIcon = new Image(fi);
//                        imageView.setImage(imageIcon);

//
//                        ByteArrayOutputStream baos= new ByteArrayOutputStream(1000);
//                        BufferedImage img=ImageIO.read(files.get(0));
//                        ImageIO.write(img, "png", baos);
//                        baos.flush();
//
//                        String base64String= Base64.getEncoder().encodeToString(baos.toByteArray());
//                        baos.close();
//
//                        byte[] bytearray = Base64.getDecoder().decode(base64String);
//
//                        BufferedImage imag=ImageIO.read(new ByteArrayInputStream(bytearray));
//                        imageIcon = SwingFXUtils.toFXImage(imag, null);
//                        imageView.setImage(imageIcon);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void displayImage() {
        if (currentEntry.hasImage()){
            ByteArrayInputStream inputStream = new ByteArrayInputStream(currentEntry.getImage());
            entryImage = new Image(inputStream);
            imageView.setImage(entryImage);
        }
        else{
            imageView.setImage(defaultImage);
        }
    }

    private void clearData(ActionEvent actionEvent) {
        LexiconDatabase.getInstance().close();
        LocalSave.clear();

        currentDatabase = null;
        currentEntry = null;

        reloadFileMenu();

        entries.clear();

        linksTextFlow.getChildren().clear();
        contentTextFlow.getChildren().clear();
        labelsTextField.clear();
        imageView.setImage(defaultImage);
    }

    @FXML
    public void createEntry()  {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(root.getScene().getWindow());
        dialog.setTitle("Create Entry");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NewEntry.fxml"));
        NewEntryController dialogController = new NewEntryController(entries);
        fxmlLoader.setController(dialogController);

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create create dialog");
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            EntryContent item = dialogController.createItem();
            entries.add(item);
            System.out.println("adding to list");
        }
    }

    @FXML
    private void deleteEntry() {
        EntryContent item = entriesListView.getSelectionModel().getSelectedItem();
        if (item == null){
            System.out.println("no selection for delete");
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete selected entry ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                if (LexiconDatabase.getInstance().removeEntry(item)){
                    entries.remove(item);
                }
            }
        }
    }

    private void entrySelected(EntryContent value) {
        if (value != null){
            if (this.currentEntry != null)
                this.currentEntry.save();
            this.currentEntry = value;

            textLoader.setTextFlow();
            linkHandler.setTextFlow();
            labelsTextField.setText(value.getLabels());
            displayImage();

            LocalSave.setMapValue(CURRENT_ENTRIES, currentDatabase, value.getId());
            navStack.add(value);

            lastMenuItem.setDisable(!navStack.hasFormer());
            nextMenuItem.setDisable(!navStack.hasNext());
        }
    }

    private void navStack(Direction dir) {
        EntryContent entry = navStack.navigateStack(dir);
        entriesListView.getSelectionModel().select(entry);
    }

    public void openDiskDatabase() {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Select Database");
        File file = dialog.showOpenDialog(root.getScene().getWindow());

        if (file != null){
            String databasePath = file.getAbsolutePath();
            if (LexiconDatabase.getInstance().open(databasePath)){
                selectDatabase(file.getName());
                LocalSave.setMapValue(FILES_LIST_PROPERTY, currentDatabase, databasePath);
            }
            else
                reloadFileMenu();
        }
    }

    public void reloadEntries() {

        Optional<String> query = LocalSave.getStr(FILE_CURRENT_PROPERTY);
        if (query.isPresent()) {
            this.currentDatabase = query.get();
            Optional<String> pathResult = LocalSave.getMapString(FILES_LIST_PROPERTY, currentDatabase);
            if (pathResult.isPresent() && LexiconDatabase.getInstance().open(pathResult.get())){

                List<EntryContent> list = LexiconDatabase.getInstance().queryEntries();
                entries = FXCollections.observableList(list);
                showEntriesList();
            }
        }
        reloadFileMenu();
    }

    private void reloadFileMenu() {
        fileMenu.getItems().clear();
        fileMenu.getItems().add(selectDatabaseMenu);
        fileMenu.getItems().add(clearDataMenu);

        Set<String> set = LocalSave.getMapValues(FILES_LIST_PROPERTY).keySet();

        if (set.size() > 0 && currentDatabase != null)
        {
            fileMenu.getItems().add(new SeparatorMenuItem());

            List<CheckMenuItem> list = set.stream().
                    sorted(String::compareToIgnoreCase).
                    map(CheckMenuItem::new).
                    collect(Collectors.toList());

            list.forEach(checkMenuItem -> {
                if (currentDatabase.equals(checkMenuItem.getText()))
                    checkMenuItem.setSelected(true);

                checkMenuItem.setOnAction(actionEvent -> {
                    for (CheckMenuItem menuItem: list) {
                        menuItem.setSelected(menuItem == checkMenuItem);
                    }
                    if (!currentDatabase.equals(checkMenuItem.getText()))
                        selectDatabase(checkMenuItem.getText());
                });
                fileMenu.getItems().add(checkMenuItem);
            });

            fileMenu.getItems().add(new SeparatorMenuItem());
        }

        fileMenu.getItems().add(closeMenu);
    }

    private void selectDatabase(String name) {
        currentDatabase = name;
        LocalSave.set(FILE_CURRENT_PROPERTY, currentDatabase);
        navStack.clear();
        reloadEntries();
    }

    public void showEntriesList() {

        FilteredList<EntryContent> filteredList = new FilteredList<>(entries, entryContent -> {
            if (!showEmptyCheckMenu.isSelected() && entryContent.hasContent()) {
                return false;
            }
            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            Matcher matcher = pattern.matcher(entryContent.getLabels());
            return matcher.matches();
        });
        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getLabels().compareToIgnoreCase(right.getLabels()));
        entriesListView.setItems(sortedList);
        if (currentDatabase != null){
            Optional<Integer> currentID = LocalSave.getMapInteger(CURRENT_ENTRIES, currentDatabase);
            if (currentID.isPresent())
            {
                Optional<EntryContent> result =  sortedList.stream().
                        filter(e -> e.getId() == currentID.get()).findAny();

                result.ifPresent(e -> entriesListView.getSelectionModel().select(e));
            }
        }
    }

    @FXML
    public void showEmpty() {
        LocalSave.set(SHOW_EMPTY_PROPERTY, showEmptyCheckMenu.isSelected());
        showEntriesList();
    }
    //endregion
}