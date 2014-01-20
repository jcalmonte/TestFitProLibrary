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
import android.widget.TextView;

import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.communication.UsbComm;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.interpreter.SystemStatusCallback;

import java.nio.ByteBuffer;
import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener{

    //UsbComm usbComm;

    private Handler m_handler, m_handlerUi;
    private int m_interval = 1000; // ms of delay
    private int txCount = 0;

    //Layout variables
    private Button buttonDecPeriod100;
    private Button buttonDecPeriod10;
    private Button buttonDecPeriod1;
    private Button buttonIncPeriod1;
    private Button buttonIncPeriod10;
    private Button buttonIncPeriod100;
    private Button buttonClearTxCount;

    private TextView textViewPeriod;
    private TextView textViewTxCount;
    private TextView textViewRxCount;
    private TextView textViewPerSecond;

    private Calendar timeWhenCleared;

    private FecpController fecpController;
    //private SystemStatusCallback systemStatusCallback;

    /**
     * onCreate
     * Called when the app is created.
     * @param savedInstanceState Bundle item
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
            fecpController.initializeConnection(CmdHandlerType.FIFO_PRIORITY);//todo change as needed
        }catch (Exception ex){
            Log.e("Device Info fail", ex.getMessage());
        }
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
    }

}
