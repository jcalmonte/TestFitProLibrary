/**
 * KeyCodes for what was pressed.
 * @author Levi.Balling
 * @date 12/12/13
 * @version 1
 * keycode for what was pressed so every keycode can be handled.
 */
package com.ifit.sparky.fecp.interpreter.key;

public enum KeyCodes {
    NO_KEY(0, "Basic"),
    STOP(1, "Basic"),
    START(2, "Basic"),
    SPEED_UP(3, "Basic"),
    SPEED_DOWN(4, "Basic"),
    INCLINE_UP(5, "Basic"),
    INCLINE_DOWN(6, "Basic"),
    RESISTANCE_UP(7, "Basic"),
    RESISTANCE_DOWN(8, "Basic"),
    GEAR_UP(9, "Basic"),
    GEAR_DOWN(10, "Basic"),
    WEIGHT_UP(11, "Basic"),
    WEIGHT_DOWN(12, "Basic"),
    AGE_UP(13, "Basic"),
    AGE_DOWN(14, "Basic"),

    FAN_UP(50, "Fan"),
    FAN_DOWN(51, "Fan"),
    FAN_OFF(52, "Fan"),
    FAN_MANUAL(53, "Fan"),
    FAN_AUTO(54, "Fan"),
    FAN_1(55, "Fan"),
    FAN_2(56, "Fan"),
    FAN_3(57, "Fan"),
    FAN_4(58, "Fan"),
    FAN_5(59, "Fan"),

    PC_BACK(100, "Navigation"),
    PC_MENU(101, "Navigation"),
    PC_HOME(102, "Navigation"),
    KEYPAD(103, "Navigation"),
    DISPLAY(104, "Navigation"),
    ENTER(105, "Navigation"),
    UP(106, "Navigation"),
    DOWN(107, "Navigation"),
    LEFT(108, "Navigation"),
    RIGHT(109, "Navigation"),

    TV_POWER(120, "Tv"),
    TV_CHANNEL_UP(121, "Tv"),
    TV_CHANNEL_DOWN(122, "Tv"),
    TV_RECALL(123, "Tv"),
    TV_MENU(124, "Tv"),
    TV_SOURCE(125, "Tv"),
    TV_SEEK(126, "Tv"),
    TV_CLOSE_CAPTION(127, "Tv"),
    TV_VOLUME_UP(128, "Tv"),
    TV_VOLUME_DOWN(129, "Tv"),
    TV_MUTE(130, "Tv"),

    RIGHT_GEAR_UP(150, "Bike"),
    RIGHT_GEAR_DOWN(151, "Bike"),
    LEFT_GEAR_UP(152, "Bike"),
    LEFT_GEAR_DOWN(153, "Bike"),

    AUDIO_VOLUME_UP(200, "Audio"),
    AUDIO_VOLUME_DOWN(201, "Bike"),
    AUDIO_MUTE(202, "Bike"),
    AUDIO_EQUALIZER(203, "Bike"),
    AUDIO_SOURCE(204, "Bike"),

    NUMBER_PAD_0(300, "Number Pad"),
    NUMBER_PAD_1(301, "Number Pad"),
    NUMBER_PAD_2(302, "Number Pad"),
    NUMBER_PAD_3(303, "Number Pad"),
    NUMBER_PAD_4(304, "Number Pad"),
    NUMBER_PAD_5(305, "Number Pad"),
    NUMBER_PAD_6(306, "Number Pad"),
    NUMBER_PAD_7(307, "Number Pad"),
    NUMBER_PAD_8(308, "Number Pad"),
    NUMBER_PAD_9(309, "Number Pad"),
    NUMBER_PAD_STAR(310, "Number Pad"),
    NUMBER_PAD_DOT(311, "Number Pad"),
    NUMBER_PAD_HASH(312, "Number Pad"),
    NUMBER_PAD_OK(313, "Number Pad"),
    NUMBER_PAD_ENTER(314, "Number Pad"),


    ERGOFIT_TILT_FORWARD(400, "Ergo Fit Keys"),
    ERGOFIT_TILT_BACK(401, "Ergo Fit Keys"),
    ERGOFIT_UPRIGHT_UP(402, "Ergo Fit Keys"),
    ERGOFIT_UPRIGHT_DOWN(403, "Ergo Fit Keys"),
    ERGOFIT_MEMORY(404, "Ergo Fit Keys"),
    ERGOFIT_USER_1(405, "Ergo Fit Keys"),
    ERGOFIT_USER_2(406, "Ergo Fit Keys"),
    ERGOFIT_USER_3(407, "Ergo Fit Keys"),
    ERGOFIT_USER_4(408, "Ergo Fit Keys"),

    SET_TO_SHIP(500, "Maintenance"),
    DEBUG_MODE(501, "Maintenance"),
    LOG_MODE(502, "Maintenance"),

    //todo  need to continue to add more keycodes stopped for sanity sake

    DUMMY(9999, "Maintenance")
    ;


    private int mKeyValue;
    private String mCategory;

    /**
     * Constructor for all keycodes
     * @param keyValue the key value
     * @param category what category it is a part of
     */
    KeyCodes(int keyValue, String category)
    {
        this.mKeyValue = keyValue;
        this.mCategory = category;
    }

    /**
     * Gets the keycode value.
     * @return the keyCode value
     */
    public int getVal()
    {
        return this.mKeyValue;
    }

    /**
     * gets the Category the Keycode is in.
     * @return the category
     */
    public String getCategory()
    {
        return this.mCategory;
    }

    /**
     * Gets the Keycode based on the value
     * @param value the KeyCode value
     * @return the Keycode
     * @throws InvalidKeyCodeException if Keycode doesn't exist throw
     */
    public static KeyCodes getKeyCode(int value) throws InvalidKeyCodeException
    {
        //go through all Keycodes and if it equals then return it.

        for (KeyCodes devId : KeyCodes.values())
        {
            if(value == devId.getVal())
            {
                return devId; // the Keycode
            }
        }

        //error throw exception
        throw new InvalidKeyCodeException(value);
    }

}
