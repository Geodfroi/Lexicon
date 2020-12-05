package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.IEntryListener;
import ch.azure.aurore.lexiconDB.LexiconDatabase;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

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
            setText(item.getLabels());
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

        ChangeListener<EntryContent> listViewListener = (observableValue, oldValue, newValue) -> entrySelected(newValue);
        main.entriesListView.getSelectionModel().selectedItemProperty().addListener(listViewListener);

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
            showEntriesList();
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
                }
            }
        }
    }

    public void showEntriesList() {

        FilteredList<EntryContent> filteredList = new FilteredList<>(main.getEntries(), entryContent -> {

            if (!main.getMenuHandler().IsShowEmptyEntries() && !entryContent.hasContent())
                return false;

            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            Matcher matcher = pattern.matcher(entryContent.getLabels());
            return matcher.matches();
        });
        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getLabels().compareToIgnoreCase(right.getLabels()));
        main.entriesListView.setItems(sortedList);
        if (main.getCurrentDatabase() != null) {
            Optional<Integer> currentID = LocalSave.getMapInteger(CURRENT_ENTRIES, main.getCurrentDatabase());
            if (currentID.isPresent()) {
                Optional<EntryContent> result = sortedList.stream().
                        filter(e -> e.getId() == currentID.get()).findAny();

                result.ifPresent(e -> main.entriesListView.getSelectionModel().select(e));
            }
        }
    }

    private void entrySelected(EntryContent value) {
        if (value != null) {
            EntryContent currentEntry = main.getCurrentEntry();
            if (currentEntry != null)
                currentEntry.saveEntry();

            main.setCurrentEntry(value);
            main.getTextLoader().setTextFlow();
            main.getLinksHandler().setTextFlow();
            main.labelsTextField.setText(value.getLabels());

            main.getImageHandler().displayImage();
            main.getImageHandler().enableManipulateImageMenu(currentEntry != null && currentEntry.hasImage());

            String currentDatabase = main.getCurrentDatabase();
            LocalSave.setMapValue(CURRENT_ENTRIES, currentDatabase, value.getId());
            main.getNavStack().add(value);

            main.getMenuHandler().enableLastMenu(main.getNavStack().hasFormer());
            main.getMenuHandler().enableNextMenu(main.getNavStack().hasNext());
        }
    }
}