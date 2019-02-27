package android.support.v17.leanback.supportleanbackshowcase.app.smarttv;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.supportleanbackshowcase.R;

public class SearchVideoActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.searchVideoFragment, new SearchVideoFragment(),
                SearchVideoFragment.TAG);
        ft.commit();
    }
}
