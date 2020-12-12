package ch.azure.aurore.lexicon;

import ch.azure.aurore.collections.Sets;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import ch.azure.aurore.strings.Strings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FieldsHandler {

    private final LinkHandler linkHandler;
    private final ContentHandler contentHandler;
    private final ImageHandler imageHandler;
    private final LabelHandler labelHandler;

    private EntryContent displayedEntry;
    private MainController main;

    public FieldsHandler(MainController main) {
        this.main = main;
        contentHandler = new ContentHandler(this);
        imageHandler = new ImageHandler(this);
        labelHandler = new LabelHandler(this);
        linkHandler = new LinkHandler(this);

        main.root.setOnMouseClicked(mouseEvent -> clearFocus());
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
        displayedEntry = null;
        contentHandler.clearDisplay();
        imageHandler.clearDisplay();
        labelHandler.clearDisplay();
        linkHandler.clearDisplay();
    }

    private void clearFocus() {
        main.root.requestFocus();
    }

    public void displayEntry(EntryContent val) {

        recordDisplay();
        clearFocus();

        clearDisplay();
        displayedEntry = val;

        contentHandler.displayEntry(val);
        imageHandler.displayEntry(val);
        labelHandler.displayEntry(val);
        linkHandler.displayEntry(val);
    }

    public void recordDisplay() {
        if (displayedEntry != null){
            contentHandler.recordToEntry(displayedEntry);
            imageHandler.recordToEntry(displayedEntry);
            labelHandler.recordToEntry(displayedEntry);
            linkHandler.recordToEntry(displayedEntry);
            LexiconDatabase.getInstance().updateEntry(displayedEntry);
        }
    }
    //endregion
}

class ImageHandler {

//    public static final String DEFAULT_IMAGE_PATH = "images/imageIcon.png";
//    public static final String COPY_IMAGE_PATH = "images/copyIcon.png";
//    public static final String IMAGE_EXPORT_FOLDER_PATH = "export";
//    private final MenuItem extractImageMenu;
//    private final MenuItem clearImageMenu;

//    private final Image defaultImage;
//    private final Image copyIcon;
//
//    private Image entryImage;
//    private int entryImageID = -1;

    private final FieldsHandler parent;

    public ImageHandler(FieldsHandler parent) {
        this.parent = parent;
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
}

class LabelHandler {
    private final FieldsHandler parent;
    private Set<String> labels = new HashSet<>();
    private String labelStr = "";
    private boolean isModified;

    public LabelHandler(FieldsHandler parent) {
        this.parent = parent;

        parent.getMain().labelsTextField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1)
                parent.getMain().labelsTextField.setText(validateText());

