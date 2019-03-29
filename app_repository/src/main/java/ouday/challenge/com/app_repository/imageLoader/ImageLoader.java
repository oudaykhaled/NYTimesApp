package ouday.challenge.com.app_repository.imageLoader;


import android.support.annotation.IdRes;
import android.widget.ImageView;

import ouday.challenge.com.service_locator.Destroyable;

public interface ImageLoader extends Destroyable {
    void load(ImageView imageView, String url);
    void load(ImageView imageView, String url, @IdRes int resourceID);
}
