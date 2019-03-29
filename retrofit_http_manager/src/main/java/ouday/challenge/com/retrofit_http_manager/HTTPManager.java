package ouday.challenge.com.retrofit_http_manager;

import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import ouday.challenge.com.app_repository.httpManager.FileToUpload;
import ouday.challenge.com.app_repository.httpManager.HttpListener;
import ouday.challenge.com.app_repository.httpManager.OnServerRawResponse;
import ouday.challenge.com.app_repository.httpManager.OnServerResponse;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Destroyable;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import ouday.challenge.com.app_repository.httpManager.HttpManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is an implementation for com.HttpManager
 * to be used for all HTTP Request in the application
 */
public class HTTPManager implements ouday.challenge.com.app_repository.httpManager.HttpManager {

    Gson gson = new Gson();

    HashMap<String, Call> hmCalls = new HashMap<>();

    private HttpListener httpListener;

    private Application application;


    public HTTPManager init(Application application) {
        this.application = application;
        return this;
    }

    public void setHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }

    private String notifyOnPostExecute(String url, String response) {
        if (httpListener != null)
            return httpListener.onPostExecuting(url, response);
        return null;
    }

    private String notifyOnPreExecute(String url, Serializable headBundle, Serializable bodyBundle) {
        if (httpListener != null)
            return httpListener.onPreExecuting(url);
        return null;
    }

    //region HTTP POST
    @Override
    public String sendHttpPostWithRawResponse(String requestCode, String url, Serializable headBundle, Object bodyBundle) throws IOException {
        String bodyJson = bodyBundle instanceof String ? (String) bodyBundle : new Gson().toJson(bodyBundle);
//        String headerJson = headBundle instanceof String ?  (String)headBundle : new Gson().toJson(headBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().postHTTP(url, bodyJson, (Map<String, String>) headBundle);
        trackCall(requestCode, call);
        return call.execute().body();
    }

    @Override
    public <T> T sendHttpPost(String requestCode, String url, Serializable headBundle, Object bodyBundle, Type type) throws IOException {
        String response = sendHttpPostWithRawResponse(requestCode, url, headBundle, bodyBundle);
        T obj = new Gson().fromJson(response, type);
        return obj;
    }

    @Override
    public void sendHttpPostWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerRawResponse onServerRawResponse) {
        String bodyJson = bodyBundle instanceof String ? (String) bodyBundle : new Gson().toJson(bodyBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().postHTTP(url, bodyJson, (Map<String, String>) headBundle);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        onServerRawResponse.onSuccess(HTTPManager.this, (String) response.body());
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerRawResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }

    @Override
    public <T> void sendHttpPostAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerResponse<T> onServerResponse, final Type type) {
        String bodyJson = bodyBundle instanceof String ? (String) bodyBundle : new Gson().toJson(bodyBundle);
        String headerJson = headBundle instanceof String ? (String) headBundle : new Gson().toJson(headBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().postHTTP(url, bodyJson, (Map<String, String>) headBundle);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        T obj = new Gson().fromJson((String) response.body(), type);
                        onServerResponse.onSuccess(HTTPManager.this, obj);
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }


    //endregion

    //region HTTP GET
    @Override
    public String sendHttpGetWithRawResponse(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit) throws Exception {

        String responseCustom = notifyOnPreExecute(url, headBundle, bodyBundle);
        if (responseCustom != null) {
            return responseCustom;
        }
        Call<String> call = ApiClient.getApiClient(application, url, cacheDurationUnit, cacheDuration).getApiService().getHTTP(url);
        String response = call.execute().body();
        notifyOnPostExecute(url, response);
        trackCall(requestCode, call);
        return response;
    }

    @Override
    public <T> T sendHttpGet(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, Type type, int cacheDuration, TimeUnit cacheDurationUnit) throws Exception {

        String responseCustom = notifyOnPreExecute(url, headBundle, bodyBundle);
        if (responseCustom != null) {
            T obj = new Gson().fromJson(responseCustom, type);
            return obj;
        }

        String response = sendHttpGetWithRawResponse(requestCode, url, headBundle, bodyBundle, cacheDuration, cacheDurationUnit);
        T obj = new Gson().fromJson(response, type);
        notifyOnPostExecute(url, response);
        return obj;
    }

    @Override
    public void sendHttpGetWithRawResponseAsync(String requestCode, final String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit, final OnServerRawResponse onServerRawResponse) throws Exception {

        String responseCustom = notifyOnPreExecute(url, headBundle, bodyBundle);
        if (responseCustom != null) {
            onServerRawResponse.onSuccess(HTTPManager.this, responseCustom);
            return;
        }

        Call<String> call = ApiClient.getApiClient(application, url, cacheDurationUnit, cacheDuration).getApiService().getHTTP(url);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        notifyOnPostExecute(url, (String) response.body());
                        onServerRawResponse.onSuccess(HTTPManager.this, (String) response.body());
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerRawResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }

    @Override
    public <T> void sendHttpGetAsync(String requestCode, final String url, Serializable headBundle, Serializable bodyBundle, int cacheDuration, TimeUnit cacheDurationUnit, final OnServerResponse<T> onServerResponse, final Type type) throws Exception {

        String responseCustom = notifyOnPreExecute(url, headBundle, bodyBundle);
        if (responseCustom != null) {
            T obj = new Gson().fromJson(responseCustom, type);
            onServerResponse.onSuccess(HTTPManager.this, obj);
            return;
        }

        Call<String> call = ApiClient.getApiClient(application, url, cacheDurationUnit, cacheDuration).getApiService().getHTTP(url);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        T obj = new Gson().fromJson((String) response.body(), type);
                        notifyOnPostExecute(url, (String) response.body());
                        onServerResponse.onSuccess(HTTPManager.this, obj);
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
//                call.cancel();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
//                call.cancel();

            }
        });
        trackCall(requestCode, call);
    }

    @Override
    public String sendHttpGetWithRawResponse(String requestCode, String url, Serializable headBundle, Serializable bodyBundle) throws Exception {
        return sendHttpGetWithRawResponse(requestCode, url, headBundle, bodyBundle, 0, TimeUnit.SECONDS);
    }

    @Override
    public <T> T sendHttpGet(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, Type type) throws Exception {
        return sendHttpGet(requestCode, url, headBundle, bodyBundle, type, 0, TimeUnit.SECONDS);
    }

    @Override
    public void sendHttpGetWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, OnServerRawResponse onServerRawResponse) throws Exception {
        sendHttpGetWithRawResponseAsync(requestCode, url, headBundle, bodyBundle, 0, TimeUnit.SECONDS, onServerRawResponse);
    }

    @Override
    public <T> void sendHttpGetAsync(String requestCode, String url, Serializable headBundle, Serializable bodyBundle, OnServerResponse<T> onServerResponse, Type type) throws Exception {
        sendHttpGetAsync(requestCode, url, headBundle, bodyBundle, 0, TimeUnit.SECONDS, onServerResponse, type);
    }

    //endregion

    //region HTTP DELETE
    @Override
    public String sendHttpDeleteWithRawResponse(String requestCode, String url) throws IOException {
        Call<String> call = ApiClient.getApiClient().getApiService().deleteHTTP(url);
        return call.execute().body();
    }

    @Override
    public <T> T sendHttpDelete(String requestCode, String url, Type type) throws IOException {

        String response = sendHttpDeleteWithRawResponse(requestCode, url);
        T obj = new Gson().fromJson(response, type);
        return obj;
    }

    @Override
    public void sendHttpDeleteWithRawResponseAsync(String requestCode, String url, final OnServerRawResponse onServerRawResponse) {

        Call<String> call = ApiClient.getApiClient().getApiService().deleteHTTP(url);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        onServerRawResponse.onSuccess(HTTPManager.this, (String) response.body());
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerRawResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }

    @Override
    public <T> void sendHttpDeleteAsync(String requestCode, String url, final OnServerResponse<T> onServerResponse, final Type type) {

//        String bodyJson = new Gson().toJson(bodyBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().deleteHTTP(url);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        T obj = new Gson().fromJson((String) response.body(), type);
                        onServerResponse.onSuccess(HTTPManager.this, obj);
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }
    //endregion

    //region HTTP PUT
    @Override
    public String sendHttpPutWithRawResponse(String requestCode, String url, Serializable headBundle, Object bodyBundle) throws IOException {

        String bodyJson = new Gson().toJson(bodyBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().putHTTP(url, bodyJson);
        trackCall(requestCode, call);
        return call.execute().body();
    }

    @Override
    public <T> T sendHttpPut(String requestCode, String url, Serializable headBundle, Object bodyBundle, Type type) throws IOException {

        String response = sendHttpPutWithRawResponse(requestCode, url, headBundle, bodyBundle);
        T obj = new Gson().fromJson(response, type);
        return obj;
    }

    @Override
    public void sendHttpPutWithRawResponseAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerRawResponse onServerRawResponse) {

        String bodyJson = new Gson().toJson(bodyBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().putHTTP(url, bodyJson);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        onServerRawResponse.onSuccess(HTTPManager.this, (String) response.body());
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerRawResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }

    @Override
    public <T> void sendHttpPutAsync(String requestCode, String url, Serializable headBundle, Object bodyBundle, final OnServerResponse<T> onServerResponse, final Type type) {

        String bodyJson = new Gson().toJson(bodyBundle);
        Call<String> call = ApiClient.getApiClient().getApiService().putHTTP(url, bodyJson);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        T obj = new Gson().fromJson((String) response.body(), type);
                        onServerResponse.onSuccess(HTTPManager.this, obj);
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }


    private void trackCall(String requestCode, Call<String> call) {

        if (requestCode == null || requestCode.equalsIgnoreCase(""))
            return;
        hmCalls.put(requestCode, call);
    }

    @Override
    public boolean cancelRequestByRequestCode(String requestCode) {
        if (!hmCalls.containsKey(requestCode)) return false;
        if (hmCalls.get(requestCode) == null) return false;
        hmCalls.get(requestCode).cancel();
        return true;
    }


    @Override
    public String encode(String uriString) {
        if (TextUtils.isEmpty(uriString)) {
//            Assert.fail("Uri string cannot be empty!");
            return uriString;
        }
        // getQueryParameterNames is not exist then cannot iterate on queries
        if (Build.VERSION.SDK_INT < 11) {
            return uriString;
        }

        // Check if uri has valid characters
        // See https://tools.ietf.org/html/rfc3986
        Pattern allowedUrlCharacters = Pattern.compile("([A-Za-z0-9_.~:/?\\#\\[\\]@!$&'()*+,;" +
                "=-]|%[0-9a-fA-F]{2})+");
        Matcher matcher = allowedUrlCharacters.matcher(uriString);
        String validUri = null;
        if (matcher.find()) {
            validUri = matcher.group();
        }
        if (TextUtils.isEmpty(validUri) || uriString.length() == validUri.length()) {
            return uriString;
        }

        // The uriString is not encoded. Then recreate the uri and encode it this time
        Uri uri = Uri.parse(uriString);
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(uri.getScheme())
                .authority(uri.getAuthority());
        for (String path : uri.getPathSegments()) {
            uriBuilder.appendPath(path);
        }
        for (String key : uri.getQueryParameterNames()) {
            uriBuilder.appendQueryParameter(key, uri.getQueryParameter(key));
        }
        String correctUrl = uriBuilder.build().toString();
        return correctUrl;
    }

    @Override
    public void registerHttpListener(HttpListener httpListener) {
        this.httpListener = httpListener;
    }


    //endregion

    //region PostFormData
    @Override
    public <T> void sendHttpPostFormDataAsync(String requestCode, String url, Map<String, String> params, Map<String, String> headers, final OnServerResponse<T> onServerResponse, final Type type) {
        String headerJson = new Gson().toJson(headers);
        Call<String> call = ApiClient.getApiClient().getApiService().postFormData(url, params, headers);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, Response response) {

                try {
                    if (response.isSuccessful()) {
                        T obj = new Gson().fromJson((String) response.body(), type);
                        onServerResponse.onSuccess(HTTPManager.this, obj);
                    } else {
                        onFailure(call, new Exception(response.message()));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    onFailure(call, new Exception(ex));
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                onServerResponse.onFailed(HTTPManager.this, new Exception(t.getMessage()));
            }
        });
        trackCall(requestCode, call);
    }
    //endregion



    @Override
    public String sendHttpPostMultipartWithRawResponse(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, FileToUpload... files) throws IOException {


        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
            if (part.getValue() instanceof List)
                requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }

        Headers headers = null;
        if (headBundle != null) {
            Map<String, String> headerMap = ((Map<String, String>) headBundle);
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headerMap.keySet()) {
                headerBuilder.add(key, headerMap.get(key));
            }
            headers = headerBuilder.build();
        }

        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()), getBytes(file.getFilePath())));
        MultipartBody req = requestBuilder.build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(req);

        if (headers != null) {
            reqBuilder.headers(headers);
        }

        Request request = reqBuilder.build();
        okhttp3.Call call = client.newCall(request);
        return call.execute().body().string();
    }

    @Override
    public String sendHttpPostMultipartWithRawResponse(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle,
                                                       int connectTimeout, int readTimeout, int writeTimeout, FileToUpload... files) throws IOException {
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (connectTimeout != -1) {
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if (readTimeout != -1) {
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }
        if (writeTimeout != -1) {
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        client = builder.build();

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
            if (part.getValue() instanceof List)
                requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }

        Headers headers = null;
        if (headBundle != null) {
            Map<String, String> headerMap = ((Map<String, String>) headBundle);
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headerMap.keySet()) {
                headerBuilder.add(key, headerMap.get(key));
            }
            headers = headerBuilder.build();
        }

        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()), getBytes(file.getFilePath())));
        MultipartBody req = requestBuilder.build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(req);

        if (headers != null) {
            reqBuilder.headers(headers);
        }

        Request request = reqBuilder.build();
        okhttp3.Call call = client.newCall(request);
        return call.execute().body().string();
    }

    @Override
    public <T> T sendHttpPostMultipart(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, Type type, FileToUpload... files) throws IOException {
        String response = sendHttpPostMultipartWithRawResponse(requestCode, url, headBundle, bodyBundle, files);
        T obj = new Gson().fromJson(response, type);
        return obj;
    }

    @Override
    public <T> T sendHttpPostMultipart(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle, Type type,int connectTimeout, int readTimeout, int writeTimeout, FileToUpload... files) throws IOException {
        String response = sendHttpPostMultipartWithRawResponse(requestCode, url, headBundle, bodyBundle, connectTimeout,  readTimeout, writeTimeout, files);
        T obj = new Gson().fromJson(response, type);
        return obj;
    }

    @Override
    public void sendHttpPutMultipartWithRawResponseAsync(String requestCode, String url,
                                                         Serializable headBundle,
                                                         HashMap<String, Object> bodyBundle,
                                                         final OnServerRawResponse onServerRawResponse,
                                                         FileToUpload... files) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
