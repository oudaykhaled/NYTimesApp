package ouday.challenge.com.app_repository.httpManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bundle to hold all HTTP Head Key-Value data
 */
public class HeadBundle {
    private HashMap<String, Object> hmHead = new HashMap<>();
    public void addBodyDetail(String key, String value){
        hmHead.put(key, value);
    }
    public void remove(String key){
        hmHead.remove(key);
    }
    public Set<Map.Entry<String, Object>> getIterator(){
        return hmHead.entrySet();
    }

}