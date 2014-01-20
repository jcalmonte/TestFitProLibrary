/**
 * Interface for all communication types (e.g. usb, uart, blue tooth).
 * @author Ryan.Tensmeyer
 * @date 12/10/13
 * @version 1
 * Release Date
 * @date 12/10/13
 **/

package com.ifit.sparky.fecp.communication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.ArrayList;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.content.IntentFilter;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.app.Activity;


public class UsbComm extends Activity implements CommInterface {
    private static final String TAG = "USB Host";

    private boolean isInitialized = false;

    //ID Constants
    private final int VENDOR_ID = 8508;
    private final int PRODUCT_ID = 2;

    //Constant variables for locations in Input and Output Data Arrays
    public final int TX_SIZE = 64;
    public final int ENDPOINT_2 = 2;
    public final int ENDPOINT_4 = 4;

    private Context mContext;
    private Intent mIntent;

    private UsbManager mUsbManager;
    private UsbInterface mInterface;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointIntrRead1;
    private UsbEndpoint mEndpointIntrWrite2;
    private UsbEndpoint mEndpointIntrWrite4;

    public boolean attachFailed = false;
    public int timeUntilClose = 2000; 	//2.0 seconds

    ByteBuffer buffer_ep1 = ByteBuffer.allocate(64);
    ByteBuffer buffer_ep3 = ByteBuffer.allocate(64);
    UsbRequest request_ep1 = new UsbRequest();
    UsbRequest request_ep3 = new UsbRequest();

    ArrayList<ByteBuffer> buffList_ep1 = new ArrayList<ByteBuffer>();

    boolean waitingForEp1Data = false;
    boolean waitingForEp3Data = false;

    // Counters
    private long ep1_RX_Count = 0;
    private long ep3_RX_Count = 0;
    private long drop_Count = 0;
    private long delayCount = 10000;	//this timer decrements, set to 10000 initially to give about 10 seconds for things to settle on start up
    private long delayMin = 10000;		//the lowest delayCount has gotten
    private final int COMM_ERROR_CONST = 2000;	//if the communication fails for 2000 ms, we will assume it is broken

    private Handler m_handler;
    private Handler m_handler_local_run;
    private int m_interval_local_run = 0; // ms of delay
    private Handler m_handler_1ms;
    private CommReply replyHandler;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private enum ConnectionState {
        NOT_CONNECTED, CONNECTED, CONNECTION_JUST_DROPPED, CONNECTION_DROPPED
    }
    private ConnectionState connectionState = ConnectionState.NOT_CONNECTED;

    /**
     * UsbComm - constructor
     * @param c - the Context from the main activity
     */
    public UsbComm(Context c, Intent i) {
        mContext = c;
        mIntent = i;
        onCreateUSB();
        onResumeUSB(mIntent);
    }

    /**
     * onCreateUSB
     * This should be called from the Main Activity's onCreate method or from the constructor.
     */
    private void onCreateUSB(){
        isInitialized = false;
        mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);

        //thread_LocalRun.start();

        m_handler = new Handler();
        m_statusChecker.run();

        m_handler_local_run = new Handler();
        m_localRun.run();

