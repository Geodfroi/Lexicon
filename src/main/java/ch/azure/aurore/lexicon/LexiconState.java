package ch.azure.aurore.lexicon;

import ch.azure.aurore.javaxt.fxml.AppState;
import ch.azure.aurore.javaxt.strings.Strings;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

public class LexiconState extends AppState {

    private Map<String, Integer> currentIds = new HashMap<>();
    private Map<String, String> databaseMap = new HashMap<>();
    private String currentDB;
    private boolean showEmpty;
    private boolean fullscreen;

    public static LexiconState getInstance(){
        return (LexiconState)App.getInstance().getState();
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        modified();
    }

    public boolean isShowEmpty() {
        return showEmpty;
    }

    public void setShowEmpty(boolean selected) {
        showEmpty = selected;
        modified();
    }

    public void setDatabasePath(String name, String path) {
        databaseMap.put(name, path);
        modified();
    }

    @SuppressWarnings("unused") //<- for JSON serialisation
    public Map<String, String> getDatabaseMap() {
        return databaseMap;
    }

    @SuppressWarnings("unused") //<- for JSON serialisation
    public void setDatabaseMap(Map<String, String> databaseMap) {
        this.databaseMap = databaseMap;
    }

    public String getCurrentDB() {
        return currentDB;
    }

    public void setCurrentDB(String currentDB) {
        if (!Objects.equals(this.currentDB, currentDB)) {
            this.currentDB = currentDB;
            modified();
        }
    }

    @JsonIgnore
    public String getCurrentPathStr() {
        if (!Strings.isNullOrEmpty(currentDB) && databaseMap.containsKey(currentDB))
            return currentDB;
        return null;
    }

    public String getDatabasePath(String name) {
        if (databaseMap.containsKey(name)) {
            return databaseMap.get(name);
        }
        return null;
    }

    public void removePath(String name) {
        databaseMap.remove(name);
        modified();
    }

    public void clearPaths() {
        databaseMap.clear();
        modified();
    }

    @JsonIgnore
    public Set<String> getDBList() {
        return new HashSet<>(databaseMap.keySet());
    }


    @SuppressWarnings("unused") //<- for JSON serialisation
    public Map<String, Integer> getCurrentIds() {
        return currentIds;
    }

    @SuppressWarnings("unused") //<- for JSON serialisation
    public void setCurrentIds(Map<String, Integer> currentIds) {
        this.currentIds = currentIds;
    }

    public int getCurrentID(String databaseName) {
        if (currentIds.containsKey(databaseName)) {
            return currentIds.get(databaseName);
        }
        return -1;
    }

    public void setCurrentID(String currentDB, int id) {
        currentIds.put(currentDB, id);
        modified();
    }
}
