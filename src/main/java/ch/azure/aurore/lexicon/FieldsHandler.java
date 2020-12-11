package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.collections.Sets;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import ch.azure.aurore.strings.Strings;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FieldsHandler {

    private final LinkHandler linkHandler;
    private final ContentHandler contentHandler;
    private final ImageHandler imageHandler;
    private final LabelHandler labelHandler;

    private EntryContent displayedEntry;
    private MainController main;

    public FieldsHandler(MainController main, TextField labelsTextField, TextArea linksTextArea,
                         TextArea contentTextArea, ImageView imageView) {
        this.main = main;
        contentHandler = new ContentHandler(this,contentTextArea);
        imageHandler = new ImageHandler(this, imageView);
        labelHandler = new LabelHandler(this, labelsTextField);
        linkHandler = new LinkHandler(this,linksTextArea);
    }

    //region accessors
    public EntryContent getDisplayEntry() {
        return displayedEntry;
    }

    public MainController getMain() {
        return main;
    }
    //endregion

    //region methods
    public void clearDisplay() {

        contentHandler.clearDisplay();
        imageHandler.clearDisplay();
        labelHandler.clearDisplay();
        linkHandler.clearDisplay();
    }

    public void displayEntry(EntryContent val) {
        if (displayedEntry != null){
            contentHandler.recordToEntry(displayedEntry);
            imageHandler.recordToEntry(displayedEntry);
            labelHandler.recordToEntry(displayedEntry);
            linkHandler.recordToEntry(displayedEntry);
            LexiconDatabase.getInstance().updateEntry(displayedEntry);
        }
        displayedEntry = val;

        contentHandler.displayEntry(val);
        imageHandler.displayEntry(val);
        labelHandler.displayEntry(val);
        linkHandler.displayEntry(val);
    }
    //endregion
}

class ImageHandler {

    public static final String DEFAULT_IMAGE_PATH = "images/imageIcon.png";
    public static final String COPY_IMAGE_PATH = "images/copyIcon.png";
    public static final String IMAGE_EXPORT_FOLDER_PATH = "export";
    private final FieldsHandler parent;
    private final ImageView imageView;

//    private final MenuItem extractImageMenu;
//    private final MenuItem clearImageMenu;
//
//    private final Image defaultImage;
//    private final Image copyIcon;

    private Image entryImage;
    private int entryImageID = -1;

    public ImageHandler(FieldsHandler parent, ImageView imageView) {
        this.parent = parent;
        this.imageView = imageView;
    }

    public void clearDisplay() {
    }

    public void recordToEntry(EntryContent val) {
        System.out.println("not impl: record image");
    }

