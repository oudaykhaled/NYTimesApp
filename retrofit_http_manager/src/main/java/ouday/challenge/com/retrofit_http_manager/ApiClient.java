package ouday.challenge.com.retrofit_http_manager;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static java.security.AccessController.getContext;

/**
 * Necessary for Retrofit Implementation
 * Contains the HTTP configuration.
 */
public class ApiClient {
    private final Application application;

//    private static ApiClient apiClient;

    private ApiInterface apiService;

    private static HashMap<String, ApiClient> hmApiClient = new HashMap<>();

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR_OFFLINE = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetworkAvailable(application)) {
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached")
                        .build();
            }
            return chain.proceed(request);
        }
    };

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + 5000)
                        .build();
            } else {
                return originalResponse;
            }
        }
    };


    public ApiClient(Application application, Interceptor additionalInterceptor) {
        this.application = application;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(REWRITE_RESPONSE_INTERCEPTOR_OFFLINE);

//        if (additionalInterceptor != null) {
//            clientBuilder.cache(CacheProvider.provideCache(application));
//            clientBuilder.addInterceptor(additionalInterceptor);
//        }

        clientBuilder.readTimeout(60, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.google.com")
//                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiInterface.class);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static ApiClient getApiClient(Application application, String url, TimeUnit timeUnit, int duration) throws Exception {
        ApiClient apiClient;
        String interceptorCode = getInterceptorCode(url, timeUnit, duration);
        if ((apiClient = hmApiClient.get(interceptorCode)) == null) {
            Interceptor interceptor = new InterceptorBuilder()
                    .setApplicationContext(application)
                    .setDuration(duration)
                    .setTimeUnit(timeUnit)
                    .setUrl(url)
                    .build();
            apiClient = new ApiClient(application, interceptor);
            hmApiClient.put(interceptorCode, apiClient);
        }
        Log.d("HASHMAP", String.valueOf(hmApiClient.size()));
        return apiClient;
    }


    public static ApiClient getApiClient() {
        ApiClient apiClient;
        String defaultInterceptorCode = "DEFAULT";
        if ((apiClient = hmApiClient.get(defaultInterceptorCode)) == null) {
            apiClient = new ApiClient(null, null);
            hmApiClient.put(defaultInterceptorCode, apiClient);
        }
        return apiClient;
    }


    public ApiInterface getApiService() {
        return apiService;
    }

    private static Interceptor defaultInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            return null;
        }
    };


    private static String getInterceptorCode(String url, TimeUnit timeUnit, int duration) {
        return url + "##" + timeUnit + "##" + duration;
    }
}