            // disable nav while navigating
            //main.getMenuHandler().setAllowNavigation(!t1);
        });
    }

    public void clearDisplay() {
        parent.getMain().labelsTextField.clear();
        labelStr = "";
        labels = new HashSet<>();
        isModified = false;
        //System.out.println("not implemented: clear labels");
    }

    public void displayEntry(EntryContent val) {
        labels = val.getLabels();
        labelStr = val.getLabelStr();
        parent.getMain().labelsTextField.setText(labelStr);

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

        String txt = parent.getMain().labelsTextField.getText();
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

    private final Comparator<EntryContent> comparator = (o1, o2) -> o1.getFirstLabel().compareToIgnoreCase(o2.getFirstLabel());
    private final FieldsHandler parent;
    private Map<Integer, EntryContent> links = new HashMap<>();
    private String linkStr = "";
    private HashSet<Integer> toRemove = new HashSet<>();

    public LinkHandler(FieldsHandler parent) {
        this.parent = parent;

        parent.getMain().linksTextFlow.setOnMouseClicked(mouseEvent -> {
            parent.getMain().linksTextArea.requestFocus();
            parent.getMain().linksTextArea.end();
            mouseEvent.consume();
        });
        parent.getMain().linksTextArea.focusedProperty().
                addListener((observableValue, aBoolean, t1) -> focusChanged(t1));
    }

    public void clearDisplay() {
        links.clear();
        toRemove = new HashSet<>();
        parent.getMain().linksTextFlow.getChildren().clear();
        parent.getMain().linksTextArea.clear();
    }

    private Hyperlink createLink(EntryContent entry, boolean isLast) {
        String label = entry.getFirstLabel();
        if (!isLast)
            label += ", ";
        Hyperlink link = new Hyperlink(label);
        link.setOnAction(actionEvent -> parent.getMain().getNavigation().selectEntry(entry, true));
        return link;
    }

    public void displayEntry(EntryContent val) {
        LexiconDatabase.getInstance().queryEntryLinks(val.getId()).forEach(id -> {
            Optional<EntryContent> entry = parent.getMain().getDatabaseAccess().getByID(id);
            entry.ifPresent(e -> this.links.put(id, e));
        });
        this.linkStr = toLinkString(this.links.values());
        displayTextFlow();
    }

    private void displayTextFlow() {

        if (links.size() == 0)
            return;

        List<EntryContent> sortedList = this.links.values().stream().
                sorted(comparator).collect(Collectors.toList());

        sortedList.forEach(e -> {
            Hyperlink h = createLink(e, sortedList.indexOf(e) == sortedList.size()-1);
            parent.getMain().linksTextFlow.getChildren().add(h);
        });
    }

    private void focusChanged(Boolean hasFocus) {
//        main.getMenuHandler().setAllowNavigation(!hasFocus);
        System.out.println("focus: " + hasFocus);
        if (hasFocus){
            parent.getMain().linksTextFlow.getChildren().clear();
            parent.getMain().linksTextArea.setText(linkStr);
        }else{
            updateLinks(parent.getMain().linksTextArea.getText());
            parent.getMain().linksTextArea.clear();
            displayTextFlow();
        }
    }

    private void updateLinks(String txt) {

        if (Strings.isNullOrEmpty(txt)){
            toRemove.addAll(this.links.keySet());
            links.clear();
            linkStr = "";
            return;
        }

        List<Pattern> patterns = Arrays.stream(txt.split(", *")).
                map(s -> LabelHandler.getSearchPattern(Strings.camel(s))).
                collect(Collectors.toList());

        Map<Integer, EntryContent> newLinks = parent.getMain().getDatabaseAccess().getEntries().stream().
                filter(e -> patterns.stream().anyMatch(p -> p.matcher(e.getLabelStr()).matches())).
                collect(Collectors.toMap(EntryContent::getId, e -> e));

        toRemove.addAll(this.links.keySet());
        toRemove.removeAll(newLinks.keySet());

        this.links = newLinks;
        this.linkStr = toLinkString(newLinks.values());
    }

    public void recordToEntry(EntryContent val) {
        //System.out.println("not implemented: record links");
        HashSet<Integer> toRecord = new HashSet<>(this.links.keySet());
        toRecord.removeAll(this.toRemove);

        toRecord.forEach(i -> LexiconDatabase.getInstance().insertLink(i, val.getId()));
        toRemove.forEach(i -> LexiconDatabase.getInstance().removeLink(i, val.getId()));
    }

    private String toLinkString(Collection<EntryContent> values) {
        Stream<EntryContent> st = values.stream().
                sorted(comparator);
        return Strings.toString(st, ", ");
    }
}

class ContentHandler {
    private final FieldsHandler parent;
    private String content;
    private boolean isModified;

    public ContentHandler(FieldsHandler parent) {
        this.parent = parent;

        parent.getMain().textFlow_scrollPane.setOnMouseClicked(mouseEvent -> {
//            if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
            parent.getMain().contentTextArea.requestFocus();
            parent.getMain().contentTextArea.end();
            mouseEvent.consume();
        });

        parent.getMain().contentTextArea.focusedProperty().addListener((observableValue, aBoolean, t1) -> focusChanged(t1));
    }

    public void clearDisplay() {
        parent.getMain().contentTextArea.clear();
        parent.getMain().contentTextFlow.getChildren().clear();
        content = "";
        //System.out.println("not implemented: clear content");
    }

    public void displayEntry(EntryContent val) {
        this.content = val.getContent();
        displayTextFlow();
        //  System.out.println("not implemented: display content");
    }