    public void displayEntry(EntryContent val) {
        System.out.println("not implemented: display image");
        //         main.getImageHandler().displayImage();
//            main.getImageHandler().enableManipulateImageMenu(main.getCurrentEntry() .hasImage());
//
    }

//    public ImageHandler(MainController main) {
//
//        //region image
//        URL url = App.class.getResource(DEFAULT_IMAGE_PATH);
//        defaultImage = new Image(url.toString());
//        url = App.class.getResource(COPY_IMAGE_PATH);
//        copyIcon = new Image(url.toString());
//        main.imageView.setImage(defaultImage);
//
//        ContextMenu imageMenu = new ContextMenu();
//        main.imageStackPane.setOnMouseClicked(mouseEvent -> imageMenu.show(main.imageStackPane, Side.TOP, 40,40));
//
//        clearImageMenu = new MenuItem("Clear image");
//        clearImageMenu.setOnAction(actionEvent -> {
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//            alert.setHeaderText("Clear image ?");
//            Optional<ButtonType> result1 = alert.showAndWait();
//            if (result1.isPresent() && result1.get() == ButtonType.OK) {
//                main.getCurrentEntry().setImage(null);
//                main.imageView.setImage(defaultImage);
//            }
//            enableManipulateImageMenu(main.getCurrentEntry().hasImage());
//        });
//        imageMenu.getItems().add(clearImageMenu);
//        extractImageMenu = new MenuItem("Extract image");
//        extractImageMenu.setOnAction(actionEvent -> {
//            byte[] array = main.getCurrentEntry().getImage();
//            String exportPath = IMAGE_EXPORT_FOLDER_PATH + "/" + main.getCurrentEntry().getId() + ".png";
//            File file = Images.toFile(array, exportPath);
//            Disk.openFile(file.getParent());
//        });
//        imageMenu.getItems().add(extractImageMenu);
//
//        //endregion
//
//        //region drag events
//        main.imageStackPane.setOnDragOver(event -> {
//            if (event.getGestureSource() != main.imageStackPane &&
//                    event.getDragboard().hasFiles() &&
//                    main.getCurrentEntry() != null) {
//                event.acceptTransferModes(TransferMode.COPY);
//            }
//
//            event.consume();
//        });
//        main.imageStackPane.setOnDragEntered(event -> {
//            if (event.getGestureSource() != main.imageStackPane &&
//                    event.getDragboard().hasFiles()) {
//                main.imageView.setImage(copyIcon);
//            }
//            event.consume();
//        });
//        main.imageStackPane.setOnDragExited(event -> {
//            displayImage();
//            event.consume();
//        });
//        main.imageStackPane.setOnDragDropped(event -> {
//            Dragboard db = event.getDragboard();
//            boolean success = false;
//            if (db.hasFiles()) {
//                List<File> files = db.getFiles();
//                if (files.size() >0){
//                    System.out.println(files.get(0).toString());
//
//                    Optional<byte[]> imgArray = Images.toByteArray(files.get(0));
//                    imgArray.ifPresent(bytes -> main.getCurrentEntry().setImage(bytes));
//
//                }
//                success = true;
//            }
//            event.setDropCompleted(success);
//            event.consume();
//        });
//        //endregion
//    }
//
//    void displayImage() {
//        if (main.getCurrentEntry().hasImage()){
//            if (entryImageID != main.getCurrentEntry().getId()) {
//                entryImageID = main.getCurrentEntry().getId();
//                ByteArrayInputStream inputStream = new ByteArrayInputStream(main.getCurrentEntry().getImage());
//                entryImage = new Image(inputStream);
//            }
//            main.imageView.setImage(entryImage);
//        }
//        else{
//            setDefaultImage();
//        }
//    }
//
//    public void enableManipulateImageMenu(boolean hasImage) {
//        extractImageMenu.setDisable(!hasImage);
//        clearImageMenu.setDisable(!hasImage);
//    }
//
//    public void setDefaultImage() {
//        main.imageView.setImage(defaultImage);
//    }
//
//    public void clear() {
//    }
}

class LabelHandler {
    private final FieldsHandler parent;
    private final TextField textField;
    private Set<String> labels = new HashSet<>();
    private String labelStr = "";
    private boolean isModified;

    public LabelHandler(FieldsHandler parent, TextField textField) {
        this.parent = parent;
        this.textField = textField;

        textField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                textField.setText(validateText());

            //// disable nav while navigating
            //main.getMenuHandler().setAllowNavigation(!t1);
        });
    }

    public void clearDisplay() {
        textField.clear();
        labelStr = "";
        labels = new HashSet<>();
        //System.out.println("not implemented: clear labels");
    }

    public void displayEntry(EntryContent val) {
        labels = val.getLabels();
        labelStr = val.getLabelStr();
        textField.setText(labelStr);
        isModified = false;

        //System.out.println("not implemented: display labels");
    }

    public static Pattern getSearchPattern(String str){
        return Pattern.compile("^.*\\b"+ str + "[sx]?\\b.*$");
    }

    public void recordToEntry(EntryContent val) {
        if (isModified){
            val.setLabels(labels);
        }
        //System.out.println("not implemented: record label");
    }

    private String validateText() {
        EntryContent current = parent.getDisplayEntry();
        ObservableList<EntryContent> entries = parent.getMain().getDatabaseAccess().getEntries();
        if (current == null || entries.size() == 0){
            return "";
        }

        String txt = textField.getText();
        if (Strings.isNullOrEmpty(txt)){
            return labelStr;
        }

        Map<String, Pattern> patterns = Arrays.stream(txt.split(", *")).
                map(Strings::camel).
                collect(Collectors.toMap(s -> s, LabelHandler::getSearchPattern));

        Set<String> validLabels = new HashSet<>();
        for (String s : patterns.keySet()) {
            boolean valid = true;

            for (EntryContent e : entries) {
                if (e == current)
                    continue;

                Matcher m = patterns.get(s).matcher(e.getLabelStr());
                if (m.matches())
                    valid = false;
            }
            if (valid)
                validLabels.add(s);
        }
        if (!Sets.equals(validLabels, labels)){
            labelStr = EntryContent.toLabelStr(validLabels);
            labels = validLabels;
            isModified = true;
        }
        return labelStr;
    }
}

