package android.support.v17.leanback.supportleanbackshowcase.app.smarttv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.supportleanbackshowcase.R;
import android.support.v17.leanback.supportleanbackshowcase.app.details.ShadowRowPresenterSelector;
import android.support.v17.leanback.supportleanbackshowcase.app.media.TvMetaData;
import android.support.v17.leanback.supportleanbackshowcase.app.media.VideoExampleActivity;
import android.support.v17.leanback.supportleanbackshowcase.app.media.VideoExampleWithExoPlayerActivity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Live TV fragment embeds a rows fragment.
 */
public class TvLiveFragment extends RowsFragment {
    private final String TAG = "TvLiveFragment";
    private final ArrayObjectAdapter mRowsAdapter;
    private TvListLoader mTvListLoader;

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
        mTvListLoader = new TvListLoader();
        mTvListLoader.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTvListLoader != null) {
            mTvListLoader.cancel(false);
            mTvListLoader = null;
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
