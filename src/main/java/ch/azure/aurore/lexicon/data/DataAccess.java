package ch.azure.aurore.lexicon.data;

import ch.azure.aurore.IO.API.FileHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataAccess {

    //region constants

    private static final String ENTRIES_TABLE_NAME = "Entries";
    private static final String LINKS_TABLE_NAME = "Links";

    private static final String ENTRIES_CONTENT_FIELD = "Content";
    private static final String ENTRIES_LABEL_FIELD = "Labels";

    private static final String LINKS_MIN_FIELD = "MinID";
    private static final String LINKS_MAX_FIELD = "MaxID";

    private static final String CREATE_ENTRIES_TABLE = "CREATE TABLE IF NOT EXISTS " + ENTRIES_TABLE_NAME + " (_id INTEGER PRIMARY KEY, " + ENTRIES_CONTENT_FIELD + " TEXT, " + ENTRIES_LABEL_FIELD + " Text)";
    private static final String CREATE_LINKS_TABLE = "CREATE TABLE IF NOT EXISTS " + LINKS_TABLE_NAME + " (" + LINKS_MIN_FIELD + " INTEGER, " + LINKS_MAX_FIELD + " INTEGER)";
    private static final String INSERT_CONTENT_STATEMENT = "INSERT INTO " + ENTRIES_TABLE_NAME + " (" + ENTRIES_CONTENT_FIELD + ", " + ENTRIES_LABEL_FIELD + ") VALUES (?,?)";
    private static final String INSERT_LINK_STATEMENT = "INSERT INTO " + LINKS_TABLE_NAME + " (" + LINKS_MIN_FIELD + ", " + LINKS_MAX_FIELD + ") VALUES (?,?)";
    private static final String QUERY_ENTRIES_STATEMENT = "SELECT * FROM " + ENTRIES_TABLE_NAME;
    private static final String UPDATE_ENTRY_STATEMENT = "UPDATE " + ENTRIES_TABLE_NAME + " SET " + ENTRIES_CONTENT_FIELD + " = ?, " + ENTRIES_LABEL_FIELD + " = ? WHERE _id = ?";
    private static final String REMOVE_ENTRY_STATEMENT = "DELETE FROM " + ENTRIES_TABLE_NAME + " WHERE _id = ?";

    //endregion

    public static void main(String[] args) {
        String pathStr =  "C:\\Users\\auror\\OneDrive\\Apps\\Lexicon\\TestLexicon.SQLite";
        DataAccess.getInstance().open(pathStr);
        DataAccess.getInstance().createEntry("Wolves and Elephants are animals", "animal");
        DataAccess.getInstance().createEntry("The wolf is an animal", "wolf");
        DataAccess.getInstance().createEntry("Elephants are very big", "elephants");

        DataAccess.getInstance().close();
        FileHelper.openFile("C:\\Users\\auror\\OneDrive\\Apps\\Lexicon");
    }

    public static DataAccess getInstance() {
        return instance;
    }

    static DataAccess instance = new DataAccess();

    private PreparedStatement insertContentStatement;
    private PreparedStatement insertLinkStatement;
    private PreparedStatement queryAllStatement;
    private PreparedStatement removeEntryStatement;
    private PreparedStatement updateEntryStatement;

    private Connection conn;

    public boolean open(String databasePath) {

        if (conn != null)
            close();

        FileHelper.backupFile(databasePath);

        String connectStr = "jdbc:sqlite:" + databasePath;
        try {
            conn = DriverManager.getConnection(connectStr);

            Statement statement = conn.createStatement();
            statement.execute(CREATE_ENTRIES_TABLE);
            statement.execute(CREATE_LINKS_TABLE);
            statement.close();

            insertContentStatement = conn.prepareStatement(INSERT_CONTENT_STATEMENT);
            insertLinkStatement = conn.prepareStatement(INSERT_LINK_STATEMENT);
            queryAllStatement = conn.prepareStatement(QUERY_ENTRIES_STATEMENT);
            updateEntryStatement = conn.prepareStatement(UPDATE_ENTRY_STATEMENT);
            removeEntryStatement = conn.prepareStatement(REMOVE_ENTRY_STATEMENT);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Can't open database");
            return false;
        }
    }

    public void close() {
        try {
            if (insertContentStatement != null)
                insertContentStatement.close();

            if (insertLinkStatement != null)
                insertLinkStatement.close();

            if (queryAllStatement != null)
                queryAllStatement.close();

            if (removeEntryStatement != null)
                removeEntryStatement.close();

            if (updateEntryStatement != null)
                updateEntryStatement.close();

            if (conn != null)
                conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateEntry(EntryContent entry){

        try {
            updateEntryStatement.setString(1, entry.getContent());
            updateEntryStatement.setString(2, entry.getLabels());
            updateEntryStatement.setInt(3, entry.getId());

            int updateCount = updateEntryStatement.executeUpdate();
            if (updateCount != 1)
                throw new SQLException("Update failed");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int createEntry(String contentStr, String labels) {
        ResultSet result = null;
        try {
            insertContentStatement.setString(1, contentStr);
            insertContentStatement.setString(2, labels);

            int updateCount = insertContentStatement.executeUpdate();
            if (updateCount != 1)
                throw new SQLException("New content insert failed");

            result = insertContentStatement.getGeneratedKeys();
            if (result.next()) {
                return result.getInt(1);
            }
            throw new SQLException("New content insert failed: can't get id");

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertLink(int minID, int maxID) {
        try {
            insertLinkStatement.setInt(1, minID);
            insertLinkStatement.setInt(2, maxID);

            int updateCount = insertLinkStatement.executeUpdate();
            if (updateCount != 1)
                throw new SQLException("New content insert failed");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EntryContent> queryEntries() {
        ResultSet result = null;
        List<EntryContent> list = new ArrayList<>();
        try {
            result = queryAllStatement.executeQuery();
            while (result.next()) {

                int id = result.getInt(1);
                String content = result.getString(2);
                String labels = result.getString(3);
                EntryContent item = new EntryContent(id, content, labels);
                list.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public boolean removeEntry(EntryContent item) {
        try {
            removeEntryStatement.setInt(1, item.getId());
            if (removeEntryStatement.executeUpdate() == 1) {
                return true;
            }

            throw new SQLException("Delete operation failed");

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}