class LinkHandler {
    private final FieldsHandler parent;
    private final TextArea textArea;

    public LinkHandler(FieldsHandler parent, TextArea textArea) {
        this.parent = parent;
        this.textArea = textArea;
    }

    public void recordToEntry(EntryContent val) {
        System.out.println("not implemented: record links");
    }

    public void displayEntry(EntryContent val) {
        System.out.println("not implemented : display links");
        //  main.getLinksHandler().setTextFlow(main.getCurrentEntry());
    }

    public void clearDisplay() {
        System.out.println("not implemented : clear links");
    }

//    public LinkHandler(MainController mainController) {

//        main.linksTextFlow.setOnMouseClicked(this::switchToEdit);
//        main.linksTextArea.focusedProperty().
//                addListener((observableValue, aBoolean, t1) -> textFieldFocus(t1));
//    }
//
//    private Hyperlink createLink(EntryContent entry) {
//        String label = entry.getFirstLabel() + ", ";
//        Hyperlink link = new Hyperlink(label);
//        link.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(entry));
//        return link;
//    }
//
//    private void switchToEdit(MouseEvent event) {
//        main.linksTextArea.requestFocus();
//        main.linksTextArea.end();
//
//        event.consume();
//    }
//
//    public void setTextFlow(EntryContent current) {
//        main.linksTextFlow.getChildren().clear();
//        if (currentEntry == null) {
//            return;
//        }
//
//        List<Hyperlink> toSort = new ArrayList<>();
//        for (int i : LexiconDatabase.getInstance().queryEntryLinks(current.getId())) {
//            Optional<EntryContent> entry = LexiconDatabase.getInstance().queryEntry(i);
//            if (entry.isPresent()){
//                Hyperlink hyperlink = createLink(entry.get());
//                toSort.add(hyperlink);
//            }
//        }
//        toSort.sort((o1, o2) -> o1.getText().compareToIgnoreCase(o2.getText()));
//        toSort.forEach(hyperlink -> main.linksTextFlow.getChildren().add(hyperlink));
//    }
//
//    private void textFieldFocus(Boolean hasFocus) {
//        main.getMenuHandler().setAllowNavigation(!hasFocus);
//        if (hasFocus){
//            currentEntry = main.getCurrentEntry();
//            Stream<String> st = LexiconDatabase.getInstance().
//                    queryEntryLinks(currentEntry.getId()).stream().
//                    map(main::getByID).
//                    map(EntryContent::getFirstLabel);
//
//            String linksLabel = Strings.toString(st, ", ");
//
//            main.linksTextArea.setText(linksLabel);
//            main.linksTextFlow.getChildren().clear();
//        }else{
//            updateLinks(main.linksTextArea.getText());
//            main.linksTextArea.clear();
//            setTextFlow(currentEntry);
//        }
//    }
//
//    private void updateLinks(String linkStr) {
//
//        List<Pattern> patterns = Arrays.stream(linkStr.split(", *")).
//                map(s -> LabelHandler.getSearchPattern(Strings.camel(s))).
//                collect(Collectors.toList());
//
//        Set<EntryContent> newLinks = main.getEntries().stream().
//                filter(e -> {
//                    for (Pattern pattern:patterns) {
//
//                        Matcher matcher = pattern.matcher(e.getLabelStr());
//                        if (matcher.matches())
//                            return true;
//                    }
//                    return false;
//                }).collect(Collectors.toSet());
//        Set<Integer> newLinkIds = newLinks.stream().
//                map(EntryContent::getId).
//                collect(Collectors.toSet());
//
//        Set<Integer> oldLinkIds = LexiconDatabase.getInstance().
//                queryEntryLinks(currentEntry.getId());
//
//        Set<Integer> toRemove = new HashSet<>(oldLinkIds);
//        toRemove.removeAll(newLinkIds);
//
//        Set<Integer> toRecord = new HashSet<>(newLinkIds);
//        toRecord.removeAll(oldLinkIds);
//
//        if (toRecord.size()>5) {
//            System.out.println(toRecord.size());
//        }
//        for (int id:toRemove) {
//            LexiconDatabase.getInstance().removeLink(id, currentEntry.getId());
//        }
//        for (int id:toRecord){
//            LexiconDatabase.getInstance().insertLink(id, currentEntry.getId());
//        }
//    }
}

