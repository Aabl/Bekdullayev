package com.example.abl.bekdullayev.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import com.example.abl.bekdullayev.R;
import com.example.abl.bekdullayev.model.Article;
import com.squareup.picasso.Picasso;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {
    private List<Article> articles;
    private int rowLayout;
    private Context context;
    private ArticleAdapterListener listener;

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout articlesLayout, articlesLayout2;
        TextView articleTitle;
        TextView articleDescription;
        ImageView srcimage;

        ArticleViewHolder(View v) {
            super(v);
            articlesLayout = (LinearLayout) v.findViewById(R.id.articles_layout);
            articlesLayout2 = (LinearLayout) v.findViewById(R.id.articles_layout2);
            articleTitle = (TextView) v.findViewById(R.id.title);
            articleDescription = (TextView) v.findViewById(R.id.description);
            srcimage = (ImageView) v.findViewById(R.id.sourceimage);
        }
    }

    public ArticlesAdapter(Context context, int rowLayout, List<Article> articles, ArticleAdapterListener listener){
        this.context = context;
        this.rowLayout = rowLayout;
        this.articles = articles;
        this.listener = listener;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, final int position) {
        Article article = articles.get(position);
        holder.articleTitle.setText(article.getTitle());
        holder.articleDescription.setText(article.getDescription());
        Picasso.with(context)
                .load(article.getUrlToImage())
                .into(holder.srcimage);
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(ArticleViewHolder holder, final int position) {
        holder.articlesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onArticleRowClicked(position);
            }
        });
        holder.articlesLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onArticleRowClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }
    public interface ArticleAdapterListener {
        void onArticleRowClicked(int position);
    }
}
