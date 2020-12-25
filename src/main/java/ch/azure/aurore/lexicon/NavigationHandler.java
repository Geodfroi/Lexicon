package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.collections.Directions;
import ch.azure.aurore.javaxt.collections.NavStack;
import ch.azure.aurore.javaxt.strings.Strings;
import ch.azure.aurore.lexiconDB.EntryContent;

public class NavigationHandler {

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

        LexiconState.getInstance().setCurrentID(currentDB, val.get_id());
        currentEntry = val;

        main.getFieldsHandler().displayEntry(val);

        if (selectListView)
            main.getListViewHandler().scrollTo(val);

        main.getMenuHandler().enableNavMenus(navStack.hasFormer(), navStack.hasNext());
    }

    public void toRecordedEntry(String loadedDB) {
        int id = LexiconState.getInstance().getCurrentID(loadedDB);
        if (id != -1){
            EntryContent entry = main.getDatabaseAccess().getByID(id);
            if (entry != null)
                selectEntry(entry, true);
        }
    }
}
