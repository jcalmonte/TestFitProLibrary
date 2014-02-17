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

public class MainActivity extends Activity implements View.OnClickListener{

    //UsbComm usbComm;

    private Handler m_handler, m_handlerUi;
    private double mSpeedMph = 0;
    private double mSpeedMphPrev = 0;

    //Layout variables
    private TextView textViewMain;
    private TextView textViewMode;
    private TextView textViewData;
    private TextView textViewCurrentSpeed;
    private TextView textViewCpu;

    private Button buttonMain;
    private Button buttonTask;//info on all of the tasks
    private Button buttonMode;//toggles which mode we are in
    private EditText editSpeedText;//toggles which mode we are in

    private Calendar timeWhenCleared;

    private FecpController fecpController;
    private FecpCommand mainCommand;//gets the info for the main command
    private FecpCommand cpuInfoCommand;//gets info for the CPU
    private FecpCommand modeCommand;//toggles the mode
    private FecpCommand taskInfoCmd;//toggles the mode

    private FecpCommand speedCommand;
    private FecpCommand infoCommand;//gets info on the whole system
    private SystemDevice MainDevice;
    private HandleInfo handleInfoCmd;
    private HandleInfo handleCpu;
    private HandleTaskInfo handleTaskCmd;

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

        try{
            //create FecpController
            fecpController = new FecpController(MainActivity.this, getIntent(), CommType.USB_COMMUNICATION, null);
            //initialize connection, and get the system Device
            MainDevice = fecpController.initializeConnection(CmdHandlerType.FIFO_PRIORITY);
            // Create a single fire command with no callback
            initHandlers();
            initFecpCmds();
        }
        catch (Exception ex){
            ex.printStackTrace();
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
            this.fecpController.removeCmd(this.taskInfoCmd);
            this.textViewData.setText(this.MainDevice.toString());//write everything to the main
        }
        else if(view == buttonTask)
        {
            try {
                this.fecpController.addCmd(this.taskInfoCmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(view == buttonMode )
        {
            //set a single command to change the mode.
            this.fecpController.removeCmd(this.taskInfoCmd);
        }
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
                String inputText;
                // if keydown and "enter" is pressed
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER)) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editSpeedText.getText().toString();
                    if(inputText.isEmpty())
                    {
                        return false;//invalid data
                    }
                    try
                    {
                        mSpeedMph = Double.parseDouble(inputText);
                    }
                    catch (NumberFormatException numEx)
                    {
                        numEx.printStackTrace();
                    }
                    if(mSpeedMph != mSpeedMphPrev){
                        try {
                            ((WriteReadDataCmd) speedCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                            fecpController.addCmd(speedCommand);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mSpeedMphPrev = mSpeedMph;
                    return true;
                }
                return false;
            }
        });

        editSpeedText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus)
                {
                    //add command with the speed to the system.
                    if(mSpeedMph != mSpeedMphPrev){
                        try {
                            ((WriteReadDataCmd) speedCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                            fecpController.addCmd(speedCommand);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mSpeedMphPrev = mSpeedMph;
                }

            }
        });
        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewMain = (TextView) findViewById(R.id.textViewMain);
        textViewMode = (TextView) findViewById(R.id.textViewMode);
        textViewCpu = (TextView) findViewById(R.id.textViewCpu);
        textViewCurrentSpeed = (TextView) findViewById(R.id.textViewCurrentSpeed);

    }

    private void initFecpCmds()
    {
        try
        {
        //create command by passing the command of the specific device you want to use.
        //NOTE do not create a command, always use FecpCommand. If you use command it can corrupt data.
        //                              command,                                       callback,   timeout, frequency
        infoCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), handleInfoCmd, 0, 1000);//every 1 second
        this.taskInfoCmd = new FecpCommand(MainDevice.getCommand(CommandId.GET_TASK_INFO),this.handleTaskCmd,0, 100);//rotates through

        speedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
        mainCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));//displays the system

        //update the cpu every 2 seconds
        cpuInfoCommand = new FecpCommand(MainDevice.getCommand(CommandId.GET_SYSTEM_INFO), handleCpu, 0, 2000);

        //typecast the command that you want to customize, and add what ever data you want to the specific command
        //we want to read the mode,Speed,etc..
        ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
        ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.KPH);


            //add the Periodic commands to the system
            this.fecpController.addCmd(infoCommand);//gets speed and mode and calls callback every 1 second
            this.fecpController.addCmd(cpuInfoCommand);//gets speed and mode and calls callback every 2 second
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Log.e("Initialize Commands Failed",ex.getLocalizedMessage());
        }
    }

    private void initHandlers()
    {
        //How to create a Callback handler that implements CommandCallback
        handleInfoCmd = new HandleInfo(this, textViewCurrentSpeed);
        this.handleTaskCmd = new HandleTaskInfo(this, this.textViewData);//upload all the data into the data section.
        this.handleTaskCmd.setNumOfTasks(this.MainDevice.getNumberOfTasks());
        handleCpu = new HandleInfo(this, textViewCpu);
    }
}
