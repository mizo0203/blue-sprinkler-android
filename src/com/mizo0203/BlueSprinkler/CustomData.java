
package com.mizo0203.BlueSprinkler;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.example.mytwitter.util.TwitterUtils;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CustomData {

    private String mScreenName;
    private String mName;
    private TextView mButton;
    private long mID;

    public CustomData(long id, String screenName, String name) {
        mID = id;
        mScreenName = screenName;
        mName = name;
    }

    public String getScreenName() {
        return mScreenName;
    }

    public String getName() {
        return mName;
    }

    public void buidButton(Button button) {
        mButton = button;
        if (TwitterUtils.isFriend(mID)) {
            mButton.setText("フォロー済み");
            mButton.setEnabled(false);
            mButton.setOnClickListener(null);
        } else {
            mButton.setText("フォローする");
            mButton.setEnabled(true);
            mButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mButton.setText("処理中");
                    mButton.setEnabled(false);
                    mButton.setOnClickListener(null);
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
                            try {
                                Twitter twitter =
                                        TwitterUtils.getTwitterInstance(mButton.getContext());
                                return twitter.createFriendship(mID) != null;
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                            return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean sucsess) {
                            mButton.setText("フォロー済み");
                        }
                    }.execute();
                }
            });
        }

    }

}
