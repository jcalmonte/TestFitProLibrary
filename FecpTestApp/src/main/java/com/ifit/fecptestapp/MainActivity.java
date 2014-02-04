/**
 * Main Activity of the test app
 * @author Ryan.Tensmeyer
 * @date 12/23/13
 * @version 1
 * Release Date
 * @date 12/31/13
 **/

package com.ifit.fecptestapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteDataCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

import java.nio.ByteBuffer;
import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener{

    //UsbComm usbComm;

    private Handler m_handler, m_handlerUi;
    private int m_interval = 1000; // ms of delay
    private int txCount = 0;
    private double mSpeedMph = 0;
    private double mSpeedMphPrev = 0;
    private final double mMaxSpeed = 12.0f;

    //Layout variables
    private Button buttonDecPeriod100;
    private Button buttonDecPeriod10;
    private Button buttonDecPeriod1;
    private Button buttonIncPeriod1;
    private Button buttonIncPeriod10;
    private Button buttonIncPeriod100;
    private Button buttonClearTxCount;

    private Button buttonSpeedDec;
    private Button buttonSpeed1;
    private Button buttonSpeed2;
    private Button buttonSpeed3;
    private Button buttonSpeed4;
    private Button buttonSpeed5;
    private Button buttonSpeed6;
    private Button buttonSpeed7;
    private Button buttonSpeed8;
    private Button buttonSpeed9;
    private Button buttonSpeed10;
    private Button buttonSpeed11;
    private Button buttonSpeed12;
    private Button buttonSpeedInc;

    private TextView textViewSpeed;

    private TextView textViewPeriod;
    private TextView mainDeviceTextView;
    private TextView textViewTxCount;
    private TextView textViewRxCount;
    private TextView textViewPerSecond;
    private TextView deviceInfoText;
    private ListView deviceList;

    private Calendar timeWhenCleared;

    private FecpController fecpController;
    private FecpCommand tempCommand;
    private SystemDevice MainDevice;
    private Device tempDevice;
    //private SystemStatusCallback systemStatusCallback;

    /**
     * onCreate
     * Called when the app is created.
     * @param savedInstanceState Bundle item
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String devInfoStr = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //usbComm = new UsbComm(MainActivity.this);

        initLayout();

        m_handler = new Handler();
        m_sendUsbData.run();

        m_handlerUi = new Handler();
        m_updateUi.run();

        //systemStatusCallback =
        try{
            fecpController = new FecpController(MainActivity.this, getIntent(), CommType.USB_COMMUNICATION, null);
            MainDevice = fecpController.initializeConnection(CmdHandlerType.FIFO_PRIORITY);//todo change as needed

            ((WriteReadDataCmd)MainDevice.getCommand(CommandId.WRITE_READ_DATA)).addWriteData(BitFieldId.KPH, 0);
            tempCommand = new FecpCommand(MainDevice, MainDevice.getCommand(CommandId.WRITE_READ_DATA), null);
        }catch (Exception ex){
            Log.e("Device Info fail", ex.getMessage());
        }
//        //debug add a command
//        mainDeviceTextView.setText("Main Device-" + MainDevice.getInfo().getDevId().getDescription()
//                + " version-" +MainDevice.getInfo().getSWVersion()
//                +  " subDevice count-" +MainDevice.getSubDeviceList().size());
        //populate the listview of devices or bitfields
        mainDeviceTextView.setText(MainDevice.toString());


        for(Device tempDev : MainDevice.getSubDeviceList())
        {
            devInfoStr += tempDev.toString() +"\n";
        }
        deviceInfoText.setText(devInfoStr);

    }

    /**
     * onCreateOptionsMenu
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu Menu item
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item which menu item was selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * onClick
     * Handles button pressed
     * @param view which button was pressed
     */
    @Override
    public void onClick(View view) {

        //check if it is the listview
        if(view == deviceList)
        {
            //load data into the deviceInfoText
            return;
        }

        if(view == buttonDecPeriod100 && m_interval >= 100){
            m_interval -= 100;
        }else if(view == buttonDecPeriod10 && m_interval >= 10){
            m_interval -= 10;
        }else if(view == buttonDecPeriod1 && m_interval >= 1){
            m_interval -= 1;
        }else if(view == buttonIncPeriod1){
            m_interval += 1;
        }else if(view == buttonIncPeriod10){
            m_interval += 10;
        }else if(view == buttonIncPeriod100){
            m_interval += 100;
        }else if(view == buttonClearTxCount){
            txCount = 0;
            //usbComm.clear_usb_counters();
            timeWhenCleared = Calendar.getInstance();
        }else if(view == buttonSpeedDec){
            if(mSpeedMph > .6){
                mSpeedMph -= .1;
            }else{
                mSpeedMph = 0;
            }
        }else if(view == buttonSpeed1){
            mSpeedMph = 1.0f;
        }else if(view == buttonSpeed2){
            mSpeedMph = 2.0f;
        }else if(view == buttonSpeed3){
            mSpeedMph = 3.0f;
        }else if(view == buttonSpeed4){
            mSpeedMph = 4.0f;
        }else if(view == buttonSpeed5){
            mSpeedMph = 5.0f;
        }else if(view == buttonSpeed6){
            mSpeedMph = 6.0f;
        }else if(view == buttonSpeed7){
            mSpeedMph = 7.0f;
        }else if(view == buttonSpeed8){
            mSpeedMph = 8.0f;
        }else if(view == buttonSpeed9){
            mSpeedMph = 9.0f;
        }else if(view == buttonSpeed10){
            mSpeedMph = 10.0f;
        }else if(view == buttonSpeed11){
            mSpeedMph = 11.0f;
        }else if(view == buttonSpeed12){
            mSpeedMph = 12.0f;
        }else if(view == buttonSpeedInc){
            mSpeedMph += .1;
            if(mSpeedMph > mMaxSpeed){
                mSpeedMph = mMaxSpeed;
            }
        }

        if(m_interval < 0)
            m_interval = 0;
        textViewPeriod.setText("TX Period: " + m_interval + " ms");
    }

    /**
     * PlaceholderFragment
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    /**
     * m_sendUsbData
     * This routine transmits data periodically according the value of 'm_interval'.
     * The initial value of 'm_interval' is 1000 which means that a packet will be sent every 1000
     * milliseconds. The value of 'm_interval' can be modified by using the GUI buttons.
     */
    Runnable m_sendUsbData = new Runnable()
    {

        @Override
        public void run() {
            ByteBuffer buff = ByteBuffer.allocate(64);
            for(int i = 20; i < 64; i++)
                buff.put(i, (byte)i);
            //usbComm.sendCmdBuffer(buff);
            txCount++;

            if(mSpeedMph != mSpeedMphPrev){
                textViewSpeed.setText("Speed: " + String.format("%.2f", mSpeedMph) + " MPH");
                try {

                    ((WriteReadDataCmd)MainDevice.getCommand(CommandId.WRITE_READ_DATA)).addWriteData(BitFieldId.KPH, mSpeedMph);
                    //fecpController.addCmd(tempCommand);
                    fecpController.sendCommand(tempCommand);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mSpeedMphPrev = mSpeedMph;

            m_handler.postDelayed(m_sendUsbData, m_interval);
        }
    };

    /**
     * m_updateUi
     * Updates the GUI.
     */
    Runnable m_updateUi = new Runnable()
    {
        int m_intervalUi = 100;
        private Calendar timeNow;
        long diff;
        float perSecond;
        long now, then;

        @Override
        public void run() {
            textViewTxCount.setText("TX Count: " + txCount);
            //textViewRxCount.setText("RX Count: " + usbComm.getEp1_RX_Count());

            timeNow = Calendar.getInstance();
            now = timeNow.getTimeInMillis();
            then = timeWhenCleared.getTimeInMillis();
            diff = now - then;
            perSecond = (float) ((1000.0 * txCount) / diff);
            textViewPerSecond.setText("TX/RX per second: " + perSecond);

            m_handlerUi.postDelayed(m_updateUi, m_intervalUi);
        }
    };

    /**
     * initLayout
     * Initialize the GUI elements
     */
    private void initLayout(){
        timeWhenCleared = Calendar.getInstance();

        buttonDecPeriod100 = (Button) findViewById(R.id.buttonDec100ms);
        buttonDecPeriod100.setOnClickListener(this);
        buttonDecPeriod10 = (Button) findViewById(R.id.buttonDec10ms);
        buttonDecPeriod10.setOnClickListener(this);
        buttonDecPeriod1 = (Button) findViewById(R.id.buttonDec1ms);
        buttonDecPeriod1.setOnClickListener(this);
        buttonIncPeriod1 = (Button) findViewById(R.id.buttonInc1ms);
        buttonIncPeriod1.setOnClickListener(this);
        buttonIncPeriod10 = (Button) findViewById(R.id.buttonInc10ms);
        buttonIncPeriod10.setOnClickListener(this);
        buttonIncPeriod100 = (Button) findViewById(R.id.buttonInc100ms);
        buttonIncPeriod100.setOnClickListener(this);
        buttonClearTxCount = (Button) findViewById(R.id.buttonClearTxCount);
        buttonClearTxCount.setOnClickListener(this);

        textViewPeriod = (TextView) findViewById(R.id.textViewPeriodMs);
        textViewTxCount = (TextView) findViewById(R.id.textViewTxCount);
        textViewRxCount = (TextView) findViewById(R.id.textViewRxCount);
        textViewPerSecond = (TextView) findViewById(R.id.textViewPerSecond);
        mainDeviceTextView = (TextView) findViewById(R.id.textView);
        deviceInfoText = (TextView)findViewById(R.id.deviceInfoTextView);
        //deviceList = (ListView) findViewById(R.id.deviceListView);

        buttonSpeedDec = (Button) findViewById(R.id.buttonSpeedDec);
        buttonSpeedDec.setOnClickListener(this);
        buttonSpeed1 = (Button) findViewById(R.id.buttonSpeed1);
        buttonSpeed1.setOnClickListener(this);
        buttonSpeed2 = (Button) findViewById(R.id.buttonSpeed2);
        buttonSpeed2.setOnClickListener(this);
        buttonSpeed3 = (Button) findViewById(R.id.buttonSpeed3);
        buttonSpeed3.setOnClickListener(this);
        buttonSpeed4 = (Button) findViewById(R.id.buttonSpeed4);
        buttonSpeed4.setOnClickListener(this);
        buttonSpeed5 = (Button) findViewById(R.id.buttonSpeed5);
        buttonSpeed5.setOnClickListener(this);
        buttonSpeed6 = (Button) findViewById(R.id.buttonSpeed6);
        buttonSpeed6.setOnClickListener(this);
        buttonSpeed7 = (Button) findViewById(R.id.buttonSpeed7);
        buttonSpeed7.setOnClickListener(this);
        buttonSpeed8 = (Button) findViewById(R.id.buttonSpeed8);
        buttonSpeed8.setOnClickListener(this);
        buttonSpeed9 = (Button) findViewById(R.id.buttonSpeed9);
        buttonSpeed9.setOnClickListener(this);
        buttonSpeed10 = (Button) findViewById(R.id.buttonSpeed10);
        buttonSpeed10.setOnClickListener(this);
        buttonSpeed11 = (Button) findViewById(R.id.buttonSpeed11);
        buttonSpeed11.setOnClickListener(this);
        buttonSpeed12 = (Button) findViewById(R.id.buttonSpeed12);
        buttonSpeed12.setOnClickListener(this);
        buttonSpeedInc = (Button) findViewById(R.id.buttonSpeedInc);
        buttonSpeedInc.setOnClickListener(this);

        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
    }

}
