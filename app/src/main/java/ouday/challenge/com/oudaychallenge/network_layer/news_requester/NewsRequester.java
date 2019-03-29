package ouday.challenge.com.oudaychallenge.network_layer.news_requester;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.HashMap;

import ouday.challenge.com.app_repository.httpManager.HttpManager;
import ouday.challenge.com.app_repository.httpManager.OnServerResponse;
import ouday.challenge.com.oudaychallenge.ApplicationContext;
import ouday.challenge.com.oudaychallenge.network_layer.UrlProvider;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.callbacks.NewsRequesterCallback;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.response.NewsResponse;

public class NewsRequester {

    public HttpManager httpManager;

    public NewsRequester(){
        httpManager = ApplicationContext.getInstance().getServiceLocator().getService(HttpManager.class);
    }


    public void requestNews(final NewsRequesterCallback newsRequesterCallback) throws Exception {
        String url = String.format(UrlProvider.REQUEST_NEWS_URL, "7", UrlProvider.API_KEY);
        HashMap<String, String> bodyBundle = new HashMap<>();
        httpManager.sendHttpGetAsync("", url, bodyBundle, bodyBundle, new OnServerResponse<NewsResponse>() {
            @Override
            public void onSuccess(HttpManager httpManager, NewsResponse response) {
                if (newsRequesterCallback == null) return;
                    newsRequesterCallback.onRequestNewsSuccessfull(response);
            }
            @Override
            public void onFailed(HttpManager httpManager, Exception ex) {
                if (newsRequesterCallback == null) return;
                newsRequesterCallback.onRequestNewsFailed(ex);
            }

        }, new TypeToken<NewsResponse>() {
        }.getType());

    }

}
