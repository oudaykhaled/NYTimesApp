package ouday.challenge.com.oudaychallenge.area.main.view;

import java.util.List;

import ouday.challenge.com.oudaychallenge.area.main.model.Article;

public interface ArticlesListFragmentView {
        void drawList(List<? extends Article> lstArticles);
        void showProgressBar();
        void hideProgressBar();
        void showError(String errorMessage);
    }