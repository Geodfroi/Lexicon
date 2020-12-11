package ch.azure.aurore.lexicon;

import ch.azure.aurore.IO.API.LocalSave;
import ch.azure.aurore.collections.NavStack;
import ch.azure.aurore.lexiconDB.EntryContent;
import ch.azure.aurore.strings.Strings;

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

    public void clearNavStack() {
        navStack.clear();
    }

    public void selectEntry(EntryContent val) {
        navStack.add(val);
        String currentDB = main.getDatabaseAccess().getLoadedDB();
        if (Strings.isNullOrEmpty(currentDB))
            throw new IllegalStateException("DB value cannot be null");

        LocalSave.getInstance().setMapValue(SELECTED_ENTRIES, currentDB, val.getId());

        System.out.println("not implemented: update navigation menu");
//            main.getMenuHandler().setCanGoToFormer(main.getNavStack().hasFormer());
//            main.getMenuHandler().setCanGoToNext(main.getNavStack().hasNext());
    }
}
