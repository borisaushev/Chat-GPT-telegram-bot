import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private final PrintWriter logger;

    public Logger() throws IOException {
        //Initializing logger
        logger = new PrintWriter(new BufferedWriter(new FileWriter(Properties.logFile)));
        logger.println("logger initialized ");
        logger.flush();

    }

    public void log(String str) {
        logger.println(str);
        logger.flush();
    }
}
