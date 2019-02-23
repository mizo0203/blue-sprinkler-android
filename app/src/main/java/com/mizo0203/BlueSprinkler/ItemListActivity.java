
package com.mizo0203.BlueSprinkler;

import com.example.mytwitter.util.TwitterUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

/**
 * An activity representing a list of Items. This activity has different presentations for handset
 * and tablet-size devices. On handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing item details. On tablets, the activity presents
 * the list of items and item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a {@link ItemListFragment} and
 * the item details (if present) is a {@link ItemDetailFragment}.
 * <p/>
 * This activity also implements the required {@link ItemListFragment.Callbacks} interface to listen
 * for item selections.
 */
public class ItemListActivity extends Activity
        implements ItemListFragment.Callbacks {

    @SuppressWarnings("unused")
    private static final String TAG = ItemListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Twitter アカウントを取得する
        if (!TwitterUtils.hasAccessToken()) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_item_list);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks} indicating that the item with the
     * given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        // In single-pane mode, simply start the detail activity
        // for the selected item ID.

        // Intent detailIntent = new Intent(this, ItemDetailActivity.class);
        // detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, id);
        // startActivity(detailIntent);

    }

}
