
package com.example.mytwitter.util;

import java.util.ArrayList;

import com.mizo0203.BlueSprinkler.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.content.Context;

public class TwitterUtils {

    private static AccessToken mAccessToken;
    private static ArrayList<Long> mFriendIDList;
    private static Long mMyID;

    /**
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     * 
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(Context context) {
        String consumerKey = context.getString(R.string.twitter_consumer_key);
        String consumerSecret = context.getString(R.string.twitter_consumer_secret);

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        if (hasAccessToken()) {
            twitter.setOAuthAccessToken(getAccessToken());
        }
        return twitter;
    }

    /**
     * アクセストークンを設定する
     * 
     * @param context
     * @param accessToken
     */
    public static void setAccessToken(AccessToken accessToken) {
        mAccessToken = accessToken;
    }

    /**
     * アクセストークンを取得する
     * 
     * @return
     */
    public static AccessToken getAccessToken() {
        return mAccessToken;
    }

    /**
     * アクセストークン設定済みか確認する
     * 
     * @return
     */
    public static boolean hasAccessToken() {
        return getAccessToken() != null;
    }

    public static void addFriend(Long id) {
        if (!mFriendIDList.contains(id)) {
            mFriendIDList.add(id);
        }
    }

    public static void init() {
        mFriendIDList = new ArrayList<Long>();
    }

    public static boolean isFriend(Long id) {
        return mFriendIDList.contains(id);
    }

    public static void setMyID(Long id) {
        mMyID = id;
        addFriend(id);
    }

    public static Long getMyID() {
        return mMyID;
    }

}
