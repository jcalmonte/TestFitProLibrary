package com.ifit.sparky.fecp.tests.Integration;

import junit.framework.TestCase;

/**
 * Created by eetestlab on 1/23/14.
 */
public class TestMotor extends TestCase{
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
