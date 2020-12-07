package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.IEntryListener;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EntryListCell extends ListCell<EntryContent> implements IEntryListener {
    @Override
    protected void updateItem(EntryContent item, boolean empty) {
        super.updateItem(item, empty);

        if (empty)
            setText("");
        else{
            item.addListener(this);
            String txt = EntryContent.toLabelStr(item.getLabels());
            setText(txt);
            setTextFill(Color.BLUE.darker());
        }
    }

    @Override
    public void entryModified(EntryContent entryContent) {
        this.updateItem(entryContent, false);
    }
}

public class ListViewHandler {

    private static final String CURRENT_ENTRIES = "currentEntries";

    private final MainController main;
    private String filterStr = "";

    public ListViewHandler(MainController main) {
        this.main = main;

        main.entriesListView.getSelectionModel().selectedItemProperty().addListener((observableValue, entryContent, t1) -> entrySelected(t1));
        main.entriesListView.setCellFactory(entryContentListView -> new EntryListCell());

        //region context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> deleteEntry());
        menu.getItems().add(delete);
        main.entriesListView.setContextMenu(menu);
        //endregion

        //region search box
        main.searchTextField.textProperty().addListener((observableValue, s, t1) -> {
            filterStr = t1;
            refreshEntriesDisplay();
        });
        //endregion
    }

    public void deleteEntry() {
        EntryContent item = main.entriesListView.getSelectionModel().getSelectedItem();
        if (item == null) {
            System.out.println("no selection for delete");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Delete selected entry ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                if (LexiconDatabase.getInstance().removeEntry(item)) {
                    main.getEntries().remove(item);
                  //  main.refresh();
                }
            }
        }
    }

    private void entrySelected(EntryContent value) {
        if (value != null) {

            Optional<String> db = main.getCurrentDB();
            if (db.isEmpty())
                return;

            main.entriesListView.scrollTo(value);
            main.setCurrentEntry(value);
            main.getTextLoader().setTextFlow();
            main.getLinksHandler().setTextFlow(main.getCurrentEntry());
            String labelStr = EntryContent.toLabelStr(value.getLabels());
            main.labelsTextField.setText(labelStr);

            main.getImageHandler().displayImage();
            main.getImageHandler().enableManipulateImageMenu(main.getCurrentEntry() .hasImage());

            LocalSave.getInstance().setMapValue(CURRENT_ENTRIES, db.get(), value.getId());
            main.getNavStack().add(value);

            main.getMenuHandler().setCanGoToFormer(main.getNavStack().hasFormer());
            main.getMenuHandler().setCanGoToNext(main.getNavStack().hasNext());
        }
    }

    public void refreshEntriesDisplay() {
        FilteredList<EntryContent> filteredList = new FilteredList<>(main.getEntries(), entryContent -> {

            if (!main.getMenuHandler().IsShowEmptyEntries() && !entryContent.hasContent())
                return false;

            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            String labelStr = EntryContent.toLabelStr(entryContent.getLabels());
            Matcher matcher = pattern.matcher(labelStr);
            return matcher.matches();
        });

        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getFirstLabel().compareToIgnoreCase(right.getFirstLabel()));
        main.entriesListView.setItems(sortedList);

        Optional<String> db = main.getCurrentDB();
        if (db.isPresent()) {
            Optional<Integer> currentID = LocalSave.getInstance().getMapInteger(CURRENT_ENTRIES, db.get());
            if (currentID.isPresent()) {
                Optional<EntryContent> result = sortedList.stream().
                        filter(e -> e.getId() == currentID.get()).findAny();

                result.ifPresent(e -> main.entriesListView.getSelectionModel().select(e));
            }
        }
    }
}