        m_handler_1ms = new Handler();
        m_1ms.run();

        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED);

        mContext.registerReceiver(mUsbDisconnect, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onResumeUSB(mIntent);
    }

    /**
     * onResumeUSB
     * @param intent - the Intent from the main activity
     * This should be called from the Main Activity's onResume method.
     */
    public void onResumeUSB(Intent intent){
        Log.d(TAG, "onResumeUSB");
        if(!isInitialized){
            isInitialized = false;
            attachFailed = false;
            mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);

            //Intent intent = getIntent();
            Log.d(TAG, "intent: " + intent);
            String action = intent.getAction();

            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(device == null)
                return;
            Log.d(TAG, "Device: " + device.getDeviceName());

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "Device Attached" );
                setDevice(device);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.d(TAG, "Action: " + action );
                Log.d(TAG, "Device Detached");
                if (mDevice != null && mDevice.equals(device)) {
                    mDevice = null; //setDevice(null);
                }
            } else {
                Log.d(TAG, "No device detected. Exiting.");
                attachFailed = true;
                timeUntilClose = 2000;
            }
        }
    }

    /**
     * sendCmdBuffer
     * Required to be part of the Command Interface
     */
    @Override
    public void sendCmdBuffer(ByteBuffer buff) {
        sendCommand(ENDPOINT_2, buff);
        waitingForEp1Data = true;
    }

    /**
     * getStsBuffer
     * Required to be part of the Command Interface
     */
    @Override
    public ByteBuffer getStsBuffer() {
        ByteBuffer tempBuff = buffList_ep1.get(0);
        buffList_ep1.remove(0);
        return tempBuff;
    }

    @Override
    public void setStsHandler(CommReply handler) {
        replyHandler = handler;
    }

    /**
     * sends the command and waits for the reply to handle the buffer
     *
     * @param buff the command buffer to send
     * @return
     */
    @Override
    public ByteBuffer sendAndRecieveCmd(ByteBuffer buff) {

        return sendAndReceiveCommand(ENDPOINT_2, buff);
    }

    /**
     * m_statusChecker
     * Used to check for the device periodically
     */
    Runnable m_statusChecker = new Runnable()
    {
        int m_interval = 1000; // ms of delay
        @Override
        public void run() {
            if(!isInitialized && mContext != null){
                //if(mDevice == null && context != null){
                check_for_device();
            }
            m_handler.postDelayed(m_statusChecker, m_interval);
        }
    };

    /**
     * m_localRun
     * Used to update communication and status of the USB communication
     */
    Runnable m_localRun = new Runnable()
    {
        @Override
        public void run() {
            if(isInitialized){
                USB_comm();
                m_interval_local_run = 0;
            }else{
                m_interval_local_run = 1000;
            }
            m_handler_local_run.postDelayed(m_localRun, m_interval_local_run);
        }
    };

    /**
     * USB_comm
     * Called periodically from m_localRun
     */
    private void USB_comm() {

        if(mConnection != null){    /* normal operation */

            /* check to see if data has been received */
            if(waitingForEp1Data){
                request_ep1_RX();
            }
            if(waitingForEp3Data){
                request_ep3_RX();
            }

        }else if(connectionState == ConnectionState.CONNECTION_JUST_DROPPED){
            connectionState = ConnectionState.CONNECTION_DROPPED;
        }

        if(connectionState == ConnectionState.CONNECTED && delayCount == 0){	//if there hasn't been communication in COMM_ERROR_CONST ms, try to reestablish
            Log.d(TAG, "detach 1 " + delayCount);
            detach();
            reestablishConnection();
        }

        if(attachFailed && timeUntilClose == 0){
            Log.d(TAG, "detach 2");
            //forceClose = true;
            detach();
            //usbComm = null;
            System.exit(0);
        }else if(attachFailed && timeUntilClose > 0){
            timeUntilClose--;
        }
    }

    /**
     * request_ep1_RX
     * request received data on endpoint 1
     */
    private void request_ep1_RX(){
        if(request_ep1.queue(buffer_ep1, 64)){
            /* sendCommand(ENDPOINT_2); */
            ep1_RX();
        }
    }

    /**
     * ep1_RX
     * Save data that is received on endpoint 1
     */
    private void ep1_RX() {
        if (mConnection.requestWait() == request_ep1) {
            waitingForEp1Data = false;
            delayCount = COMM_ERROR_CONST;
            ep1_RX_Count++;
            buffList_ep1.add(buffer_ep1);
            while(buffList_ep1.size() > 100)
                buffList_ep1.remove(0);
            ByteBuffer tempBuff = buffList_ep1.get(0);
            buffList_ep1.remove(0);
            replyHandler.stsMsgHandler(tempBuff);
        }
    }

    /**
     * request_ep3_RX
     * request received data on endpoint 3
     */
    private void request_ep3_RX(){
        if(request_ep3.queue(buffer_ep3, 64)){
            //sendCommand(ENDPOINT_4);
            ep3_RX();
        }
    }

    /**
     * ep3_RX
     * Save data that is received on endpoint 3
     */
    private void ep3_RX() {
        if (mConnection.requestWait() == request_ep3) {
            waitingForEp3Data = false;
            ep3_RX_Count++;
        }
    }

    /**
     * sendCommand
     * @param endpoint which endpoint to send data on ENDPOINT2 or ENDPOINT4
     * @param buff the data to send, not to exceed 64 bytes
     */
    private void sendCommand(int endpoint, ByteBuffer buff) {
        synchronized (this) {
            if (mConnection != null) {

                byte[] message = new byte[TX_SIZE];
                for(int i = 0; i < buff.capacity(); i++){
                    message[i] = buff.get(i);
                }

                if(ENDPOINT_2 == endpoint){
                    mConnection.bulkTransfer(mEndpointIntrWrite2, message, message.length, 0);
                }else if(ENDPOINT_4 == endpoint){
                    mConnection.bulkTransfer(mEndpointIntrWrite4, message, message.length, 0);
                }
            }
        }
    }

    /**
     * sendCommand
     * @param endpoint which endpoint to send data on ENDPOINT2 or ENDPOINT4
     * @param buff the data to send, not to exceed 64 bytes
     */
    private ByteBuffer sendAndReceiveCommand(int endpoint, ByteBuffer buff) {
        synchronized (this) {
            if (mConnection != null) {
                ByteBuffer replyBuffer;

                byte[] message = new byte[TX_SIZE];
                for(int i = 0; i < buff.capacity(); i++){
                    message[i] = buff.get(i);
                }

                if(ENDPOINT_2 == endpoint){
                    mConnection.bulkTransfer(mEndpointIntrWrite2, message, message.length, 500);
                }else if(ENDPOINT_4 == endpoint){
                    mConnection.bulkTransfer(mEndpointIntrWrite4, message, message.length, 0);
                }

                try
                {
                    this.mConnection.bulkTransfer(this.mEndpointIntrRead1, message, message.length, 500);

                    replyBuffer = ByteBuffer.allocate(message.length);
                    replyBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    replyBuffer.position(0);
                    replyBuffer.put(message);
                    return replyBuffer;
                }
                catch (Exception ex)
                {

                }


                //after the data is send
                // read the data back
            }
        }
        return null;
    }

    /**
     * m_1ms
     * decrement delay count every millisecond
     */
    Runnable m_1ms = new Runnable()
    {
        @Override
        public void run() {
            if(isInitialized){
                if(delayCount > 0)
                    delayCount--;
                if(delayCount < delayMin)
                    delayMin = delayCount;
            }
            m_handler_1ms.postDelayed(m_1ms, 1);
        }
    };

    /**
     * getDesiredDevice
     * returns the device specified by VENDOR_ID and PRODUCT_ID
     * @return the desired device
     */
    private UsbDevice getDesiredDevice(){
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        try{
            Iterator<UsbDevice> deviceIterator = null;
            if(deviceList != null)
                deviceIterator = deviceList.values().iterator();
            UsbDevice device = null;
            while(deviceIterator != null){
                if(!deviceIterator.hasNext()){
                    deviceList = mUsbManager.getDeviceList();
                    deviceIterator = deviceList.values().iterator();
                }else{
                    device = deviceIterator.next();
                    if(device != null && device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID)
                        break;
                }
            }
            return device;  /* Returns the device that was found, it will be null if no device was found */
        }catch (Exception e){
            e.printStackTrace();
            return null;    /* Returns the device as null because an exception occurred while trying to find the device */
        }
    }

    /**
     * mUsbReceiver
     * This is an alternative way to set the device if it isn't triggered when detected
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            isInitialized = false;
                            setDevice(device);
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    /**
     * setDevice - once a device is discovered and we have permission to communicate with it this method is called to set up all the hooks
     * @param device pass in the device to be configured
     */
    private void setDevice(UsbDevice device) {

        UsbEndpoint mEndpointIntrRead1;
        UsbEndpoint mEndpointIntrRead3;
        UsbEndpoint ep;

        Log.d(TAG, "setDevice " + device);

        isInitialized = false;
        if (device.getInterfaceCount() != 1) {
            Log.e(TAG, "could not find interface");
            return;
        }
        mInterface = device.getInterface(0);
        // device should have 4 endpoints
        Log.d(TAG, "Endpoint count: " + mInterface.getEndpointCount());
        //Toast.makeText(context, "Endpoint count: " + mInterface.getEndpointCount(), Toast.LENGTH_SHORT).show();
        if (mInterface.getEndpointCount() == 0) {
            Log.e(TAG, "could not find endpoint");
            return;
        }
        // endpoint should be of type interrupt
        ep = mInterface.getEndpoint(0);
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
            Log.e(TAG, "Read endpoint 1 is not interrupt type");
            return;
        }

        this.mEndpointIntrRead1 = ep;

        ep = mInterface.getEndpoint(1);
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
            Log.e(TAG, "Write endpoint 2 is not interrupt type");
            return;
        }
        mEndpointIntrWrite2 = ep;

        ep = mInterface.getEndpoint(2);
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
            Log.e(TAG, "Read endpoint 3 is not interrupt type");
            return;
        }
        mEndpointIntrRead3 = ep;

        ep = mInterface.getEndpoint(3);
        if (ep.getType() != UsbConstants.USB_ENDPOINT_XFER_INT) {
            Log.e(TAG, "Write endpoint 4 is not interrupt type");
            return;
        }
        mEndpointIntrWrite4 = ep;

        mDevice = device;
        if (device != null) {
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            if (connection != null && connection.claimInterface(mInterface, true)) {
                Log.d(TAG, "open SUCCESS");
                mConnection = connection;
                request_ep1.initialize(mConnection, this.mEndpointIntrRead1);
                request_ep3.initialize(mConnection, mEndpointIntrRead3);

                isInitialized = true;
                connectionState = ConnectionState.CONNECTED;

                m_interval_local_run = 0;
                clear_usb_counters();
                clear_debug_counters();

                /*Thread thread = new Thread((Runnable) this);
                thread.start();*/
            } else {
                Log.d(TAG, "open FAIL");
                mConnection = null;
            }
        }
    }

    /**
     * clear_usb_counters
     */
    public void clear_usb_counters() {
        ep1_RX_Count = 0;
        ep3_RX_Count = 0;
        //clear_PSoC_msCount = true;
        //dataHasChanged = true;
    }

    /**
     * clear_debug_counters
     */
    public void clear_debug_counters() {
        drop_Count = 0;
        delayCount = COMM_ERROR_CONST;
        delayMin = COMM_ERROR_CONST;
    }

    /**
     * mUsbDisconnect
     * called when the device is disconnected
     */
    private final BroadcastReceiver mUsbDisconnect = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                detach();
                System.exit(0);
            }
        }
    };

    /**
     * reestablishConnection
     * Used to fix an intermittent connection (this happened in testing during static testing)
     */
    public void reestablishConnection(){
        drop_Count++;
        UsbDevice device = getDesiredDevice();
        if (device != null && mConnection != null) {

            isInitialized = false;

            PendingIntent mPermissionIntent = null;
            if(!mUsbManager.hasPermission(device)){
                //mUsbManager = (UsbManager) context.getSystemService(ACTION_USB_PERMISSION);
                mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                mContext.registerReceiver(mUsbReceiver, filter);

                mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
            }

            if(mUsbManager.hasPermission(device))
                setDevice(device);
            else
                mUsbManager.requestPermission(device, mPermissionIntent);
        }
    }

    /**
     * check_for_device
     * Look for the desired device and try to connect to it
     */
    private void check_for_device() {
        HashMap<String, UsbDevice> deviceList;

        Log.d(TAG, "check_for_device");
        mUsbManager = (UsbManager)mContext.getSystemService(Context.USB_SERVICE);
        deviceList = mUsbManager.getDeviceList();
        try{
            Iterator<UsbDevice> deviceIterator = null;
            if(deviceList != null)
                deviceIterator = deviceList.values().iterator();

            while(deviceIterator != null && deviceIterator.hasNext() && (mConnection == null || mDevice == null)){

                UsbDevice device = deviceIterator.next();
                if(device != null && device.getVendorId() == VENDOR_ID && device.getProductId() == PRODUCT_ID){
                    isInitialized = false;
                    setDevice(device);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * detach
     * detach/disconnect from the device
     */
    public void detach(){

        Log.d(TAG, "Device Detached" );
        connectionState = ConnectionState.CONNECTION_DROPPED;
        //Toast.makeText(context, "Device Detached", Toast.LENGTH_SHORT).show();
        //if (mDevice != null && mDevice.equals(device)) {
        mDevice = null; //setDevice(null);
        // }
        if(mConnection != null){
            mConnection.releaseInterface(mInterface);
            mConnection.close();
        }
    }

    /**
     * getEp1_RX_Count
     * @return ep1_RX_Count
     */
    public long getEp1_RX_Count() {
        return ep1_RX_Count;
    }

    /**
     * getEp3_RX_Count
     * @return ep3_RX_Count
     */
    public long getEp3_RX_Count() {
        return ep3_RX_Count;
    }

    /**
     * getDrop_Count
     * @return drop_Count
     */
    public long getDrop_Count() {
        return drop_Count;
    }

    /**
     * getWaitingForEp1Data
     * @return waitingForEp1Data
     */
    public boolean getWaitingForEp1Data() {
        return waitingForEp1Data;
    }
}
