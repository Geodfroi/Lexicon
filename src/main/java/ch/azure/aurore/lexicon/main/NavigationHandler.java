package ch.azure.aurore.lexicon.main;

import ch.azure.aurore.javaxt.collections.Directions;
import ch.azure.aurore.javaxt.collections.NavStack;
import ch.azure.aurore.javaxt.strings.Strings;
import ch.azure.aurore.lexicon.DatabaseAccess;
import ch.azure.aurore.lexicon.LexiconState;
import ch.azure.aurore.lexiconDB.EntryContent;

public class NavigationHandler {

    private final NavStack<Integer> navStack = new NavStack<>();
    private int currentEntry;

    private final MainController main;

    public NavigationHandler(MainController main) {
        this.main = main;
    }

    public int getCurrentEntry() {
        return currentEntry;
    }

    public void clearEntry(){
        navStack.clear();
        currentEntry = 0;
        main.getFieldsHandler().clearDisplay();
        main.getMenuHandler().enableNavMenus(false, false);
    }

    public void navigate(Directions dir) {
        int entryId = navStack.navigateStack(dir);
        selectEntry(entryId, true);
    }

    public void selectEntry(int id, boolean selectListView) {
        if (id ==0)
            return;

        navStack.add(id);
        String currentDB = DatabaseAccess.getInstance().getLoadedDB();
        if (Strings.isNullOrEmpty(currentDB))
            throw new IllegalStateException("DB value cannot be null");

        LexiconState.getInstance().setCurrentID(currentDB, id);
        currentEntry = id;

        main.getFieldsHandler().displayEntry(id);

        if (selectListView){
            EntryContent entry  = DatabaseAccess.getInstance().queryEntry(id);
            main.getListViewHandler().scrollTo(entry);
        }

        main.getMenuHandler().enableNavMenus(navStack.hasFormer(), navStack.hasNext());
    }

    public void toRecordedEntry(String loadedDB) {
        int id = LexiconState.getInstance().getCurrentID(loadedDB);
        if (id > 0)
            selectEntry(id, true);
    }
}
