package ouday.challenge.com.app_repository.httpManager;

/**
 * Created by AppsOuday on 9/18/2017.
 * Contains all Call back for any HTTP Response
 */
public interface OnServerResponse<T> {
    void onSuccess(HttpManager httpManager, T response);
    void onFailed(HttpManager httpManager, Exception ex);
}