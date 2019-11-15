package Main;
import java.net.*;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        //Socket socket = new Socket("localhost",5000);
        //DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        ObjectCreator objcre = new ObjectCreator();
        objcre.primitiveObj();
    }
}
