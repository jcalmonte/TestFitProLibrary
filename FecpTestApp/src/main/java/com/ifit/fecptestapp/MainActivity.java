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
import android.content.Intent;
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

import com.ifit.sparky.fecp.CmdHandlerType;
import com.ifit.sparky.fecp.CommandCallback;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.SystemDevice;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.InclineConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.KeyObjectConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.LongConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ModeConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.SpeedConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.SetTestingKeyCmd;
import com.ifit.sparky.fecp.interpreter.command.SetTestingTachCmd;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.key.KeyCodes;
import com.ifit.sparky.fecp.interpreter.key.KeyObject;
import com.ifit.sparky.fecp.interpreter.status.GetSysInfoSts;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;

import java.util.TreeMap;

public class MainActivity extends Activity implements View.OnClickListener, CommandCallback, Runnable{

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
    private TextView textViewIncline;
    private TextView textViewDistance;
    private TextView textViewUserTime;
    private TextView textViewTabletTime;

    private Button buttonMain;
    private Button buttonTask;//info on all of the tasks
    private Button buttonMode;//toggles which mode we are in
    private Button buttonIncline;
    private Button buttonKeyPress;
    private Button buttonReconnect;
    private Button buttonErrorLogMenu;
    private Button buttonSendStopKey;
    private Button buttonWriteTach;

    private EditText editSpeedText;//toggles which mode we are in
    private EditText editInclineText;//toggles which mode we are in
    private EditText editTextTachNumber;//toggles which mode we are in


    private FecpController fecpController;
    private FecpCommand cpuInfoCommand;//gets info for the CPU
    private FecpCommand modeCommand;//toggles the mode
    private FecpCommand inclineCommand;//Changes the incline
    private FecpCommand taskInfoCmd;//toggles the mode
    private FecpCommand keyInfoCmd;//Gets the info about the current key being pressed
    private FecpCommand sendKeyCmd;
    private FecpCommand sendTachCmd;
    private FecpCommand speedCommand;
    private FecpCommand infoCommand;//gets info on the whole system


    private SystemDevice MainDevice;
    private HandleInfo handleInfoCmd;
    private HandleTaskInfo handleTaskCmd;
    private ConnectionStatus connectionCallback;

    private long startTime;
    private int currentMode;
    private int connectCount;
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
        connectCount = 0;

