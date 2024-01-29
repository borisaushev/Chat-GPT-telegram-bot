package Util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private static PrintWriter logger;

    public static synchronized void log(String str) {
        //Initializing logger if it's not
        if(logger == null) {
            try {logger = new PrintWriter(new FileWriter(Properties.logFile));} catch (IOException ignored) {}
            logger.println("logger initialized ");
            logger.flush();
        }

        //logging
        logger.println(str);
        logger.flush();
    }
}
