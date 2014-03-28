package com.ifit.sparkydevapp.sparkydevapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparkydevapp.sparkydevapp.Connecting.ConnectionActivity;
import com.ifit.sparkydevapp.sparkydevapp.Connecting.ProgressThread;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ItemListActivity extends FragmentActivity
        implements ItemListFragment.Callbacks, SystemStatusCallback {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private ProgressDialog mProgress;
    private FecpController mFecpCntrl;
    private boolean mConnected;
    private ProgressThread mProgressThread;
    private SystemDevice mMainDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        this.mConnected = false;
        //while connecting show progress bar.

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Attempting to Connect");
        mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgress.setIndeterminate(true);
        mProgress.show();
        mProgressThread = new ProgressThread(mProgress);
        mProgressThread.start();

        //Attempt to connect to the Fecp controller
        try {
            this.mFecpCntrl = new FecpController(ItemListActivity.this, getIntent(), CommType.USB_COMMUNICATION, this);
            mMainDevice = this.mFecpCntrl.initializeConnection(CmdHandlerType.FIFO_PRIORITY);
            if(this.mMainDevice.getInfo().getDevId() == DeviceId.NONE)//failed to connect
            {
                Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
                this.mProgressThread.stopProgress();
                //change intent back to the connect main menu
                Intent ConnectionActivity = new Intent(this.getApplicationContext(), ConnectionActivity.class);
                startActivity(ConnectionActivity);
                finish();
            }
            this.mProgressThread.stopProgress();
            Toast.makeText(this,"Connection Successful",Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (findViewById(R.id.item_detail_container) != null) {


            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ItemListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.main_device_fragment))
                    .setActivateOnItemClick(true);
        }

    }

    /**
     * Callback method from {@link ItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        //load the fragment that we want to see. //pass a reference to the Fecp Controller



        // fragment transaction.
        Bundle arguments = new Bundle();
        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, id);
        ItemDetailFragment fragment = new ItemDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();

    }

    /**
     * this method is called when the system is connected.
     */
    @Override
    public void systemConnected() {
        this.mConnected = true;
        mProgressThread.stopProgress();
    }

    /**
     * this method is called when the system is disconnected.
     */
    @Override
    public void systemDisconnected() {
        this.mConnected = false;
        Toast.makeText(this,"Connection Lost",Toast.LENGTH_LONG).show();
    }
}
