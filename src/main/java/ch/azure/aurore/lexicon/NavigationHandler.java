package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.collections.Directions;
import ch.azure.aurore.collections.NavStack;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.strings.Strings;

import java.util.Optional;

public class NavigationHandler {

    private static final String SELECTED_ENTRIES = "currentEntries";

    private final NavStack<EntryContent> navStack = new NavStack<>();
    private EntryContent currentEntry;

    private final MainController main;

    public NavigationHandler(MainController main) {
        this.main = main;
    }

    public EntryContent getCurrentEntry() {
        return currentEntry;
    }

    public void clearEntry(){
        navStack.clear();
        currentEntry = null;
        main.getFieldsHandler().clearDisplay();
        main.getMenuHandler().enableNavMenus(false, false);
    }

    public void navigate(Directions dir) {
        EntryContent entry = navStack.navigateStack(dir);
        selectEntry(entry, true);
    }

    public void selectEntry(EntryContent val, boolean selectListView) {
        if (val == null)
            return;

        navStack.add(val);
        String currentDB = main.getDatabaseAccess().getLoadedDB();
        if (Strings.isNullOrEmpty(currentDB))
            throw new IllegalStateException("DB value cannot be null");

        LocalSave.getInstance().setMapValue(SELECTED_ENTRIES, currentDB, val.getId());
        currentEntry = val;

        main.getFieldsHandler().displayEntry(val);

        if (selectListView)
            main.getListViewHandler().scrollTo(val);

        main.getMenuHandler().enableNavMenus(navStack.hasFormer(), navStack.hasNext());
    }

    public void toRecordedEntry(String loadedDB) {
        Optional<Integer> entryID = LocalSave.getInstance().getMapInteger(SELECTED_ENTRIES, loadedDB);
        if (entryID.isPresent()){
            Optional<EntryContent> entry = main.getDatabaseAccess().getByID(entryID.get());
            entry.ifPresent(entryContent -> selectEntry(entryContent, true));
        }
    }
}
