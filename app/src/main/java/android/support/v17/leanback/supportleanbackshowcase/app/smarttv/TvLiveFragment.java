package android.support.v17.leanback.supportleanbackshowcase.app.smarttv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.supportleanbackshowcase.R;
import android.support.v17.leanback.supportleanbackshowcase.app.details.ShadowRowPresenterSelector;
import android.support.v17.leanback.supportleanbackshowcase.app.media.TvMetaData;
import android.support.v17.leanback.supportleanbackshowcase.app.media.VideoExampleActivity;
import android.support.v17.leanback.supportleanbackshowcase.cards.presenters.CardPresenterSelector;
import android.support.v17.leanback.supportleanbackshowcase.models.Card;
import android.support.v17.leanback.supportleanbackshowcase.models.CardRow;
import android.support.v17.leanback.supportleanbackshowcase.utils.CardListRow;
import android.support.v17.leanback.supportleanbackshowcase.utils.Utils;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Live TV fragment embeds a rows fragment.
 */
public class TvLiveFragment extends RowsFragment {
    private final String TAG = "TvLiveFragment";
    private final ArrayObjectAdapter mRowsAdapter;
    private TvListLoader mTvListLoader;
    private TvListOnlineLoader mTvListOnlineLoader;
    private static String TV_SOURCE_URL = "https://raw.githubusercontent.com/hulkxu/leanback-showcase/master/app/src/main/res/raw/livetv.json";

    public TvLiveFragment() {
        mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());

        setAdapter(mRowsAdapter);
        setOnItemViewClickedListener((OnItemViewClickedListener)
                (itemViewHolder, item, rowViewHolder, row) -> {
                    Card itemCard = (Card) item;
                    List<String> videoSources = itemCard.getVideoSources();
                    if (videoSources == null || videoSources.isEmpty()) {
                        return;
                    }

                    TvMetaData metaData = new TvMetaData();
                    metaData.setMediaSourcePath((videoSources.toArray(new String[videoSources.size()])));
                    metaData.setMediaTitle(itemCard.getTitle());
                    metaData.setMediaArtistName(itemCard.getDescription());
                    metaData.setMediaAlbumArtUrl(itemCard.getImageUrl());
                    Intent intent = new Intent(getActivity(), SmartPlayerActivity.class);
                    intent.putExtra(VideoExampleActivity.TAG, metaData);
                    getActivity().startActivity(intent);

                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create");
        super.onCreate(savedInstanceState);
        if (mTvListLoader != null) {
            mTvListLoader.cancel(false);
            mTvListLoader = null;
        }
        if (mTvListOnlineLoader != null) {
            mTvListOnlineLoader.cancel(false);
            mTvListOnlineLoader = null;
        }
        mTvListLoader = new TvListLoader();
        mTvListLoader.execute();
        mTvListOnlineLoader = new TvListOnlineLoader();
        mTvListOnlineLoader.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTvListLoader != null) {
            mTvListLoader.cancel(false);
            mTvListLoader = null;
        }
        if (mTvListOnlineLoader != null) {
            mTvListOnlineLoader.cancel(false);
            mTvListOnlineLoader = null;
        }
    }

    private final class TvListOnlineLoader extends AsyncTask<Void, Void, List<Row>> {

        @Override
        protected List<Row> doInBackground(Void... voids) {
            Log.d(TAG, "load data from github");
            BufferedReader reader = null;
            HttpURLConnection urlConnection = null;
            try {
                java.net.URL url = new java.net.URL(TV_SOURCE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),
                        "utf-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
                List<Row> cardListRows = new ArrayList<>();
                for (CardRow row : rows) {
                    cardListRows.add(createCardRow(row));
                }
                return cardListRows;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                urlConnection.disconnect();
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "JSON feed closed", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<Row> result) {
            Log.d(TAG, "load finish. update UI");
            mRowsAdapter.clear();
            mRowsAdapter.addAll(mRowsAdapter.size(), result);
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }

    }

    private final class TvListLoader extends AsyncTask<Void, Void, List<Row>> {

        @Override
        protected List<Row> doInBackground(Void... voids) {
            Log.d(TAG, "load data from raw json first");
            List<Row> cardListRows = new ArrayList<>();
            String json = Utils.inputStreamToString(getResources().openRawResource(
                    R.raw.livetv));
            CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
            for (CardRow row : rows) {
                cardListRows.add(createCardRow(row));
            }
            return cardListRows;
        }


        @Override
        protected void onPostExecute(List<Row> result) {
            Log.d(TAG, "load finish. update UI");
            mRowsAdapter.clear();
            mRowsAdapter.addAll(mRowsAdapter.size(), result);
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }

    }

    private Row createCardRow(CardRow cardRow) {
        PresenterSelector presenterSelector = new CardPresenterSelector(getActivity());
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        for (Card card : cardRow.getCards()) {
            adapter.add(card);
        }

        HeaderItem headerItem = new HeaderItem(cardRow.getTitle());
        return new CardListRow(headerItem, adapter, cardRow);
    }
}
