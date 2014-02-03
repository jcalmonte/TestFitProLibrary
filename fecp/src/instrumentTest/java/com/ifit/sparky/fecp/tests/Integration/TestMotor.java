package com.ifit.sparky.fecp.tests.Integration;

import junit.framework.TestCase;

/**
 * Created by eetestlab on 1/23/14.
 */
public class TestMotor extends TestIntegration{
    //The testStartSpeed is planned to automate #16 of the software
    //checklist to make sure that the machine starts at 1.0mph or 2.0kph
    public void testStartSpeed() throws Exception{
        //outline for code
        //send basic start command to start motor at on position
        //request actual speed from device to make sure it is connected and moving
        //read speed received into this code which should be target speed
        //check against constant variable of 1.0 mph
        //make sure formatting is right for verification for english or metric units
    }
    //the testMaxSpeedTime is planned to automate #59 of the software
    //checklist to time the amount of time it takes to go from 0 to max speed
    public void testMaxSpeedTime() throws Exception{
        //outline for code
        //look up max speed for device
        //send basic start command to start motor at on position
        //start stopwatch timer
        //send command to change speed to max speed
        //read current speed until actual is the same as target
        //stop stopwatch and return/display/record the value of the stopwatch
    }
}
