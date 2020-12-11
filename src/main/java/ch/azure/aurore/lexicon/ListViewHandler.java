package ch.azure.aurore.lexicon;

import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.lexiconDB.IEntryListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListViewHandler {

    private final MainController main;
    private final ListView<EntryContent> listView;
    private String filterStr = "";

    public ListViewHandler(MainController main, ListView<EntryContent> listView) {
        this.main = main;
        this.listView = listView;

        listView.setCellFactory(entryContentListView -> new EntryListCell());

        listView.getSelectionModel().selectedItemProperty().
                addListener((observableValue, entryContent, t1) -> listViewSelection(t1));

        //region context menu
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete Selection");
        delete.setOnAction(actionEvent -> {
            EntryContent current = main.getNavigation().getCurrentEntry();
            if (current != null)
                main.getDatabaseAccess().deleteEntry(current);
        });
        menu.getItems().add(delete);
        listView.setContextMenu(menu);
        //endregion

//        //region search box
//        main.searchTextField.textProperty().addListener((observableValue, s, t1) -> {
//            filterStr = t1;
//            refreshEntriesDisplay();
//        });
//        //endregion
    }

    public void displayEntries() {

        ObservableList<EntryContent> entries = main.getDatabaseAccess().getEntries();
        if (entries.size() == 0)
            return;

        FilteredList<EntryContent> filteredList = new FilteredList<>(entries, e -> {

            if (!main.getMenuHandler().hideEmptyEntries() && !e.hasContent())
                return false;

            Pattern pattern = Pattern.compile("^.*" + filterStr + ".*$");
            String labelStr = EntryContent.toLabelStr(e.getLabels());
            Matcher matcher = pattern.matcher(labelStr);
            return matcher.matches();
        });

        SortedList<EntryContent> sortedList = new SortedList<>(filteredList, (left, right) -> left.getFirstLabel().compareToIgnoreCase(right.getFirstLabel()));
        listView.setItems(sortedList);

        EntryContent selection = main.getNavigation().getCurrentEntry();
        if (selection != null)
            listView.getSelectionModel().select(selection);

//        Optional<String> db = main.getCurrentDB();
//        if (db.isPresent()) {
//            Optional<Integer> currentID = LocalSave.getInstance().getMapInteger(CURRENT_ENTRIES, db.get());
//            if (currentID.isPresent()) {
//                Optional<EntryContent> result = sortedList.stream().
//                        filter(e -> e.getId() == currentID.get()).findAny();
//
//                result.ifPresent(e -> main.entriesListView.getSelectionModel().select(e));
//            }
//        }
//    }
    }

    private void listViewSelection(EntryContent val) {
        if (val == null)
            return;

        main.getNavigation().selectEntry(val);
        main.getFieldsHandler().displayEntry(val);

//            main.entriesListView.scrollTo(value);


//        }
//    }
    }
}

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