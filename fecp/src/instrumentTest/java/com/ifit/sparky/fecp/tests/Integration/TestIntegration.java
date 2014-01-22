package com.ifit.sparky.fecp.tests.Integration;

import junit.framework.TestCase;

/**
 * Created by eetestlab on 1/21/14.
 * This is Sean's first attempt to try and set up and verify that the android tablet clock
 * is the same as the computer clock to verify the stopwatch portion of the software checklist
 */
public class TestIntegration extends TestCase{
    public void testTimeFiveMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 5 minute time test
        while (elapsedTime < 5*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test took 300 seconds which is 5 minutes so it looks like it works
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }
    public void testTimeTenMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 10 minute time test
        while (elapsedTime < 10*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test should take 600 seconds which is 10 minutes
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }
    public void testTimeFifteenMin() throws Exception
    {
        //start stopwatch
        //get java time
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long androidTime = 0;
        boolean testPassed = false;
        //loop for 15 minute time test
        while (elapsedTime < 15*60*1000){
            //get time from android and compare to java time
            //Not sure how to do this
            //the test should take 900 seconds which is 15 minutes
            //androidTime = getDeviceTime();
            elapsedTime = (new java.util.Date()).getTime() - startTime;
        }
        if(androidTime == elapsedTime)
            testPassed = true;
        else
            testPassed = false;
    }

    //super test to run through all the tests; not sure if necessary
    public void superTest() throws Exception{

        //testConstructor_fecpCommand();
        //testSetters_fecpCommand();
        //testCallback_fecpCommand();
        //testGetterSetter_systemDevice();
        //testConstructor_systemConfiguration();
        //testCallback_SystemCallback();
        //testConstructor_fecpController();
        //testInitializeConnection_FecpController();
        //testConstructor_systemDevice();
        //testGetterSetter_systemDevice();
        //getSysDev();
        //getIsConnected();
        //testInitializeConnection_FecpController();


    }
    //The testStartSpeed is planned to automate #16 of the software
    //checklist to make sure that the machine starts at 1.0mph or 2.0kph
    public void testStartSpeed() throws Exception{
        //outline for code
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code
        //and check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
    }
}
