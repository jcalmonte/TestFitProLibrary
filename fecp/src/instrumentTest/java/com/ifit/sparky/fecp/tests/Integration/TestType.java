package com.ifit.sparky.fecp.tests.Integration;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.FecpController;
import com.ifit.sparky.fecp.SystemConfiguration;
import com.ifit.sparky.fecp.communication.CommType;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;
import android.app.Activity;

/**
 * Created by eetestlab on 1/27/14.
 * This class is the way for the testing system to determine what type of
 * machine it is whether it is a Treadmill, Bike or Elliptical to determine
 * what tests to run and what tests are possible and appropriate.
 */
public class TestType extends TestIntegration{
    //need to connect to the FECP controller and get the correct type of machine
    //to choose what test to run
    public char getType() throws  Exception{
        char machineType = 'x';
//        //if machine type is x we could not get the machine type
//        //legend: t represents treadmill
//        //        e represents elliptical
//        //        b represents bike
//        //        x represents unknown device
//        Device machineName = new Device(DeviceId.valueOf());
//        machineName.getCommand();
//        Command cmd = new InfoCmd(DeviceId.valueOf());
//        //try to access the fecp system
//        String machine;
//        //try to put the type into a string to compare it
//        machine = DeviceId.getDeviceId();
//        FecpCommand machineCommand = null;
//        //attempt to initialize a connection with the FECP controller
//        //access the FECP library
//        try{
//            FecpController fecpController = new FecpController(TestType.this, getIntent(), CommType.USB_COMMUNICATION, null);}
//            fecpController.initializeConnection();
//            catch (Exception e)
//            {
//                    Log.e("Device failed", e.getMessage()));
//            try {
//                machineCommand = new FecpCommand(); getIntent(), CommType.USB_COMMUNICATION, null);
//
//            }catch (Exception exit){
//
//                //could not pull the info check to make sure device is connected
//                //perhaps wait and try again?
//            exit.printStackTrace();
//        }
//
//            //the device can not be both tread and trainer therefore XOR gate
//            if((machine.equals("TREADMILL"))^(machine.equals("INCLINE_TRAINER")))
//                machineType = 't';
//            if(machine.equals("ELLIPTICAL"))
//                machineType = 'e';
//            if(machine.equals("BIKE"))
//            machineType = 'b';

        return machineType;

    }
    public void chooseType() throws Exception{
        char treadmill = 't';
        char elliptical = 'e';
        char bike = 'b';
        char unknown = 'x';
        if(getType() == unknown)
            //unknown device don't run a test
        if(getType() == elliptical)
            //device is elliptical
            //run elliptical test method
            testElliptical();

        if(getType() == bike)
            //device is bike
            //run bike test method
            testBike();

        if(getType() == treadmill)
            //device is treadmill
            //run treadmill test method
            testTreadmill();

    }
    public void genericTest() throws Exception {
        //method that includes all tests that are appropriate for all machines
        testTimeFiveMin();
        testTimeTenMin();
        testTimeFifteenMin();
        testTimeModes();
        testSystemConfiguration();
    }
    public void testTreadmill() throws Exception{
        //method for Treadmill & Incline Trainer specific tests
        genericTest();
        //run all automation methods from test motor class
        TestMotor treadmillMotor = new TestMotor();
        treadmillMotor.testStartSpeed();
        treadmillMotor.testMaxSpeedTime();
        treadmillMotor.testModeChange();
        treadmillMotor.testMotorPWM();
        treadmillMotor.testSpeedController();
        treadmillMotor.testDistance();
        //run automation methods from test incline class
        TestIncline treadmillIncline = new TestIncline();
        //test all incline functions and records transmax value
        int transmax = treadmillIncline.testIncline();
        int transmaxStandard = 200; //value will change with each machine
        boolean transmaxPass = false;
        if(transmax == transmaxStandard)
            //check transmax against hardcoded standard?
            transmaxPass = true;
        //write transmax to file for logging? What to do with transmax?

    }
    public void testElliptical() throws Exception{
        //method for Elliptical specific tests
        genericTest();
    }
    public void testBike() throws Exception{
        //method for bike specific tests
        genericTest();
    }
}
