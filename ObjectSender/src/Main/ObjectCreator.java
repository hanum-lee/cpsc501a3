package Main;
import org.jdom2.*;
import java.util.ArrayList;


public class ObjectCreator {


    public static class Primitive{
        public int value;
    }

    public static class Objects{
        public Object obj;
    }

    public static class PrimitiveArray{
        public int[] array = new int[0];
    }

    public static class ObjectArray{
        public Object[] array = new Object[0];
    }

    public static class Collection{
        public ArrayList<Object> array = new ArrayList<>();
    }




}
