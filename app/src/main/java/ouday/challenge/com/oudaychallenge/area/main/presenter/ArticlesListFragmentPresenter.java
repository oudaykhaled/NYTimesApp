package ouday.challenge.com.oudaychallenge.area.main.presenter;

import ouday.challenge.com.oudaychallenge.BaseActivity;
import ouday.challenge.com.oudaychallenge.FragmentLoader;
import ouday.challenge.com.oudaychallenge.R;
import ouday.challenge.com.oudaychallenge.area.main.model.Article;
import ouday.challenge.com.oudaychallenge.area.main.view.ArticleFragment;
import ouday.challenge.com.oudaychallenge.area.main.view.ArticlesListFragmentView;
import ouday.challenge.com.oudaychallenge.network_layer.DataProvider;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.callbacks.NewsRequesterCallback;
import ouday.challenge.com.oudaychallenge.network_layer.news_requester.response.NewsResponse;

public class ArticlesListFragmentPresenter implements NewsRequesterCallback {

    private ArticlesListFragmentView view;

    public ArticlesListFragmentPresenter(ArticlesListFragmentView view) {
        this.view = view;
    }

    public ArticlesListFragmentPresenter loadNews(){
        try {
            DataProvider.getInstance().requestNews(this);
            view.showProgressBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void onRequestNewsSuccessfull(NewsResponse newsResponse) {
        view.drawList(newsResponse.getResults());
        view.hideProgressBar();
    }

    @Override
    public void onRequestNewsFailed(Exception ex) {
        view.hideProgressBar();
        view.showError(BaseActivity.getCurrentActivity().getString(R.string.error_failed_to_fetch_news));
    }


    public void onArticleClicked(Article article) throws Exception {
        if (!(BaseActivity.getCurrentActivity() instanceof FragmentLoader))
            throw new Exception("Activity must implement FragmentLoader interface");
        ((FragmentLoader)BaseActivity.getCurrentActivity()).openFragment(ArticleFragment.newInstance(article.getUrl()));
    }
}
