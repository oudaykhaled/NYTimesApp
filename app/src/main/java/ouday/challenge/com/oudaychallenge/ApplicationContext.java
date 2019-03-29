package ouday.challenge.com.oudaychallenge;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import ouday.challenge.com.app_repository.httpManager.HttpManager;
import ouday.challenge.com.app_repository.imageLoader.ImageLoader;
import ouday.challenge.com.oudaychallenge.utils.GlideImageLoader;
import ouday.challenge.com.service_locator.ServiceLocator;

public class ApplicationContext extends MultiDexApplication {

    public ServiceLocator serviceLocator;
    public static ApplicationContext instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prepareServices();
    }

    private void prepareServices() {
        getServiceLocator().map(HttpManager.class, ouday.challenge.com.retrofit_http_manager.HTTPManager.class);
        getServiceLocator().map(ImageLoader.class, GlideImageLoader.class);

        getServiceLocator().getService(HttpManager.class).init(this);
    }

    public static ApplicationContext getInstance(){
        return instance;
    }

    public ServiceLocator getServiceLocator(){
        if (serviceLocator == null) serviceLocator = new ServiceLocator();
        return serviceLocator;
    }

}
