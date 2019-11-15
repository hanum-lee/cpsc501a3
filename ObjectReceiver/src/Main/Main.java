package Main;
import com.sun.security.ntlm.Server;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

import javax.print.Doc;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.*;

public class Main {

    public static void main(String[] args) throws Exception {
	// write your code here
        ServerSocket socket = new ServerSocket(5000);

        while(true){
            Socket connection = socket.accept();
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
            SAXBuilder sax = new SAXBuilder();
            Document doc = sax.build(input);
            Deserializer deserializer = new Deserializer();
            Object o = deserializer.deseralize(doc);
            Inspector inspector = new Inspector();
            inspector.inspect(o,true);
        }

    }
}