    private void displayTextFlow() {

        parent.getMain().contentTextFlow.getChildren().clear();
        if (Strings.isNullOrEmpty(this.content)) {
            return;
        }

        List<TextLink> links = new ArrayList<>();
        for (EntryContent entry: parent.getMain().getDatabaseAccess().getEntries()) {
            for (String label: entry.getLabels())
            {
                Pattern pattern = Pattern.compile("\\b("+ label + "[sx]?)\\b", Pattern.CASE_INSENSITIVE);
                //  Pattern pattern = Pattern.compile("(^|\\b)("+label + "[sx]?)\\b");
                Matcher matcher = pattern.matcher(content);
                while (matcher.find())
                {
                    TextLink link = new TextLink(entry, matcher.start(), matcher.end());
                    links.add(link);
                }
            }
        }
        links.sort(Comparator.comparingInt(TextLink::getStartIndex));

        int currentIndex =0;

        List<Node> nodes = new ArrayList<>();
        for (TextLink link :links) {
            if (currentIndex < link.getStartIndex()){
                String sequence = content.substring(currentIndex, link.getStartIndex());
                for (String str:sequence.split(" ")) {
                    myText text = myText.create(parent, str + " ", myText.VALID_LINK);
                    nodes.add(text);
                }
            }

            String linkStr = content.substring(link.getStartIndex(), link.getEndIndex());
            if (link.getEntry() != parent.getDisplayEntry()) {
                if (nodes.size() > 1 && nodes.get(nodes.size()-1) instanceof Hyperlink){
                    nodes.add(new Text(" "));
                }

                Hyperlink hyperlink = new Hyperlink(linkStr);
                hyperlink.setOnAction(actionEvent -> parent.getMain().getNavigation().selectEntry(link.getEntry(), true));
                nodes.add(hyperlink);
            }
            else{
                myText text = myText.create(parent, linkStr, myText.INVALID_LINK);
                nodes.add(text);
            }
            currentIndex = link.getEndIndex();
        }

        nodes.add(new Text(content.substring(currentIndex))); // <- end of text
        nodes.forEach(n-> parent.getMain().contentTextFlow.getChildren().add(n));
    }

    private void focusChanged(Boolean hasFocus) {
       // System.out.println("text area focus state: " + hasFocus + " - " + focusCount++);
        if (hasFocus){
            parent.getMain().contentTextArea.setText(content);
            parent.getMain().textFlow_scrollPane.setVisible(false);

        }else{
            if (parent.getDisplayEntry() == null){
                this.isModified = false;
                this.content = "";
            }
            else {
                this.isModified = !this.content.equals(parent.getMain().contentTextArea.getText());
                this.content = parent.getMain().contentTextArea.getText();
            }
            parent.getMain().contentTextArea.clear();
            parent.getMain().textFlow_scrollPane.setVisible(true);

            displayTextFlow();
        }

//        if (hasFocus){
//            parent.getMain().contentTextArea.setText(parent.getMain().getCurrentEntry().getContent());
//           // parent.getMain().contentTextFlow.getChildren().clear();
//            parent.getMain().textFlow_scrollPane.setVisible(false);
//        }else{
//            parent.getMain().getCurrentEntry().setContent(parent.getMain().contentTextArea.getText());
//        }
    }

    public void recordToEntry(EntryContent val) {
        if (isModified){
            val.setContent(this.content);
        }
        //System.out.println("not implemented: record content");
    }
}

class myText extends Text{

    public static final int VALID_LINK = 1;
    public static final int INVALID_LINK = 0;
    public static final String VALID_STYLE = "-fx-font-smoothing-type: lcd;-fx-fill: red;";
    public static final String NEUTRAL_STYLE = "-fx-font-smoothing-type: lcd;-fx-fill: black;";
    private static final String INVALID_STYLE = "-fx-font-smoothing-type: lcd;-fx-fill: orange;";

    private final String txt;
    private final int type;

    public myText(String txt, int type) {
        this.txt = txt;
        this.type = type;
    }

    @Override
    public String toString() {
        return "myText{" +
                "txt='" + txt + '\'' +
                '}';
    }

    public static myText create(FieldsHandler handler, String str, int type){
        myText text = new myText(str, type);
        text.setText(str);

        String style = type > 0 ? VALID_STYLE : INVALID_STYLE;
        text.setOnMouseEntered(mouseEvent -> text.setStyle(style));
        text.setOnMouseExited(mouseEvent -> text.setStyle(NEUTRAL_STYLE));

        ContextMenu menu = new ContextMenu();
        MenuItem createEntry = new MenuItem("");
        createEntry.setOnAction(actionEvent -> handler.getMain().getDatabaseAccess().createEntry(str));
        menu.getItems().add(createEntry);

        if (type > 0){
            text.setOnContextMenuRequested(contextMenuEvent -> {
                createEntry.setText("Create entry from ["+ str + "]");
                menu.show(text, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                contextMenuEvent.consume();
            });
        }
        text.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY){
                mouseEvent.consume();
            }
        });
        return text;
    }
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