package ouday.challenge.com.retrofit_http_manager;

import android.app.Application;
import android.util.Log;

import java.io.File;

import okhttp3.Cache;


public class CacheProvider {

    public static Cache provideCache(Application application) {
        Cache cache = null;
        try {
            cache = new Cache(new File(application.getCacheDir(), "http-cache"), 10 * 1024 * 1024);
        } catch (Exception e) {
            Log.d("cache exception", e.getMessage());
        }
        return cache;
    }
}