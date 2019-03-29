package ouday.challenge.com.app_repository.httpManager;

/**
 * Contains all Call back for any Raw HTTP Response
 */
public interface OnServerRawResponse {
    void onSuccess(HttpManager httpManager, String response);
    void onFailed(HttpManager httpManager, Exception ex);
}