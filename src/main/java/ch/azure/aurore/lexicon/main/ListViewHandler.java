package ch.azure.aurore.lexicon.main;

import ch.azure.aurore.lexicon.DatabaseAccess;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.IEntryListener;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListViewHandler {

    private final MainController main;
    private final ListView<EntryContent> listView;
    private final ChangeListener<EntryContent> listViewListener;
    private String filterStr = "";

    public ListViewHandler(MainController main, ListView<EntryContent> listView) {
        this.main = main;
        this.listView = listView;

        listView.setCellFactory(entryContentListView -> new EntryListCell());
        listViewListener = (observableValue, entryContent, t1) -> main.getNavigation().selectEntry(t1.get_id(), false);
        switchListViewEvent(true);

        //region context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(e -> {
            switchListViewEvent(false);
            main.getMenuHandler().deleteCurrentEntry();
            switchListViewEvent(true);
        });
        menu.getItems().add(delete);
        listView.setContextMenu(menu);
        //endregion

        //region search box
        main.searchTextField.textProperty().addListener((observableValue, s, t1) -> {
            filterStr = t1;
            displayEntries();
        });
        //endregion
    }

    public void displayEntries() {
        ObservableList<EntryContent> entries = FXCollections.observableList(DatabaseAccess.getInstance().queryEntries());
        if (entries.size() == 0)
            return;

        FilteredList<EntryContent> filteredList = new FilteredList<>(entries, e -> {

            if (!main.getMenuHandler().showEmpty() && !e.hasContent())
                return false;

            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            String labelStr = EntryContent.toLabelStr(e.getLabels());
            Matcher matcher = pattern.matcher(labelStr);
            return matcher.matches();
        });

        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getFirstLabel().compareToIgnoreCase(right.getFirstLabel()));
        listView.setItems(sortedList);

        int selection = main.getNavigation().getCurrentEntry();
        if (selection != 0) {
            EntryContent item = DatabaseAccess.getInstance().queryEntry(selection);
            listView.getSelectionModel().select(item);
        }
    }

    public void scrollTo(EntryContent val) {
        switchListViewEvent(false);
        listView.getSelectionModel().select(val);
        switchListViewEvent(true);
        // main.entriesListView.scrollTo(val);
    }

    private void switchListViewEvent(boolean value) {
        if (value) {
            listView.getSelectionModel().selectedItemProperty().addListener(listViewListener);
        } else {
            listView.getSelectionModel().selectedItemProperty().removeListener(listViewListener);
        }
    }
}

class EntryListCell extends ListCell<EntryContent> implements IEntryListener {
    @Override
    protected void updateItem(EntryContent item, boolean empty) {
        super.updateItem(item, empty);

        if (empty)
            setText("");
        else {
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