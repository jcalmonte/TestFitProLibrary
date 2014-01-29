package com.ifit.sparky.fecp.tests.Integration;

import com.ifit.sparky.fecp.FecpCommand;
import com.ifit.sparky.fecp.interpreter.command.Command;
import com.ifit.sparky.fecp.interpreter.command.InfoCmd;
import com.ifit.sparky.fecp.interpreter.device.Device;
import com.ifit.sparky.fecp.interpreter.device.DeviceId;

/**
 * Created by eetestlab on 1/27/14.
 * This class is the way for the testing system to determine what type of
 * machine it is whether it is a Treadmill, Bike or Elliptical to determine
 * what tests to run and what tests are possible and appropriate.
 */
public class TestType {
    //need to connect to the FECP controller and get the correct type of machine
    //to choose what test to run
    public char getType(){
        char machineType = 'x';
        //if machine type is x we could not get the machine type
        //legend: t represents treadmill
        //        e represents elliptical
        //        b represents bike
        //        x represents unknown device
        Device dev = new Device(DeviceId.valueOf());
        dev.getCommand();
        Command cmd = new InfoCmd(DeviceId.valueOf());
        //try to access the fecp system
        String machine;
        //try to put the type into a string to compare it
        machine = DeviceId.();
        FecpCommand machine = null;
        try {
            FecpCommand machine = new FecpCommand();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Device machineName = new Device(DeviceId.valueOf());
        //the device can not be both tread and trainer therefore XOR gate
        if((machine.equals("TREADMILL"))^(machine.equals("INCLINE_TRAINER")))
            machineType = 't';
        if(machine.equals("ELLIPTICAL"))
            machineType = 'e';
        if(machine.equals("BIKE"))
            machineType = 'b';
        return machineType;
    }
    public void chooseType(){
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
    public void testElliptical(){
        //method for Elliptical specific tests
        genericTest();
    }
    public void testTreadmill(){
        //method for Treadmill specific tests
        genericTest();
        //test start speed automation from test motor class
        testStartSpeed();

    }
    public void testBike(){
        //method for bike specific tests
        genericTest();
    }
    public void genericTest(){
        //method that includes all tests that are appropriate for all machines
        testTimeFiveMin();
        testTimeTenMin();
        testTimeFifteenMin();
    }
}