        initLayout();
        m_handler = new Handler();
        m_handlerUi = new Handler();
        this.connectionCallback = new ConnectionStatus();
        //attempt to connect on startup
        if(this.ConnectToDevice())
        {
            //set button to be invisible
            this.buttonReconnect.setVisibility(View.INVISIBLE);
        }
        else
        {
            //set button to be invisible
            this.buttonReconnect.setVisibility(View.VISIBLE);
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
    public void onClick(View view)
    {
        if(view == buttonMain )
        {
            //get info on the main Device and display it
            this.fecpController.removeCmd(this.taskInfoCmd);
            this.fecpController.removeCmd(this.keyInfoCmd);
            this.textViewData.setText(this.MainDevice.toString());//write everything to the main
        }
        else if(view == buttonTask)
        {
            try {
                this.fecpController.removeCmd(this.keyInfoCmd);
                this.fecpController.addCmd(this.taskInfoCmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(view == buttonMode )
        {
            if(currentMode == 8)
            {
                currentMode = 0;
            }
            else
            {
                currentMode++;
            }
            try
            {
                ((WriteReadDataCmd)this.modeCommand.getCommand()).addWriteData(BitFieldId.WORKOUT_MODE, currentMode);
                this.fecpController.addCmd(this.modeCommand);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            //set a single command to change the mode.
            this.fecpController.removeCmd(this.taskInfoCmd);
            this.fecpController.removeCmd(this.keyInfoCmd);
        }
        else if(view == buttonIncline)
        {
            //set a single command to change the mode.
            this.fecpController.removeCmd(this.taskInfoCmd);
            this.fecpController.removeCmd(this.keyInfoCmd);
        }
        else if(view == buttonKeyPress)
        {
            //set a single command to change the mode.
            this.fecpController.removeCmd(this.taskInfoCmd);
            try
            {
                this.fecpController.addCmd(this.keyInfoCmd);//add
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else if(view == buttonReconnect)
        {

            //attempt to connect on startup
            if(this.ConnectToDevice())
            {
                //set button to be invisible
                this.buttonReconnect.setVisibility(View.INVISIBLE);
            }
            else
            {
                //set button to be invisible
                this.buttonReconnect.setVisibility(View.VISIBLE);

            }
        }
        else if(view == buttonErrorLogMenu)
        {
            //set a single command to change the mode.
            Intent i = new Intent(this, ErrorlogMenuActivity.class);
            startActivity(i);

        }
        else if(view == buttonSendStopKey && this.sendKeyCmd != null)
        {
            //set a single command to change the mode.
            try {
                this.fecpController.addCmd(this.sendKeyCmd);//add command to send

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void msgHandler(Command cmd)
    {
        this.runOnUiThread(new Thread(this));
    }

    @Override
    public void run()
    {
        //update all of the info we need to display
        int tempTime;
        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        this.textViewCpu.setText( " cpu(%"+String.format("%.1f",((GetSysInfoSts)cpuInfoCommand.getCommand().getStatus()).getCpuUse()* 100)+")");

        commandData = ((WriteReadDataSts)this.infoCommand.getCommand().getStatus()).getResultData();

        if(commandData.containsKey(BitFieldId.KPH) && commandData.containsKey(BitFieldId.ACTUAL_KPH))
        {
            try
            {
                this.textViewCurrentSpeed.setText("kph=" +
                        ((SpeedConverter) commandData.get(BitFieldId.KPH).getData()).getSpeed()
                        + "," + ((SpeedConverter) commandData.get(BitFieldId.ACTUAL_KPH).getData()).getSpeed());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else if(commandData.containsKey(BitFieldId.KPH))
        {
            try
            {
                this.textViewCurrentSpeed.setText("kph=" + ((SpeedConverter) commandData.get(BitFieldId.KPH).getData()).getSpeed());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if(commandData.containsKey(BitFieldId.INCLINE))
        {
            try
            {
                this.textViewIncline.setText("%" + ((InclineConverter) commandData.get(BitFieldId.INCLINE).getData()).getIncline());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if(commandData.containsKey(BitFieldId.WORKOUT_MODE))
        {
            try
            {
                this.textViewMode.setText("Mode=" + ((ModeConverter) commandData.get(BitFieldId.WORKOUT_MODE).getData()).getMode().getDescription());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.DISTANCE))
        {
            try
            {
                this.textViewDistance.setText(" Dist=" + ((LongConverter) commandData.get(BitFieldId.DISTANCE).getData()).getValue() + "meters");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.RUNNING_TIME))
        {
            try
            {
                tempTime = ((LongConverter) commandData.get(BitFieldId.RUNNING_TIME).getData()).getValue();

                this.textViewUserTime.setText(" runTime=" + (tempTime/60)+":"+(tempTime%60));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        commandData = ((WriteReadDataSts)this.keyInfoCmd.getCommand().getStatus()).getResultData();

        if(commandData.containsKey(BitFieldId.KEY_OBJECT) && this.keyInfoCmd.getCmdIndexNum() != 0)
        {
            try
            {
                KeyObject tempKey;
                tempKey = ((KeyObjectConverter) commandData.get(BitFieldId.KEY_OBJECT).getData()).getKeyObject();
                this.textViewData.setText(tempKey.toString());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        tempTime = (int)((System.currentTimeMillis() - startTime)/1000);
        this.textViewTabletTime.setText(" tabletStartTime =" + (tempTime/60)+":"+(tempTime%60));
    }

    private boolean ConnectToDevice()
    {
        String devInfoStr = "";
        this.connectCount++;

        try{
            //create FecpController
            fecpController = new FecpController(MainActivity.this, getIntent(), CommType.USB_COMMUNICATION, this.connectionCallback);
            //initialize connection, and get the system Device
            MainDevice = fecpController.initializeConnection(CmdHandlerType.FIFO_PRIORITY);
            if(this.MainDevice.getInfo().getDevId() == DeviceId.NONE)
            {
                //set data notifying that there isn't a device.
                this.textViewData.setText("No USB Device Sorry connect attempt:"+ this.connectCount + ".");
                return false;//connect failed
            }
            // Create a single fire command with no callback
            initHandlers();
            initFecpCmds();
        }
        catch (Exception ex){
            ex.printStackTrace();
            Log.e("Device Info fail", ex.getLocalizedMessage());
            return false;
        }

        textViewMain.setText("Main " + MainDevice.getInfo().getDevId().getDescription());
        startTime = System.currentTimeMillis();

        for(Device tempDev : MainDevice.getSubDeviceList())
        {
            devInfoStr += tempDev.toString() +"\n";
        }
        textViewData.setText(devInfoStr);
        return true;
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

        //initialize buttons
        buttonMain = (Button) findViewById(R.id.buttonMain);
        buttonMain.setOnClickListener(this);
        buttonTask = (Button) findViewById(R.id.buttonTask);
        buttonTask.setOnClickListener(this);
        buttonMode = (Button) findViewById(R.id.buttonMode);
        buttonMode.setOnClickListener(this);
        buttonIncline = (Button) findViewById(R.id.buttonIncline);
        buttonIncline.setOnClickListener(this);
        buttonKeyPress = (Button) findViewById(R.id.buttonKeyPress);
        buttonKeyPress.setOnClickListener(this);
        buttonReconnect = (Button) findViewById(R.id.reconnectButton);
        buttonReconnect.setOnClickListener(this);
        this.buttonReconnect.setVisibility(View.INVISIBLE);

        buttonErrorLogMenu = (Button) findViewById(R.id.buttonErrorMenu);
        buttonErrorLogMenu.setOnClickListener(this);

        buttonSendStopKey = (Button) findViewById(R.id.buttonSendStopKey);
        buttonSendStopKey.setOnClickListener(this);

        buttonWriteTach = (Button) findViewById(R.id.buttonWriteTach);
        buttonWriteTach.setOnClickListener(this);

        //initialize editText items
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

                    try {
                        ((WriteReadDataCmd) speedCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                        fecpController.addCmd(speedCommand);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    try {
                        ((WriteReadDataCmd) speedCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                        fecpController.addCmd(speedCommand);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        editInclineText = (EditText) findViewById(R.id.editInclineText);
        editInclineText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {

                double tempEditTextVal = 0.0;
                String inputText;
                // if keydown and "enter" is pressed
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (code == KeyEvent.KEYCODE_ENTER)) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editInclineText.getText().toString();
                    if(inputText.isEmpty())
                    {
                        return false;//invalid data
                    }
                    try
                    {
                        tempEditTextVal = Double.parseDouble(inputText);
                    }
                    catch (NumberFormatException numEx)
                    {
                        numEx.printStackTrace();
                    }
                        try {
                            ((WriteReadDataCmd) inclineCommand.getCommand()).addWriteData(BitFieldId.INCLINE, tempEditTextVal);
                            fecpController.addCmd(inclineCommand);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    return true;
                }
                return false;
            }
        });

        editInclineText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus)
                {
                    //add command with the speed to the system.
                     try {
                            ((WriteReadDataCmd) inclineCommand.getCommand()).addWriteData(BitFieldId.KPH, mSpeedMph);
                            fecpController.addCmd(inclineCommand);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }

            }
        });

        editTextTachNumber = (EditText) findViewById(R.id.editTextTachNumber);
        editTextTachNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String inputText;
                int tachSpeed = 0;
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //do something
                    //get the value from the text edit box and send it
                    inputText = editTextTachNumber.getText().toString();
                    if(inputText.isEmpty())
                    {
                        return false;//invalid data
                    }
                    try
                    {
                        tachSpeed = Integer.parseInt(inputText);
                    }
                    catch (NumberFormatException numEx)
                    {
                        numEx.printStackTrace();
                    }

                    try {
                        //update sendTachCmd for sending

                        ((SetTestingTachCmd)sendTachCmd.getCommand()).setTachTime((short)tachSpeed);
                        ((SetTestingTachCmd)sendTachCmd.getCommand()).setTachOverride(true);
                        fecpController.addCmd(sendTachCmd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        //initialize textview items
        textViewData = (TextView) findViewById(R.id.textViewData);
        textViewMain = (TextView) findViewById(R.id.textViewMain);
        textViewMode = (TextView) findViewById(R.id.textViewMode);
        textViewCpu = (TextView) findViewById(R.id.textViewCpu);
        textViewIncline = (TextView) findViewById(R.id.textViewIncline);
        textViewCurrentSpeed = (TextView) findViewById(R.id.textViewCurrentSpeed);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewUserTime = (TextView) findViewById(R.id.textViewUserTime);
        textViewTabletTime = (TextView) findViewById(R.id.textViewTabletTime);

    }

    private void initFecpCmds()
    {
        try
        {
            //create command by passing the command of the specific device you want to use.
            //NOTE do not create a command, always use FecpCommand. If you use command it can corrupt data.
            //                              command,                                       callback,   timeout, frequency
            infoCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second
            this.taskInfoCmd = new FecpCommand(MainDevice.getCommand(CommandId.GET_TASK_INFO),this.handleTaskCmd,0, 100);//rotates through
            this.inclineCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            speedCommand = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));
            modeCommand  = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA));

            //update the cpu every 2 seconds
            cpuInfoCommand = new FecpCommand(MainDevice.getCommand(CommandId.GET_SYSTEM_INFO), this, 0, 2000);

            keyInfoCmd = new FecpCommand(MainDevice.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);

            //typecast the command that you want to customize, and add what ever data you want to the specific command
            //we want to read the mode,Speed,etc..
            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.WORKOUT_MODE))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.WORKOUT_MODE);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.KPH))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.KPH);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.ACTUAL_KPH))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.ACTUAL_KPH);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.INCLINE))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.INCLINE);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.DISTANCE))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.DISTANCE);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.RUNNING_TIME))
            {
                ((WriteReadDataCmd)infoCommand.getCommand()).addReadBitField(BitFieldId.RUNNING_TIME);
            }

            if(this.MainDevice.getInfo().getSupportedBitfields().contains(BitFieldId.KEY_OBJECT))
            {
                ((WriteReadDataCmd)keyInfoCmd.getCommand()).addReadBitField(BitFieldId.KEY_OBJECT);
            }

            Device keyPressTemp = this.MainDevice.getSubDevice(DeviceId.KEY_PRESS);
            if(keyPressTemp != null)
            {
                Command writeKeyPressCmd = keyPressTemp.getCommand(CommandId.SET_TESTING_KEY);
                if(writeKeyPressCmd != null)
                {
                    sendKeyCmd = new FecpCommand(writeKeyPressCmd, this);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyCode(KeyCodes.STOP);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setKeyOverride(true);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setTimeHeld(1000);
                    ((SetTestingKeyCmd)sendKeyCmd.getCommand()).setIsSingleClick(true);
                }

            }
            if(this.MainDevice.getCommandSet().containsKey(CommandId.SET_TESTING_TACH))
            {
                sendTachCmd = new FecpCommand(this.MainDevice.getCommand(CommandId.SET_TESTING_TACH), this);
            }

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
       // handleInfoCmd = new HandleInfo(this, textViewCurrentSpeed);
        this.handleTaskCmd = new HandleTaskInfo(this, this.textViewData);//upload all the data into the data section.
        this.handleTaskCmd.setNumOfTasks(this.MainDevice.getNumberOfTasks());
        //handleCpu = new HandleInfo(this, textViewCpu);
    }
}
