package com.ifit.sparky.fecp.tests.Integration;

import com.ifit.sparky.fecp.FecpCommand;
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
    public char getType{
        char machineType = x;
        //if machine type is x we could not get the machine type
        //legend: t represents treadmill
        //        e represents elliptical
        //        b represents bike
        //        x represents unknown device
        fecpCmd machine = null;
        try {
            machine = new FecpCommand();
        } catch (Exception e) {
            e.printStackTrace();
        }
        device machineName = new DeviceId.getDeviceId();
        if(machine.equals("Treadmill"))
            machineType = t;
        if(machine.equals("Elliptical"))
            machineType = e;
        if(machine.equals("Bike"))
            machineType =b;
        return machineType;
    }
    public void chooseType{
        char treadmill = t;
        char elliptical = e;
        char bike = b;
        char unknown = x;
        if(getType() == unknown)
            //unknown device don't run a test
        if(getType() == elliptical)
            //device is elliptical
            //run elliptical test
            testElliptical();

        if(getType() == bike)
            //device is bike
            //run bike test
            testBike();

        if(getType() == treadmill)
            //device is treadmill
            //run treadmill test
            testTreadmill();

    }
    public void testElliptical{
        //method for Elliptical specific tests
        genericTest();
    }
    public void testTreadmill{
        //method for Treadmill specific tests
        genericTest();
        //test start speed automation from test motor class
        testStartSpeed();

    }
    public void testBike{
        //method for bike specific tests
        genericTest();
    }
    public void genericTest{
        //method that includes all tests that are appropriate for all machines
        testTimeFiveMin();
        testTimeTenMin();
        testTimeFifteenMin();
    }
}
