package com.example.abl.bekdullayev.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.abl.bekdullayev.R;
import com.example.abl.bekdullayev.adapter.ArticlesAdapter;
import com.example.abl.bekdullayev.model.Article;
import com.example.abl.bekdullayev.model.ArticlesResponse;
import com.example.abl.bekdullayev.model.CachedArticleList;
import com.example.abl.bekdullayev.model.Source;
import com.example.abl.bekdullayev.model.SourcesResponse;
import com.example.abl.bekdullayev.rest.ApiClient;
import com.example.abl.bekdullayev.rest.ApiInterface;
import com.example.abl.bekdullayev.splash.SplashScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ArticlesAdapter.ArticleAdapterListener {

    private final static String sortBy = "";
    private final static String API_KEY = "8872065758894ce7b117943a2c60d011";
    private int counter = 0;
    public List<Article> articles = new ArrayList<>();
    public List<Source> sources = new ArrayList<>();
    //Список для получения +10 новостей
    public List<Article> anotherarticles = new ArrayList<>();
    //Список для кэшированных новостей
    public List<Article> cachedarticles = new ArrayList<>();
    private ArticlesAdapter AAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(MainActivity.this, SplashScreen.class));
        recyclerView = (RecyclerView) findViewById(R.id.articles_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        final LinearLayoutManager ALayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(ALayoutManager);
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        //Попытка вывода новостей из кэша
                        try {
                            cachedarticles = (List<Article>) CachedArticleList.readObject(MainActivity.this, "cache");
                            if (cachedarticles.size()>0){
                                AAdapter= new ArticlesAdapter(getApplicationContext(), R.layout.list_item_article, cachedarticles, MainActivity.this);
                                recyclerView.setAdapter(AAdapter);
                                AAdapter.notifyDataSetChanged();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        GetSources();
                    }
                }
        );
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = ALayoutManager.getChildCount();
                totalItemCount = ALayoutManager.getItemCount();
                pastVisiblesItems = ALayoutManager.findFirstVisibleItemPosition();
                int summary = visibleItemCount + pastVisiblesItems;
                if (isNetworkAvailable()) {
                    if (summary >= ALayoutManager.getItemCount()) loading = false;
                    if (!loading) {
                        if (dy > 0) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                LoadMore();
                                loading = true;
                            }
                        }
                    }
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    public void GetSources() {
        if (isNetworkAvailable()) {
            counter=0;
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SourcesResponse> callsources = apiService.getAvailableSources(API_KEY);
            callsources.enqueue(new Callback<SourcesResponse>() {
                @Override
                public void onResponse(Call<SourcesResponse> call, Response<SourcesResponse> response) {
                    sources = response.body().getSources();
                    QueryNews();
                }

                @Override
                public void onFailure(Call<SourcesResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), R.string.nodata, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),R.string.nodata,Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void QueryNews() {
        if (isNetworkAvailable()) {
            counter=0;
            swipeRefreshLayout.setRefreshing(true);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            //
            Call<ArticlesResponse> call = apiService.getLatestNews(sources.get(counter).getID(), sortBy, API_KEY);
            call.enqueue(new Callback<ArticlesResponse>() {
                @Override
                public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                    articles = response.body().getArticles();
                    AAdapter = new ArticlesAdapter(getApplicationContext(), R.layout.list_item_article, articles, MainActivity.this);
                    recyclerView.setAdapter(AAdapter);
                    AAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    try {
                        CachedArticleList.writeObject(MainActivity.this, "cache", articles);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void LoadMore() {
        if (isNetworkAvailable() && (articles.size() == (visibleItemCount + pastVisiblesItems))) {
            counter++;
            if (counter < sources.size()) {
                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<ArticlesResponse> call = apiService.getLatestNews(sources.get(counter).getID(), sortBy, API_KEY);
                call.enqueue(new Callback<ArticlesResponse>() {
                    @Override
                    public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                        anotherarticles.clear();
                        anotherarticles = response.body().getArticles();
                        if (anotherarticles.size() >= 10) {
                             articles.addAll(anotherarticles);
                             AAdapter.notifyItemRangeInserted(counter * 10, 10);
                            recyclerView.scrollTo(0, visibleItemCount + pastVisiblesItems);

                            Toast.makeText(getApplicationContext(),String.valueOf(articles.size()),Toast.LENGTH_SHORT).show();
                        }
                        else {
                            LoadMore();
                        }
                    }

                    @Override
                    public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), R.string.nodata, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Новостей больше нет", Toast.LENGTH_SHORT).show();
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onRefresh() {
        counter = 0;
        loading = false;
        GetSources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), R.string.action_settings, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleRowClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), Single.class);
        if (isNetworkAvailable()) {
            Article article = articles.get(position);
            articles.set(position, article);
            HashMap<String, String> art = new HashMap<>();
            art.put("title",article.getTitle());
            art.put("image",article.getUrlToImage());
            art.put("url",article.getUrl());
            art.put("author",article.getAuthor());
            art.put("description",article.getDescription());
            art.put("published",article.getPublishedAt());
            intent.putExtra("article", art);
            startActivity(intent);
        }
        else {
            try {
                Article cached = cachedarticles.get(position);
                cachedarticles.set(position, cached);
                HashMap<String, String> cache = new HashMap<>();
                cache.put("title", cached.getTitle());
                cache.put("image", cached.getUrlToImage());
                cache.put("url", cached.getUrl());
                cache.put("author", cached.getAuthor());
                cache.put("description", cached.getDescription());
                cache.put("published", cached.getPublishedAt());
                intent.putExtra("article", cache);
                startActivity(intent);
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),R.string.nodata,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}