package ouday.challenge.com.app_repository.httpManager;

import android.app.Application;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ouday.challenge.com.service_locator.Destroyable;

/**
 * This Interface will be used to perform any HTTP Request.
 */
public interface HttpManager extends Destroyable {

    HttpManager init(Application application);

    //region HTTP POST Methods
    String sendHttpPostWithRawResponse(String requestCode, String url, Serializable headBundle, Object bodyBundle) throws IOException;
    <T> T sendHttpPost(String requestCode, String url, Serializable headBundle, Object bodyBundle, Type type) throws IOException;
    void sendHttpPostWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerRawResponse onServerRawResponse);
    <T> void sendHttpPostAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, OnServerResponse<T> onServerResponse, Type type);
    //endregion



    //region HTTP GET Methods
    String sendHttpGetWithRawResponse(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit) throws Exception;
    <T> T sendHttpGet(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, Type type, int cacheDuration, TimeUnit cacheDurationUnit) throws Exception;
    void sendHttpGetWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit, final OnServerRawResponse onServerRawResponse) throws Exception;
    <T> void sendHttpGetAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit, OnServerResponse<T> onServerResponse, Type type) throws Exception;
    String sendHttpGetWithRawResponse(String requestCode, String url, Serializable headBundle, Serializable bodyBundle) throws Exception;
    <T> T sendHttpGet(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, Type type) throws Exception;
    void sendHttpGetWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, final OnServerRawResponse onServerRawResponse) throws Exception;
    <T> void sendHttpGetAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, OnServerResponse<T> onServerResponse, Type type) throws Exception;
    //endregion

    //region HTTP DELETE Methods
    String sendHttpDeleteWithRawResponse(String requestCode, String url) throws IOException;
    <T> T sendHttpDelete(String requestCode, String url, Type type) throws IOException;
    void sendHttpDeleteWithRawResponseAsync(String requestCode, String url, final OnServerRawResponse onServerRawResponse);
    <T> void sendHttpDeleteAsync(String requestCode, String url, OnServerResponse<T> onServerResponse, Type type);
    //endregion

    //region HTTP PUT Methods
    String sendHttpPutWithRawResponse(String requestCode, String url, Serializable headBundle, Object bodyBundle) throws IOException;
    <T> T sendHttpPut(String requestCode, String url, Serializable headBundle, Object bodyBundle, Type type) throws IOException;
    void sendHttpPutWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerRawResponse onServerRawResponse);
    <T> void sendHttpPutAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, OnServerResponse<T> onServerResponse, Type type);
    void sendHttpPutMultipartWithRawResponseAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, final OnServerRawResponse onServerRawResponse, FileToUpload... files);
    //endregion

    //region HTTP POST Methods
    String sendHttpPostMultipartWithRawResponse(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, FileToUpload... files) throws IOException;
    String sendHttpPostMultipartWithRawResponse(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, int connectTimeout, int readTimeout, int writeTimeout, FileToUpload... files) throws IOException;
    <T> T sendHttpPostMultipart(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, Type type, FileToUpload... files) throws IOException;
    <T> T sendHttpPostMultipart(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, Type type,int connectTimeout, int readTimeout, int writeTimeout, FileToUpload... files) throws IOException;
    void sendHttpPostMultipartWithRawResponseAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, final OnServerRawResponse onServerRawResponse, FileToUpload... files);
    void sendHttpPostMultipartWithRawResponseAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle,int connectTimeout, int readTimeout, int writeTimeout, final OnServerRawResponse onServerRawResponse, FileToUpload... files);
    <T> void sendHttpPostMultipartAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, OnServerResponse<T> onServerResponse, Type type, FileToUpload... files);
    <T> void sendHttpPostMultipartAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, OnServerResponse<T> onServerResponse, int connectTimeout, int readTimeout, int writeTimeout, Type type, FileToUpload... files);
    //endregion


    public String encode(String uriString);

    void registerHttpListener(HttpListener httpListener);

    boolean cancelRequestByRequestCode(String requestCode);

    <T> void sendHttpPostFormDataAsync(String requestCode, String url, Map<String, String> params, Map<String, String> headers, OnServerResponse<T> onServerResponse, Type type);

}