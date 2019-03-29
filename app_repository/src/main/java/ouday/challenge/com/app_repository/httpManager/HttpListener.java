package ouday.challenge.com.app_repository.httpManager;

public interface HttpListener {
    String onPreExecuting(String url);
    String onPostExecuting(String url, String response);
}