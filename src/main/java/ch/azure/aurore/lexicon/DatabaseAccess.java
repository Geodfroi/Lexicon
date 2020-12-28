package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.sqlite.wrapper.SQLite;
import ch.azure.aurore.lexiconDB.EntryContent;

import java.util.List;

public class DatabaseAccess {

    private final static DatabaseAccess instance = new DatabaseAccess();

    public static DatabaseAccess getInstance(){
        return instance;
    }

    private String loadedDB;
    private SQLite sqLite;

    public String getLoadedDB() {
        return loadedDB;
    }

    public EntryContent getByID(int id) {
        return sqLite.queryItem(EntryContent.class, id);
    }


    public boolean loadDatabase(String name) {
        if (name == null || name.equals(loadedDB)) {
            return false;
        }
        String path = LexiconState.getInstance().getDatabasePath(name);
        if (path == null)
            return false;

        sqLite = SQLite.connect(path);
        if (sqLite != null) {
            this.loadedDB = name;
            LexiconState.getInstance().setCurrentDB(name);
            return true;
        } else{
            LexiconState.getInstance().removePath(name);
            return false;
        }
    }

    public void clearData() {
        LexiconState.getInstance().clearPaths();
        loadedDB = "";
    }

    public void close() {
        if (sqLite != null)
            sqLite.close();
    }

    public boolean updateItem(EntryContent item) {
        return sqLite.updateItem(item);
    }

    public EntryContent queryEntry(Integer id) {
        return sqLite.queryItem(EntryContent.class, id);
    }

    public List<EntryContent> queryEntries() {
        return sqLite.queryItems(EntryContent.class);
    }

    public boolean removeItem(EntryContent entry) {
        return sqLite.removeItem(entry);
    }
}
