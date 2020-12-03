package ch.azure.aurore.lexicon;

import java.util.ArrayList;
import java.util.List;

enum Direction
{
    forward, backward,
}

public class NavStack<T> {

    private  List<T> list = new ArrayList<>();
    private int index = -1;

    public void add(T value) {

        //skip if it is the current item
        if (index != -1 && list.get(index) == value)
            return;

        //shorten list to current index
        if (hasNext())
            list = list.subList(0, index + 1);

        list.add(value);
        index = list.size()-1;
    }

    public void clear() {
        list.clear();
        index =-1;
    }

    public boolean hasFormer() {
        return list.size() >=1 && index >0;
    }

    public boolean hasNext() {
        return index < list.size()-1;
    }

    public T navigateStack(Direction direction) {
        if (direction == Direction.backward){
            return toFormer();
        }
        else{
            return toNext();
        }
    }

    public T toNext(){
        if (hasNext()) {
            index++;
            return list.get(index);
        }
        return null;
    }
    public T toFormer() {
        if (hasFormer()){
            index--;
            return list.get(index);
        }
        return null;
    }
}
