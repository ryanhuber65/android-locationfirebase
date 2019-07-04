package com.comp259.huber7517.comp262final;

public class Global {

    // the android emulator maps 10.0.2.2 to the hosts's loopback ip 127.0.0.1
    // note that if you are using visual studio to run a Firebase application server you must
    // force the visual studio project to use 127.0.0.1 by chaging the Project > Properties settings
    // and editing the .vs/applicationhost.config file
    // see, for example:
    // http://10printhello.com/using-a-custom-hostname-with-iis-express-with-visual-studio-2015-vs2015/

    //helper class for distinguishing the data in the intent
    public static final String SERVER_URL = "http://10.0.2.2:6868/fcm/register.aspx";
    public static final String EXTRA_MESSAGE = "Extra_Message";
    public static final String NOTIFICATION_MESSAGE = "Notification_Message";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lon";
    public static final String TIME = "time";
	public static final String REGISTRATION_COMPLETE = "registrationComplete";
	public static final String PUSH_NOTIFICATION = "pushNotification";

}