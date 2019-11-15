package Main;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.util.ArrayList;


public class ObjectCreator {


    public static class Primitive{
        public int value = 1;
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

    public void primitiveObj(){
        Serializer serial = new Serializer();
        int test = 1;
        boolean bool = true;

        Document doc = serial.serialize(new Objects());
        XMLOutputter xml = new XMLOutputter();
        xml.setFormat(Format.getPrettyFormat());
        String data = xml.outputString(doc);
        System.out.println(data);
    }


}
