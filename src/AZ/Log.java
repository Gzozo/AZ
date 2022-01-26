package AZ;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log
{
    private static Logger LOG = Logger.getLogger(Log.class);
    
    public static void log(String message)
    {
        LOG.log(Log.class.getCanonicalName(), Level.INFO, message, null);
    }
    
    public static void log(int message)
    {
        log(message + "");
    }
}