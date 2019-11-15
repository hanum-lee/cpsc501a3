package Main;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;


public class Main {

    public static void main(String[] args) {
	// write your code here

        SAXBuilder sax = new SAXBuilder();

        Object o;
        Inspector inspector = new Inspector();
        inspector.inspect(o,true);
    }
}
