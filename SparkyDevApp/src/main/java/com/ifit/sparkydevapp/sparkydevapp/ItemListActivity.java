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
import com.ifit.sparky.fecp.error.ErrorEventListener;
import com.ifit.sparky.fecp.error.SystemError;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparkydevapp.sparkydevapp.Connecting.ConnectionActivity;
import com.ifit.sparkydevapp.sparkydevapp.Connecting.ProgressThread;
import com.ifit.sparkydevapp.sparkydevapp.fragments.BaseInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.ErrorFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.InclineDeviceFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.MainDeviceInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.SpeedDeviceFragment;
import com.ifit.sparkydevapp.sparkydevapp.fragments.TaskInfoFragment;
import com.ifit.sparkydevapp.sparkydevapp.listFragments.MainInfoListFragmentControl;

import java.util.ArrayList;

public class ItemListActivity extends FragmentActivity
        implements MainInfoListFragmentControl.Callbacks, SystemStatusCallback, ErrorEventListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private ProgressDialog mProgress;
    private FecpController mFecpCntrl;
    private boolean mConnected;
    private ProgressThread mProgressThread;
    private SystemDevice mMainDevice;
    private ArrayList<BaseInfoFragment> baseInfoFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_twopane);

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
                return;
            }
            this.mProgressThread.stopProgress();
            Toast.makeText(this,"Connection Successful",Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Log.e("Connection Error", ex.getMessage());
            Toast.makeText(this,"Connection Failed",Toast.LENGTH_LONG).show();
            this.mProgressThread.stopProgress();
            //change intent back to the connect main menu
            Intent ConnectionActivity = new Intent(this.getApplicationContext(), ConnectionActivity.class);
            startActivity(ConnectionActivity);
            finish();
            return;
        }

        this.mFecpCntrl.addOnErrorEventListener(this);//notify users of any errors

        //get supported list of item we will be supporting
        this.baseInfoFragments = new ArrayList<BaseInfoFragment>();

        //always support main info, error, task
        baseInfoFragments.add(new MainDeviceInfoFragment(this.mFecpCntrl));
        baseInfoFragments.add(new TaskInfoFragment(this.mFecpCntrl));
        baseInfoFragments.add(new ErrorFragment(this.mFecpCntrl));

        if(this.mMainDevice.containsDevice(DeviceId.INCLINE))
        {
            baseInfoFragments.add(new InclineDeviceFragment(this.mFecpCntrl));
        }
        if(this.mMainDevice.containsDevice(DeviceId.SPEED))
        {
            baseInfoFragments.add(new SpeedDeviceFragment(this.mFecpCntrl));
        }

        //add supported Fragments here.
        if (findViewById(R.id.item_detail_container) != null) {

            ((MainInfoListFragmentControl)getSupportFragmentManager()
                    .findFragmentById(R.id.main_device_fragment))
                    .addSupportedFragments(baseInfoFragments);


            ((MainInfoListFragmentControl) getSupportFragmentManager()
                    .findFragmentById(R.id.main_device_fragment))
                    .setActivateOnItemClick(true);
        }

    }

    /**
     * Callback method from {@link MainInfoListFragmentControl.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

        //load the fragment that we want to see. //pass a reference to the Fecp Controller

        // fragment transaction.
        Bundle arguments = new Bundle();
        BaseInfoFragment currentFrag = null;

        for (BaseInfoFragment baseInfoFragment : this.baseInfoFragments) {
            if(id == baseInfoFragment.getIdString())
            {
                currentFrag = baseInfoFragment;
            }
        }
        if(currentFrag == null)
        {
            currentFrag = new MainDeviceInfoFragment(this.mFecpCntrl);
        }

//        if (id == MainDeviceInfoFragment.ARG_ITEM_ID) {
//            currentFrag = new MainDeviceInfoFragment(this.mFecpCntrl);
//        }
//        else if (id == TaskInfoFragment.ARG_ITEM_ID) {
//            currentFrag = new TaskInfoFragment(this.mFecpCntrl);
//        }
//        else if (id == InclineDeviceFragment.ARG_ITEM_ID) {
//            currentFrag = new InclineDeviceFragment(this.mFecpCntrl);
//        }
//        else if (id == SpeedDeviceFragment.ARG_ITEM_ID) {
//            currentFrag = new SpeedDeviceFragment(this.mFecpCntrl);
//        }
//        else
//        {
//            currentFrag = new MainDeviceInfoFragment(this.mFecpCntrl);
//        }

        arguments.putString(currentFrag.toString(), id);
        currentFrag.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, currentFrag)
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

    /**
     * This will notify anyone that an error has occurred with the system
     *
     * @param error the error that occurred.
     */
    @Override
    public void onErrorEventListener(SystemError error) {
        Toast.makeText(this,error.toString(),Toast.LENGTH_LONG).show();
    }
}
