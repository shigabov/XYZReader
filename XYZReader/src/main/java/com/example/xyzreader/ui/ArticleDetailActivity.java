package com.example.xyzreader.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * Created by Артем on 04.04.2016.
 */
public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = ArticleDetailActivity.class.getName();
    Long mId;
    private Cursor mCursor;
    private Activity mActivity;

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(LOG_TAG,"onCreateLoader");
        return ArticleLoader.newInstanceForItemId(this, mId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(LOG_TAG,"onLoadFinished");

        mCursor = cursor;

        // Select the start ID
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {

                Log.d(LOG_TAG, "Title=" + mCursor.getString(1));

                final ImageView imageView = (ImageView) findViewById(R.id.backdrop);

                Glide.with(this).load(mCursor.getString(ArticleLoader.Query.PHOTO_URL)).centerCrop().into(imageView);

                ((TextView) findViewById(R.id.article_body)).setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));

                ((TextView) findViewById(R.id.article_title)).setText(mCursor.getString(ArticleLoader.Query.TITLE));

                ((TextView) findViewById(R.id.article_author_and_date)).setText( Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()
                                + " by "
                                + mCursor.getString(ArticleLoader.Query.AUTHOR)
                ));

                CollapsingToolbarLayout collapsingToolbar =
                        (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbar.setTitle(mCursor.getString(1));
                break;
            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        Intent intent = getIntent();

        mId = ItemsContract.Items.getItemId(intent.getData());
        mActivity = this;

        Log.d(LOG_TAG, "mId=" + mId);
        getLoaderManager().initLoader(0, null, this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(mActivity)
                        .setType("text/plain")
                        .setText(((TextView) findViewById(R.id.article_body)).getText())
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }
}