class ContentHandler {
    private final FieldsHandler parent;
    private final TextArea textArea;

    public ContentHandler(FieldsHandler parent, TextArea textArea) {
        this.parent = parent;
        this.textArea = textArea;
    }

    public void recordToEntry(EntryContent val) {
        System.out.println("not implemented: record content");
    }

    public void displayEntry(EntryContent val) {
        // main.getTextLoader().setTextFlow();
        System.out.println("not implemented: display content");
    }

    public void clearDisplay() {
        System.out.println("not implemented: clear content");
    }


//    public TextLoader(MainController main) {
//        this.main = main;
//
//        main.textFlow_scrollPane.setOnMouseClicked(mouseEvent -> {
//            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
//                switchToEdit();
//                mouseEvent.consume();
//            }
//        });
//        main.contentTextArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> textAreaFocus(t1));
//    }
//
//    private void textAreaFocus(Boolean hasFocus) {
//        if (hasFocus){
//            main.contentTextArea.setText(main.getCurrentEntry().getContent());
//           // main.contentTextFlow.getChildren().clear();
//            main.textFlow_scrollPane.setVisible(false);
//        }else{
//            main.getCurrentEntry().setContent(main.contentTextArea.getText());
//            main.contentTextArea.clear();
//            main.textFlow_scrollPane.setVisible(true);
//
//            setTextFlow();
//        }
//    }
//
//    public void setTextFlow() {
//
//        EntryContent currentEntry = main.getCurrentEntry();
//
//        main.contentTextFlow.getChildren().clear();
//        if (currentEntry == null || !currentEntry.hasContent()) {
//            return;
//        }
//        String currentEntryContent = currentEntry.getContent();
//        List<TextLink>links = new ArrayList<>();
//
//        for (EntryContent entry: main.getEntries()) {
//            for (String label: entry.getLabels())
//            {
//                Pattern pattern = Pattern.compile("\\b("+label + "[sx]?)\\b");
//
//                Matcher matcher = pattern.matcher(currentEntryContent);
//                while (matcher.find())
//                {
//                    TextLink link = new TextLink(entry, matcher.start(), matcher.end());
//                    links.add(link);
//                }
//            }
//        }
//        links.sort(Comparator.comparingInt(TextLink::getStartIndex));
//
//        int currentIndex =0;
//
//        for (TextLink link :links) {
//            if (currentIndex < link.getStartIndex()){
//                String str = currentEntryContent.substring(currentIndex, link.getStartIndex());
//                Text text = new Text(str);
//                main.contentTextFlow.getChildren().add(text);
//            }
//
//            String linkStr = currentEntryContent.substring(link.getStartIndex(), link.getEndIndex());
//            if (link.getEntry() != currentEntry) {
//                Hyperlink hyperlink = new Hyperlink(linkStr);
//                hyperlink.setOnAction(actionEvent -> main.entriesListView.getSelectionModel().select(link.getEntry()));
//                main.contentTextFlow.getChildren().add(hyperlink);
//            }
//            else{
//                main.contentTextFlow.getChildren().add(new Text(linkStr));
//            }
//            currentIndex = link.getEndIndex();
//        }
//        Text endText = new Text(currentEntryContent.substring(currentIndex));
//        main.contentTextFlow.getChildren().add(endText);
//    }
//
//    private void switchToEdit() {
//        main.contentTextArea.requestFocus();
//        main.contentTextArea.end();
//    }
}

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
        return entry.getFirstLabel() + "@"+ startIndex;
    }
}