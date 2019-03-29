package ouday.challenge.com.oudaychallenge.area.main.view;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import ouday.challenge.com.oudaychallenge.R;
import ouday.challenge.com.oudaychallenge.area.main.model.Article;
import ouday.challenge.com.oudaychallenge.area.main.presenter.ArticlesListFragmentPresenter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticlesListFragment extends Fragment implements ArticlesListFragmentView, ArticlesAdapter.OnArticleClickedListener {

    public RecyclerView recyclerView;
    private ArticlesListFragmentPresenter presenter;
    private ProgressBar progressBar;
    private ArticlesAdapter adapter;

    public ArticlesListFragment() {}

    public static Fragment newInstance() {
        return new ArticlesListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.rvNews);
        presenter = new ArticlesListFragmentPresenter(this);
        initProgressBar();
        presenter.loadNews();
    }

    private void initProgressBar() {
        progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleSmall);
        progressBar.setIndeterminate(true);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels,
                250);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        getActivity().addContentView(progressBar, params);
        showProgressBar();
    }

    @Override
    public void drawList(List<? extends Article> lstArticles) {
        adapter = new ArticlesAdapter(lstArticles, getActivity(), getFragmentManager(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onArticleClicked(int position, Article article) {
        try {
            presenter.onArticleClicked(article);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPresenter(ArticlesListFragmentPresenter presenter) {
        this.presenter = presenter;
    }

    public ArticlesAdapter getAdapter() {
        return adapter;
    }
}
