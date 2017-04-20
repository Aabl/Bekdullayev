package com.example.abl.bekdullayev.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.abl.bekdullayev.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Single extends AppCompatActivity {
    TextView title, content, date, author, description;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single);
        Intent intent = getIntent();
        HashMap<String, String> article = (HashMap<String, String>) intent.getSerializableExtra("article");

        title = (TextView) findViewById(R.id.ArticleTitle);
        if (article.get("title")!=null) title.setText(article.get("title"));
        iv = (ImageView) findViewById(R.id.ArticleImage);
        if (article.get("image")!=null) {
        Picasso.with(this)
                .load(article.get("image"))
                .into(iv); }
        content = (TextView) findViewById(R.id.ArticleURL);
        if (article.get("url")!=null) {
        content.setText(article.get("url"));
        Linkify.addLinks(content, Linkify.WEB_URLS);}
        date = (TextView) findViewById(R.id.ArticleDate);
        if (article.get("published")!=null) {
            String givendate = article.get("published");
            String newdate = givendate.substring(0, 10);
            String newtime = givendate.substring(11, 19);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfo = new SimpleDateFormat("dd MMMM yyyy");
            try {
                Date d = sdf.parse(newdate);
                givendate = sdfo.format(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date.setText("Опубликовано : " + givendate + " в " + newtime);
        }
        author = (TextView) findViewById(R.id.ArticleAuthor);
        if (article.get("author")!=null)
        author.setText("Автор: "+article.get("author"));
        description = (TextView) findViewById(R.id.ArticleDescription);
        if (article.get("description")!=null)
        description.setText(article.get("description"));
    }
}
