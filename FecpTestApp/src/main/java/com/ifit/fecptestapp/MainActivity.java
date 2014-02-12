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
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener, DialogInterface.OnKeyListener{

    //UsbComm usbComm;

    private Handler m_handler, m_handlerUi;
    private double mSpeedMph = 0;
    private double mSpeedMphPrev = 0;

    //Layout variables
    private TextView textViewMain;
    private TextView textViewMode;
    private TextView textViewData;
    private TextView textViewCurrentSpeed;

    private Button buttonMain;
    private Button buttonTask;//info on all of the tasks
    private Button buttonMode;//toggles which mode we are in
    private EditText editSpeedText;//toggles which mode we are in

    private Calendar timeWhenCleared;

    private FecpController fecpController;
    private FecpCommand tempCommand;
    private FecpCommand infoCommand;//gets info on the whole system
    private SystemDevice MainDevice;
    private HandleInfo handleInfoCmd;
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

        initLayout();

        m_handler = new Handler();
        m_handlerUi = new Handler();
        m_updateUi.run();

        try{
            //create FecpController
            fecpController = new FecpController(MainActivity.this, getIntent(), CommType.USB_COMMUNICATION, null);
            //initialize connection, and get the system Device
            MainDevice = fecpController.initializeConnection(CmdHandlerType.FIFO_PRIORITY);

            //How to create a Callback handler that implements CommandCallback
            handleInfoCmd = new HandleInfo(this, textViewCurrentSpeed);

            //create command by passing the command of the specific device you want to use.
            //NOTE do not create a command, always use FecpCommand. If you use command it can corrupt data.
            //                              command,                                       callback,   timeout, frequency
            infoCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 0, 1000);//every 1 second

            //typecast the command that you want to customize, and add what ever data you want to the specific command
            //we want to read the mode and the Speed
            ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
            ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.KPH);

            // Create a single fire command with no callback
            tempCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            //add the commands to the system
            this.fecpController.addCmd(tempCommand);//does nothing
            this.fecpController.addCmd(infoCommand);//gets speed and mode and calls callback every 1 second

        }catch (Exception ex){
            Log.e("Device Info fail", ex.getLocalizedMessage());
        }

        textViewMain.setText("Main " + MainDevice.getInfo().getDevId().getDescription());

        for(Device tempDev : MainDevice.getSubDeviceList())
        {
            devInfoStr += tempDev.toString() +"\n";
        }
        textViewData.setText(devInfoStr);

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


        if(view == buttonMain )
        {
            //get info on the main Device and display it
        }
        else if(view == buttonTask)
        {

        }
        else if(view == buttonMode )
        {

        }

        if(mSpeedMph != mSpeedMphPrev){
            try {
                ((WriteReadDataCmd)tempCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                fecpController.addCmd(tempCommand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mSpeedMphPrev = mSpeedMph;
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int code, KeyEvent keyEvent) {

        // if keydown and "enter" is pressed
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                && (code == KeyEvent.KEYCODE_ENTER)) {

            if(mSpeedMph != mSpeedMphPrev){
                try {
                    ((WriteReadDataCmd)tempCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                    fecpController.addCmd(tempCommand);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mSpeedMphPrev = mSpeedMph;

            // display a floating message
            //get the value from the text edit box and send it
            return true;

        }

        return false;
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

        buttonMain = (Button) findViewById(R.id.buttonMain);
        buttonMain.setOnClickListener(this);
        buttonTask = (Button) findViewById(R.id.buttonTask);
        buttonTask.setOnClickListener(this);
        buttonMode = (Button) findViewById(R.id.buttonMode);
        buttonMode.setOnClickListener(this);

        editSpeedText = (EditText) findViewById(R.id.editSpeedText);
        editSpeedText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                // if keydown and "enter" is pressed
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER)) {
                    //do something
                    //get the value from the text edit box and send it
                    return true;
                }
                return false;
            }
        });

        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewMain = (TextView) findViewById(R.id.textViewMain);
        textViewMode = (TextView) findViewById(R.id.textViewMode);
        textViewCurrentSpeed = (TextView) findViewById(R.id.textViewCurrentSpeed);

    }

}
