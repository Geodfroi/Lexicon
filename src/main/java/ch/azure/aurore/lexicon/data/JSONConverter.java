package ch.azure.aurore.lexicon.data;

import JavaExt.Collections.CollectionSt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JSONConverter {

    private final static String JSON_PATH = "craftLexicon.json";

    public static void main(String[] args) {

        List<JSONEntry> entries = getJSONEntries();
//        for (JSONEntry entry: entries) {
//            System.out.println(entry.key);
//            System.out.println(entry.content);
//            System.out.println(entry.labels);
//            System.out.println(entry.links);
//            System.out.println("**********");
//        }
        System.out.println("********\r\nobject count: " + entries.size());

        DataAccess database = DataAccess.getInstance();

        String databasePath = File.separator + "Database.SQLite";
        database.open(databasePath);

        System.out.println("Building entries");
        setEntry(database, entries);
        System.out.println("setting links");
        setLinks(database,entries);

        database.close();
    }

    private static void setLinks(DataAccess database, List<JSONEntry> entries) {

        List<JSONLink> links = new ArrayList<>();

        for (JSONEntry entry:entries) {

               entries.stream().
                       filter(e -> entry.links.contains(e.getKey())).
                       forEach(e -> createLink(links, entry.getID(), e.getID(), database));

        }
    }

    private static void createLink(List<JSONLink> links, int left, int right, DataAccess database) {
        int firstID = Math.min(left, right);
        int secondID = Math.max(left, right);

        if (links.stream().noneMatch(link -> link.getMinID() == firstID && link.getMaxID() == secondID)){

            var link = new JSONLink(firstID, secondID);
            links.add(link);
            database.InsertLink(link.getMinID(), link.getMaxID());
            System.out.println("Insert link: " + link.getMinID() + "-" + link.getMaxID());
        }
        else
            System.out.println("duplicate link");
    }

    private static void setEntry(DataAccess database, List<JSONEntry> entries) {

        for (JSONEntry entry : entries) {

            String labels = CollectionSt.toString(entry.getLabels(), ", ");
            System.out.println("Entry : " + labels);
            int id = database.NewContent(entry.content, labels);
            entry.setId(id);
        }
    }

    private static List<JSONEntry> getJSONEntries() {
        ArrayList<JSONEntry> entries = new ArrayList<>();
        Path path = Paths.get(JSON_PATH);

       throw new UnsupportedOperationException("Not implemented yet: getJSONEntries");
//        try {
//            String str = Files.readString(path);
//            JSONArray array = new JSONArray(str);
//            for (int i = 0; i < array.length(); i++) {
//
//                JSONObject object = array.getJSONObject(i);
//                System.out.println(object);
//
//                JSONEntry entry = new JSONEntry();
//                entries.add(entry);
//
//                if (!object.isNull("Content")){
//                    entry.setContent(object.getString("Content"));
//                }
//
//                entry.setKey(object.getString("Key"));
//
//                JSONArray linksArray = object.getJSONArray("Links");
//                for (int n = 0; n < linksArray.length(); n++)
//                    entry.addLink(linksArray.getString(n));
//
//                JSONArray labelArray = object.getJSONArray("Labels");
//                for (int n = 0; n < labelArray.length(); n++)
//                    entry.addLabel(labelArray.getString(n));
//            }
//
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        return entries;
    }
}
