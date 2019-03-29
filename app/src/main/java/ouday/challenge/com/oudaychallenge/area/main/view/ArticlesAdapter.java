package ouday.challenge.com.oudaychallenge.area.main.view;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ouday.challenge.com.app_repository.imageLoader.ImageLoader;
import ouday.challenge.com.oudaychallenge.ApplicationContext;
import ouday.challenge.com.oudaychallenge.R;
import ouday.challenge.com.oudaychallenge.area.main.model.Article;


public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private OnArticleClickedListener onArticleClickedListener;
    private List<? extends Article> lstArticles;
    public android.support.v4.app.FragmentManager fragmentManager;
    public Activity activity;

    public ArticlesAdapter(List<? extends Article> lstArticles,
                           Activity activity, android.support.v4.app.FragmentManager fragmentManager,
                           OnArticleClickedListener onArticleClickedListener) {
        this.onArticleClickedListener = onArticleClickedListener;
        this.lstArticles = lstArticles;
        this.fragmentManager = fragmentManager;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ArticlesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v =
                inflater.inflate(R.layout.layout_fragment_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onArticleClickedListener!= null)
                    onArticleClickedListener.onArticleClicked(position, lstArticles.get(position));
            }
        });

        final String name = ((Article)lstArticles.get(position)).getTitle();
        holder.txtHeader.setText(name);
        holder.date.setText(((Article)lstArticles.get(position)).getPublishedDate());

        holder.source.setText(((Article)lstArticles.get(position)).getSource());
        holder.byLine.setText(((Article)lstArticles.get(position)).getByline());


        ApplicationContext.getInstance().getServiceLocator().getService(ImageLoader.class)
                .load(holder.img_article_icon,((Article)lstArticles.get(position)).getImageUrl());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lstArticles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader;
        public TextView date;
        final public ImageView img_article_icon;
        public View layout;
        public TextView source;
        public TextView byLine;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.title);
            img_article_icon = (ImageView)v.findViewById(R.id.img_article_icon);
            date = (TextView)v.findViewById(R.id.date);
            source = (TextView)v.findViewById(R.id.source);
            byLine = (TextView)v.findViewById(R.id.byLine);


        }
    }

    public interface OnArticleClickedListener{
        void onArticleClicked(int position, Article article);
    }

}
