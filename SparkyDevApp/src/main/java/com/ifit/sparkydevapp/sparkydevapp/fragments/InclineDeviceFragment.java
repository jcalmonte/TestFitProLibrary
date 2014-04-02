package com.ifit.sparkydevapp.sparkydevapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ifit.sparky.fecp.CommandCallback;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.interpreter.bitField.BitFieldId;
import com.ifit.sparky.fecp.interpreter.bitField.converter.BitfieldDataConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.InclineConverter;
import com.ifit.sparky.fecp.interpreter.bitField.converter.ShortConverter;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.CommandId;
import com.ifit.sparky.fecp.interpreter.command.WriteReadDataCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import com.ifit.sparky.fecp.interpreter.status.WriteReadDataSts;
import com.ifit.sparkydevapp.sparkydevapp.R;

import java.util.Set;
import java.util.TreeMap;


public class InclineDeviceFragment extends BaseInfoFragment implements CommandCallback, Runnable{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "incline_dev_id";
    public static final String DISPLAY_STRING = "Incline Device";

    private Device mInclineDev;

    private FecpCommand mInclineInfoCmd;
    private FecpCommand mInclineCalibrateCmd;

    private TextView mTextViewInclineValues;
    private TextView mTextViewInclineDevice;
    private TextView mTextViewInclineDetails;
    private EditText mEditInclineText;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     *
     * @param fecpCntrl
     */
    public InclineDeviceFragment(FecpController fecpCntrl) {
        super(fecpCntrl, InclineDeviceFragment.DISPLAY_STRING, InclineDeviceFragment.ARG_ITEM_ID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.incline_device, container, false);
        //assign all of the textviews and values we need
        this.mTextViewInclineDevice = ((TextView) rootView.findViewById(R.id.textViewInclineDevice));
        this.mTextViewInclineValues = ((TextView) rootView.findViewById(R.id.textViewInclineValues));
        this.mTextViewInclineDetails = ((TextView) rootView.findViewById(R.id.textViewInclineDetails));
        this.mEditInclineText = ((EditText) rootView.findViewById(R.id.editInclineText));


        this.mInclineDev = this.mFecpCntrl.getSysDev().getSubDevice(DeviceId.INCLINE);
        Set<BitFieldId> supportedBitfields;
        try {

            this.mInclineInfoCmd = new FecpCommand(this.mInclineDev.getCommand(CommandId.WRITE_READ_DATA), this, 0, 1000);//every 1 second

            //check which bitfields are supported
            supportedBitfields = this.mInclineDev.getInfo().getSupportedBitfields();
            if(supportedBitfields.contains(BitFieldId.INCLINE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.INCLINE);
            }

            if(supportedBitfields.contains(BitFieldId.MAX_INCLINE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.MAX_INCLINE);
            }

            if(supportedBitfields.contains(BitFieldId.MIN_INCLINE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.MIN_INCLINE);
            }

            if(supportedBitfields.contains(BitFieldId.TRANS_MAX))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.TRANS_MAX);
            }

            if(supportedBitfields.contains(BitFieldId.ACTUAL_INCLINE))
            {
                ((WriteReadDataCmd)this.mInclineInfoCmd.getCommand()).addReadBitField(BitFieldId.ACTUAL_INCLINE);
            }

            if(this.mInclineDev.getCommandSet().containsKey(CommandId.CALIBRATE)) {
                this.mInclineCalibrateCmd = new FecpCommand(this.mInclineDev.getCommand(CommandId.CALIBRATE), this);//every 1 second
            }
        }
        catch (Exception ex)
        {
            Log.e("Initialize Incline Commands Failed", ex.getLocalizedMessage());
        }

                // Show the dummy content as text in a TextView.
        this.mTextViewInclineDevice.setText(this.mInclineDev.toString());
        this.addFragmentFecpCommands();
        return rootView;
    }


    /**
     * These are the commands that we will be using on the startup
     */
    @Override
    public void addFragmentFecpCommands() {
        try {
            this.mFecpCntrl.addCmd(this.mInclineInfoCmd);
        }
        catch (Exception ex)
        {
            Log.e("Initialize Incline Commands Failed", ex.getLocalizedMessage());
        }
    }

    /**
     * These are the commands that well be removed when we switch fragments
     */
    @Override
    public void deleteFragmentFecpCommands() {

        this.mFecpCntrl.removeCmd(this.mInclineInfoCmd);
    }

    /**
     * Handles the reply from the device
     *
     * @param cmd the command that was sent.
     */
    @Override
    public void msgHandler(Command cmd) {
        //check for the max and min incline

        //((TextView) this.mRootView.findViewById(R.id.textViewInclineDetails)).setText(this.mInclineDev.toString());
        //check for the current incline

        //check for the trans max
        //check for actual incline
        //
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {

        TreeMap<BitFieldId, BitfieldDataConverter> commandData;

        commandData = ((WriteReadDataSts)this.mInclineInfoCmd.getCommand().getStatus()).getResultData();
        String valueString = "Current Incline= %";
        if(commandData.containsKey(BitFieldId.INCLINE))
        {

            try
            {
                valueString += ((InclineConverter) commandData.get(BitFieldId.INCLINE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.ACTUAL_INCLINE))
        {

            try
            {
                valueString += "Actual Incline= %" +((InclineConverter) commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if(commandData.containsKey(BitFieldId.TRANS_MAX))
        {

            try
            {
                valueString += "Trans Max= " +((ShortConverter) commandData.get(BitFieldId.ACTUAL_INCLINE).getData()).getValue() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.MAX_INCLINE))
        {

            try
            {
                valueString += "Trans Max= " +((InclineConverter) commandData.get(BitFieldId.MAX_INCLINE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        if(commandData.containsKey(BitFieldId.MIN_INCLINE))
        {

            try
            {
                valueString += "Trans Max= " +((InclineConverter) commandData.get(BitFieldId.MIN_INCLINE).getData()).getIncline() + "\n";
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }
}
