// IDeviceControlInterface.aidl
package com.techinfo.devicemanager;

interface IDeviceControlInterface {
    //**********************************************************************************************
    //Node
    // |-  ____________________________________________________________________________________________________________________________
    //    |__Name___|___Get_path_from_______|____Default_path__________________________________|____Notice__________|____Value_________|
    //    |VBUS     | ro.pogo.path.vbus     |  /sys/class/ext_dev/function/ext_dev_5v_enable   |  readable,writable |       0,1        |
    //    |VBAT     | ro.pogo.path.vbat     |  /sys/class/ext_dev/function/ext_dev_3v3_enable  |  readable,writable |       0,1        |
    //    |GPIO1    | ro.pogo.path.gpio1    |  /sys/class/ext_dev/function/pin10_en            |  writable          |       0,1        |
    //    |GPIO2    | ro.pogo.path.gpio2    |  /sys/class/ext_dev/function/pin11_en            |  writable          |       0,1        |
    //    |IRQ      | ro.pogo.path.irq      |  /sys/class/ext_dev/function/irq_state           |  readable          |       0,1        |
    //    |_________|__________________________________________________________________________|____________________|__________________|
    //Serial
    // |-  ____________________________________________________________________________________________________________________________
    //    |__Name___|___Get_path_from_______|____Default_path__________________________________|____Notice__________|____Buff__________|
    //    |Serial   | ro.pogo.path.serial   |  /dev/ttyHSL1                                    |  readable,writable |   any bytes      |
    //    |_________|_______________________|__________________________________________________|____________________|__________________|
    //**********************************************************************************************

    // Specify nodes using the "value" configuration
    // @parameter path Specify node path
    // @parameter value 1: high, 0: low
    oneway void writeNode(String path, String value);

    // Gets the state of the specified Node
    // @parameter path Specify node path
    // @return value 1: high, 0: low
    String readNode(String path);

    // Opens the specified serial port at the specified baud rate
    // @parameter path Specify serial path
    // @parameter baudRate Specified baud rate
    // @return A descriptor for a serial port file. if smaller than 0: Serial opened failed.
    int openSerial(String path, int baudRate);

    // Read data from a serial port in the specified byte array
    // @parameter fd The descriptor of a serial port
    // @parameter buff An array of bytes that holds data read from a serial port
    // @parameter size The maximum length of data read from a serial port
    // @return The length of data read from the serial port
    int readSerial(int fd, out byte[] buff, int size);

    // Write data in the specified byte array to a serial port
    // @parameter fd The descriptor of a serial port
    // @parameter buff An array of bytes that holds data written to a serial port
    // @parameter size The size of data to be written to the serial port
    // @return The length of the data that was successfully written
    int writeSerial(int fd, in byte[] buff, int size);

    // Closes the specified serial port
    // @parameter fd The descriptor of a serial port
    oneway void closeSerial(int fd);
}

