package ch.azure.aurore.lexicon.data;

public class JSONLink {

    int minID;
    int maxID;

    public JSONLink(int firstID, int secondID) {
        this.minID=firstID;
        this.maxID=secondID;
    }

    public int getMinID() {
        return minID;
    }

    public void setMinID(int minID) {
        this.minID = minID;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }
}
