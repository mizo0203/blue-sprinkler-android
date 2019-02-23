
package com.mizo0203.BlueSprinkler;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import com.example.mytwitter.util.TwitterUtils;

import youten.redo.ble.abeacon.ImmediateAlertService;
import youten.redo.ble.util.BleUtil;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A list fragment representing a list of Items. This fragment also supports tablet devices by
 * allowing list items to be given an 'activated' state upon selection. This helps indicate which
 * item is currently being viewed in a {@link ItemDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class ItemListFragment extends ListFragment {

    private static final String TAG = ItemListFragment.class.getSimpleName();

    /**
     * The serialization (saved instance state) Bundle key representing the activated item position.
     * Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item clicks.
     */
    @SuppressWarnings("unused")
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private CustomAdapter adapter;

    private static final int REQUEST_ENABLE_BT = 1;

    // BT
    private BluetoothAdapter mBTAdapter;
    private BluetoothLeAdvertiser mBTAdvertiser;
    private BluetoothGattServer mGattServer;
    private BluetoothLeScanner mBLeScanner;

    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            // Advする際に設定した値と実際に動作させることに成功したSettingsが違うとsettingsInEffectに
            // 有効な値が格納される模様です。設定通りに動かすことに成功した際にはnullが返る模様。
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d(TAG, "onStartSuccess, settingInEffect is null");
            }
        }

        public void onStartFailure(int errorCode) {
            Log.d(TAG, "onStartFailure errorCode=" + errorCode);
        };
    };

    /** アドバタイズパケットのスキャン結果 */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (callbackType != ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                // Should not happen.
                Log.e(TAG, "LE Scan has already started");
                return;
            }
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord == null || scanRecord.getServiceUuids() == null) {
                return;
            }

            for (ParcelUuid uuid : scanRecord.getServiceUuids()) {
                Log.d(TAG, uuid.getUuid().toString());
                Log.d(TAG, "getMostSignificantBits" + uuid.getUuid().getMostSignificantBits());
                Log.d(TAG, "getLeastSignificantBits" + uuid.getUuid().getLeastSignificantBits());

                Long userId = Util.idFromUUID(uuid.getUuid());

                if (userId != null) {

                    new AsyncTask<Long, Void, User>() {
                        @Override
                        protected User doInBackground(Long... params) {
                            try {
                                return mTwitter.showUser(params[0]);
                            } catch (TwitterException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(User user) {
                            if (user != null) {
                                adapter.add(new CustomData(user.getId(), user.getScreenName(), user
                                        .getName()));
                            }
                        }
                    }.execute(userId);

                }

            }

        }
    };

    private Twitter mTwitter;

    /**
     * A callback interface that all activities containing this fragment must implement. This
     * mechanism allows activities to be notified of item selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does nothing. Used only when
     * this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitter = TwitterUtils.getTwitterInstance(getActivity());

        adapter = new CustomAdapter(getActivity(), android.R.id.text1);
        setListAdapter(adapter);

        Util.init();
        init();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        setActivateOnItemClick(true);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.

        // mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);

        // getListView().setItemChecked(position, true);
        Log.d("TAG", "mActivatedPosition: "
                + (getListView().isItemChecked(position) ? "ture" : "false"));
        // adapter.add(new CustomData());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the
     * 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_MULTIPLE
                : ListView.CHOICE_MODE_NONE);

    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    public void onStart() {
        super.onStart();
        startIASAdvertise();
        startLeScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAdvertise();
        stopLeScan();
    }

    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(getActivity())) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        // BT check
        BluetoothManager manager = BleUtil.getManager(getActivity());
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if ((mBTAdapter == null) || (!mBTAdapter.isEnabled())) {
            Toast.makeText(getActivity(), R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    // start Advertise as Immediate Alert Service
    private void startIASAdvertise() {
        if (mBTAdapter == null) {
            return;
        }
        if (mBTAdvertiser == null) {
            mBTAdvertiser = mBTAdapter.getBluetoothLeAdvertiser();
        }
        if (mBTAdvertiser != null) {
            ImmediateAlertService ias = new ImmediateAlertService();
            mGattServer = BleUtil.getManager(getActivity()).openGattServer(getActivity(), ias);
            ias.setupServices(mGattServer);
            Long myID = TwitterUtils.getMyID();
            mBTAdvertiser.startAdvertising(
                    BleUtil.createAdvSettings(true, 0),
                    BleUtil.createFMPAdvertiseData(Util.uuidFromID(myID)),
                    mAdvCallback);
        }
    }

    private void stopAdvertise() {
        if (mGattServer != null) {
            mGattServer.clearServices();
            mGattServer.close();
            mGattServer = null;
        }
        if (mBTAdvertiser != null) {
            mBTAdvertiser.stopAdvertising(mAdvCallback);
            mBTAdvertiser = null;
        }
    }

    // start Advertise as Immediate Alert Service
    private void startLeScan() {
        if (mBTAdapter == null) {
            return;
        }
        if (mBLeScanner == null) {
            mBLeScanner = mBTAdapter.getBluetoothLeScanner();
        }
        if (mBLeScanner != null) {
            mBLeScanner.startScan(mScanCallback);
        }

    }

    private void stopLeScan() {
        if (mBLeScanner != null) {
            mBLeScanner.stopScan(mScanCallback);
            mBLeScanner = null;
        }
    }

}
