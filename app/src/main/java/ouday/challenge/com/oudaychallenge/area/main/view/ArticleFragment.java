package ouday.challenge.com.oudaychallenge.area.main.view;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import ouday.challenge.com.oudaychallenge.R;
import ouday.challenge.com.oudaychallenge.area.main.presenter.ArticleFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends Fragment implements ArticleFragmentView{


    private static final String BUNDLE_ARTICLE_URL = "BUNDLE_ARTICLE_URL";
    private ArticleFragmentPresenter articleFragmentPresenter;
    private View view;
    private WebView mWebview;
    private String mUrl;

    public static ArticleFragment newInstance(String url){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ARTICLE_URL, url);
        ArticleFragment fragment = new ArticleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_article, null);
        mUrl = getArguments().getString(BUNDLE_ARTICLE_URL);
        initView(view);
        articleFragmentPresenter = new ArticleFragmentPresenter(this);
        return view;
    }

    private void initView(View view) {
        mWebview = view.findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript
        mWebview.setWebViewClient(new WebViewController());
        mWebview.loadUrl(mUrl);
    }

    public class WebViewController extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }



}
