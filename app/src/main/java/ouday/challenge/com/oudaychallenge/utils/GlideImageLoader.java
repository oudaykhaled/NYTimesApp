package ouday.challenge.com.oudaychallenge.utils;

import android.support.annotation.IdRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ouday.challenge.com.app_repository.imageLoader.ImageLoader;
import ouday.challenge.com.oudaychallenge.BaseActivity;
import ouday.challenge.com.oudaychallenge.R;


public class GlideImageLoader implements ImageLoader {

    private static GlideImageLoader instance;

    public GlideImageLoader(){}

    @Override
    public void load(ImageView imageView, String url) {
        if (BaseActivity.getCurrentActivity() != null && imageView != null)
            Glide.with(BaseActivity.getCurrentActivity()).load(url)
                    .thumbnail(0.5f)
                    .placeholder(R.color.colorAccent)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(imageView);
    }

    @Override
    public void load(ImageView imageView, String url, @IdRes int placeHolderResourceId) {
        try {
            if (BaseActivity.getCurrentActivity() != null && imageView != null)
                Glide.with(BaseActivity.getCurrentActivity()).load(url)
                        .thumbnail(0.5f)
                        .placeholder(placeHolderResourceId)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(imageView);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void destroyService() {

    }
}
