package ouday.challenge.com.retrofit_http_manager;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class InterceptorBuilder {

    private Interceptor interceptor;
    private String url;
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private int duration = 0;
    private Application application;

    private HashMap<String, Interceptor> hmInterceptors = new HashMap<>();

    public Interceptor build() throws Exception {

        if (application == null) throw new Exception("Missing application context");

        Interceptor cashedInterceptor;
        if ((cashedInterceptor = hmInterceptors.get(getID(url, timeUnit, duration))) != null)
            return cashedInterceptor;

        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                try {
                    Request request = chain.request();
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(duration, timeUnit)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();

                    return chain.proceed(request);
                } catch (Exception e) {
                    Request offlineRequest = chain.request().newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                    return chain.proceed(offlineRequest);
                }

//                Request request = chain.request();
//                CacheControl cacheControl = new CacheControl.Builder()
//                        .maxStale(duration, timeUnit)
//                        .build();
//
//                request = request.newBuilder()
//                        .cacheControl(cacheControl)
//                        .build();
//
//                return chain.proceed(request);
            }
        };
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public InterceptorBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public InterceptorBuilder setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public InterceptorBuilder setDuration(int duration) {
        this.duration = duration;
        return this;

    }

    public InterceptorBuilder setApplicationContext(Application application) {
        this.application = application;
        return this;
    }

    public Interceptor findInterceptor(String url, TimeUnit timeUnit, int duration) {
        return hmInterceptors.get(getID(url, timeUnit, duration));
    }

    private void addInterceptor(String url, TimeUnit timeUnit, int duration, Interceptor interceptor) {
        String id = getID(url, timeUnit, duration);
        hmInterceptors.put(id, interceptor);
    }

    private String getID(String url, TimeUnit timeUnit, int duration) {
        return url + "#" + timeUnit + "#" + duration;
    }
}