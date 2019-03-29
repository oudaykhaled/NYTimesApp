package ouday.challenge.com.app_repository.httpManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bundle to hold all HTTP Body Key-Value data
 */
public class BodyBundle {
    private HashMap<String, Object> hmBody = new HashMap<>();
    public void addBodyDetail(String key, String value){
        hmBody.put(key, value);
    }
    public void remove(String key){
        hmBody.remove(key);
    }
    public Set<Map.Entry<String, Object>> getIterator(){
        return hmBody.entrySet();
    }
}