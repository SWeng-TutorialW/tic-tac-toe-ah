package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
	
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        System.out.println("Starting server...");
        server = new SimpleServer(3001); // prefered to write more than 3000 (the port)
        server.listen(); // we write this here
    }
}
