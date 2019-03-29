package ouday.challenge.com.app_repository.imageLoader;

import javax.swing.text.html.ImageView;

public interface ImageLoader {
    void load(ImageView imageView, String url);
    void load(ImageView imageView, String url, int resourceID)
}