//            if (part.getValue() instanceof List)
            requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
//            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }

        Headers headers = null;
        if (headBundle != null) {
            Map<String, String> headerMap = ((Map<String, String>) headBundle);
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headerMap.keySet()) {
                headerBuilder.add(key, headerMap.get(key));
            }
            headers = headerBuilder.build();
        }

        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()), file.getFilePath()));
        MultipartBody req = requestBuilder.build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .put(req);
        if (headers != null) {
            reqBuilder.headers(headers);
        }
        Request request = reqBuilder.build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onServerRawResponse.onFailed(HTTPManager.this, e);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });

            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.isSuccessful()) {
                                onServerRawResponse.onSuccess(HTTPManager.this, response.body().string());
                            } else {
                                onServerRawResponse.onFailed(HTTPManager.this, new Exception(response.message().toString()));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });

            }
        });
    }

    @Override
    public void sendHttpPostMultipartWithRawResponseAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle,
                                                          final OnServerRawResponse onServerRawResponse, FileToUpload... files) {
        OkHttpClient client = new OkHttpClient();
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
//            if (part.getValue() instanceof List)
            requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
//            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }
        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()), file.getFilePath()));

        Headers headers = null;
        if (headBundle != null) {
            Map<String, String> headerMap = ((Map<String, String>) headBundle);
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headerMap.keySet()) {
                headerBuilder.add(key, headerMap.get(key));
            }
            headers = headerBuilder.build();
        }

        MultipartBody req = requestBuilder.build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(req);
        if (headers != null) {
            reqBuilder.headers(headers);
        }
        Request request = reqBuilder.build();

        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onServerRawResponse.onFailed(HTTPManager.this, e);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });

            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.isSuccessful()) {
                                onServerRawResponse.onSuccess(HTTPManager.this, response.body().string());
                            } else {
                                onServerRawResponse.onFailed(HTTPManager.this, new Exception(response.message().toString()));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });


            }
        });
    }
    @Override
    public void sendHttpPostMultipartWithRawResponseAsync(String requestCode, String url, Serializable headBundle, HashMap<String, Object> bodyBundle,int connectTimeout, int readTimeout, int writeTimeout,
                                                          final OnServerRawResponse onServerRawResponse, FileToUpload... files) {
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (connectTimeout != -1) {
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if (readTimeout != -1) {
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }
        if (writeTimeout != -1) {
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        client = builder.build();

        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
//            if (part.getValue() instanceof List)
            requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
//            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }
        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()), file.getFilePath()));

        Headers headers = null;
        if (headBundle != null) {
            Map<String, String> headerMap = ((Map<String, String>) headBundle);
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headerMap.keySet()) {
                headerBuilder.add(key, headerMap.get(key));
            }
            headers = headerBuilder.build();
        }

        MultipartBody req = requestBuilder.build();
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(req);
        if (headers != null) {
            reqBuilder.headers(headers);
        }
        Request request = reqBuilder.build();

        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onServerRawResponse.onFailed(HTTPManager.this, e);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });

            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (response.isSuccessful()) {
                                onServerRawResponse.onSuccess(HTTPManager.this, response.body().string());
                            } else {
                                onServerRawResponse.onFailed(HTTPManager.this, new Exception(response.message().toString()));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerRawResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }
                    }
                });


            }
        });
    }

    private byte[] getBytes(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bytes;
    }

    @Override
    public <T> void sendHttpPostMultipartAsync(String requestCode, String url,
                                               Serializable headBundle, HashMap<String, Object> bodyBundle, final OnServerResponse<T> onServerResponse,
                                               final Type type, FileToUpload... files) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
            if (part.getValue() instanceof List)
                requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }
        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()),
                            getBytes(file.getFilePath())));

        MultipartBody req = requestBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(req)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onServerResponse.onFailed(HTTPManager.this, e);
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (response.isSuccessful()) {
                                T obj = new Gson().fromJson(response.body().string(), type);
                                onServerResponse.onSuccess(HTTPManager.this, obj);
                            } else {
                                onServerResponse.onFailed(HTTPManager.this, new Exception(response.message().toString()));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }


                    }
                });
            }
        });
    }

    @Override
    public <T> void sendHttpPostMultipartAsync(String requestCode, String url,
                                               Serializable headBundle, HashMap<String, Object> bodyBundle, final OnServerResponse<T> onServerResponse,
                                               int connectTimeout, int readTimeout, int writeTimeout, final Type type, FileToUpload... files) {
        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        if (connectTimeout != -1) {
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        if (readTimeout != -1) {
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        }
        if (writeTimeout != -1) {
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        client = builder.build();
        MultipartBody.Builder requestBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Object> part : bodyBundle.entrySet()) {
            if (part.getValue() instanceof List)
                requestBuilder.addFormDataPart(part.getKey(), gson.toJson(part.getValue()));
            else requestBuilder.addFormDataPart(part.getKey(), String.valueOf(part.getValue()));
        }
        for (FileToUpload file : files)
            requestBuilder.addFormDataPart(
                    file.getFileParamName(), file.getFileName(),
                    RequestBody.create(MediaType.parse(file.getMime()),
                            getBytes(file.getFilePath())));

        MultipartBody req = requestBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(req)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, final IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onServerResponse.onFailed(HTTPManager.this, e);
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            if (response.isSuccessful()) {
                                T obj = new Gson().fromJson(response.body().string(), type);
                                onServerResponse.onSuccess(HTTPManager.this, obj);
                            } else {
                                onServerResponse.onFailed(HTTPManager.this, new Exception(response.message().toString()));
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onServerResponse.onFailed(HTTPManager.this, new Exception(ex));
                        }


                    }
                });
            }
        });
    }


    @Override
    public void destroyService() {

    }
}