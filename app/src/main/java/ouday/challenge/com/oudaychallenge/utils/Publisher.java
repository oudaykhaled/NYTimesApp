package ouday.challenge.com.oudaychallenge.utils;

import java.util.ArrayList;

public class Publisher<T> {

    private ArrayList<T> lstObservers = new ArrayList<>();

    public void register(T observer){
        lstObservers.add(observer);
    }

    public void unregister(T observer){
        lstObservers.remove(observer);
    }


}
