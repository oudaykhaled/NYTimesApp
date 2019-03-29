package ouday.challenge.com.oudaychallenge.network_layer;

import ouday.challenge.com.oudaychallenge.network_layer.news_requester.NewsRequester;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.callbacks.NewsRequesterCallback;

public class DataProvider implements DataProviderContract{

    private static final DataProvider ourInstance = new DataProvider();
    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
    }

    public void requestNews(final NewsRequesterCallback newsRequesterCallback) throws Exception{
        new NewsRequester().requestNews(newsRequesterCallback);
    }


}
