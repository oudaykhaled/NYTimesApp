package ouday.challenge.com.oudaychallenge.network_layer.news_requester.callbacks;

import ouday.challenge.com.oudaychallenge.network_layer.news_requester.response.NewsResponse;

public interface NewsRequesterCallback {
    void onRequestNewsSuccessfull(NewsResponse newsResponse);
    void onRequestNewsFailed(Exception ex);
}
