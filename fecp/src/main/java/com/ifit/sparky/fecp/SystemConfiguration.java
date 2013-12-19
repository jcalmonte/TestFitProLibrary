/**
 * The system configuration for the communication mode.
 * @author Levi.Balling
 * @date 12/18/13
 * @version 1
 * This is to determine the mode and the type of communication that ifit can have with the device.
 */
package com.ifit.sparky.fecp;

public enum SystemConfiguration {
    SLAVE("Ifit has complete control, if communication is lost system stops."),
    MASTER("Ifit only has access to getData commands only, " +
            "if communication is lost system continues."),
    MULTI_MASTER("Both the systems need to play nice, if communication is lost, system continues");


    private String mDescription;//description about the mode

    /**
     * Constructor for the SystemConfiguration
     * @param description about the Configuration
     */
    SystemConfiguration(String description)
    {
        this.mDescription = description;
    }

    /**
     * Gets a description of the configuration
     * @return the description
     */
    public String getDescription()
    {
        return this.mDescription;
    }

    /**
     * Gets the unique id of the system configuration
     * @return the ordinal value
     */
    public int getVal()
    {
        return this.ordinal();
    }
}
