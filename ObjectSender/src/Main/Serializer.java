package Main;
import org.jdom2.*;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class Serializer {

    private Element root;
    private  Document document;
    private HashMap<Object, Integer> serializedMap = new HashMap<>();
    private int globalID = 1;

    public Document serialize(Object obj){
        document = new Document();
        root = new Element("serialized");
        document.setRootElement(root);
        serializeImplementation(obj);
        return document;
    }

    private int serializeImplementation(Object obj){
        if(obj == null){
            return 0;
        }
        Class objClass = obj.getClass();

        if (serializedMap.containsKey(obj)) {

            return serializedMap.get(obj);
        }

        int id = globalID++;
        serializedMap.put(obj,id);

        Element objEle = new Element("Object")
                .setAttribute("class",objClass.getName())
                .setAttribute("id",Integer.toString(id));
        System.out.println(obj.getClass().getName());
        if(objClass.isArray()){
            objEle.setAttribute("length",Integer.toString(Array.getLength(obj)));
            serializeArray(obj,objEle);
        }else if (objClass.isPrimitive() || isType(objClass)){
            System.out.println("Primitive");
            serializePrimitive(obj,objEle);
        }else if (objClass.equals(ArrayList.class)){
            serializeArray((ArrayList)obj,objEle);
        }else{
            serializeObj(obj,objEle);
        }

        root.addContent(objEle);
        return id;
    }

    private void serializePrimitive(Object obj, Element parent){
         Element e = new Element("value");
         e.setText(obj.toString());
         parent.addContent(e);
    }

    private void serializeObj(Object obj, Element parent){
        Class currentClass = obj.getClass();
        if(currentClass != null){
            Field[] fields = currentClass.getDeclaredFields();
            for (Field f:fields) {
                serializeField(obj,f,parent);
            }
        }

    }
    private void serializeField(Object obj, Field f, Element parent){
        Element e = new Element("field");
        e.setAttribute("name", f.getName());
        e.setAttribute("declaringClass",f.getDeclaringClass().getName());

        parent.addContent(e);

        f.setAccessible(true);
        try{
            Object val = f.get(obj);
            Class fieldType = f.getType();
            if(fieldType.isPrimitive()){
                serializePrimitive(val,e);
            }else{
                int id = serializeImplementation(val);
                Element eo = new Element("reference");
                eo.setText(Integer.toString(id));
                parent.addContent(eo);
            }
        }catch(IllegalAccessException errors){
            errors.printStackTrace();
        }

    }

    private boolean isType(Class clas){
        HashSet<Class<?>> types = new HashSet<>(
                Arrays.asList(
                        Character.class,
                        //Short.class,
                        Byte.class,
                        Integer.class,
                        //Long.class,
                        Float.class,
                        Double.class,
                        Boolean.class,
                        String.class
                )
        );
        return types.contains(clas);
    }

    private void serializeArray(Object obj, Element parent){
        int length = Array.getLength(obj);
        Class arrType = obj.getClass().getComponentType();
        for(int i = 0; i < Array.getLength(obj);i++){
            Object arrObj = Array.get(obj,i);
            if(arrType.isPrimitive()){
                serializePrimitive(arrObj,parent);
            }else{
                int id = serializeImplementation(arrObj);
                Element eo = new Element("reference");
                eo.setText(Integer.toString(id));
                parent.addContent(eo);
            }
        }
    }
}
