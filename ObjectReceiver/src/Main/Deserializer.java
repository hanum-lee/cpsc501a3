package Main;
import org.jdom2.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Deserializer {
    Object[] objects;

    public Object deseralize(Document doc) throws Exception{
        Element root = doc.getRootElement();
        initializeObjectBuffer(root);
        List<Element> objElements = root.getChildren();

        for(Element e: objElements){
            getObject(e);
        }
        for(Element e: objElements){
            getReferences(e);
        }

        return objects[1];
    }

    private void initializeObjectBuffer(Element root){
        objects = new Object[getMaxID(root) + 1];
        Arrays.fill(objects, null);
    }

    private int getMaxID(Element root){
        int current = 0;

        List<Element> children = root.getChildren();
        for(Element e: children){
            int idVal = Integer.parseInt(e.getAttribute("id").getValue());
            if(idVal > current){
                current = idVal;
            }
        }
        return current;
    }

    private int getObject(Element e) throws Exception {
        Class elemClass = getObjectClass(e);
        int id = Integer.parseInt(e.getAttribute("id").getValue());
        objects[id] = createObject(elemClass,e);
        return id;
    }

    private Class getObjectClass(Element e) throws Exception{
        String className = e.getAttribute("class").getValue();
        Class clz = Class.forName(className);
        return clz;
    }

    private Object createObject(Class c, Element e) throws Exception{
        Object obj = null;
        if(c.isArray()){
            obj = createArray(c,e);
        }else if(c.isPrimitive() || isType(c)){
            obj = createPrimitive(c,e);
        }else if(c.equals(ArrayList.class)){
            obj = createArrayList(e);
        }else{
            obj = createObjectTypes(c,e);
        }
        return obj;
    }

    private Object createObjectTypes(Class c, Element e)throws Exception{
        Object obj = c.getDeclaredConstructor().newInstance();
        List<Element> childFields = e.getChildren();
        for(Element el: childFields){
            setFieldValue(obj,el);
        }
        return obj;
    }

    private void setFieldValue(Object obj, Element field) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class declaringClazz = getDeclaringClass(field);
        Class currentClazz = findParentClass(obj.getClass(),declaringClazz);

        String fieldName = field.getAttributeValue("name");
        Field fieldset = currentClazz.getDeclaredField(fieldName);
        Class fieldType = fieldset.getType();

        if(fieldType.isPrimitive()){
            fieldset.setAccessible(true);
            List<Element> elements = field.getChildren();
            Element value = elements.get(0);

            Object val = loadPrimitive(fieldType, value.getValue());
            fieldset.set(obj,val);
        }



    }

    private Class getDeclaringClass(Element field) throws ClassNotFoundException {
        String declaringClassName = field.getAttributeValue("declaringClass");
        Class declaringClass = Class.forName(declaringClassName);
        return declaringClass;
    }

    private Class findParentClass(Class c,Class declaring){
        while(c != null && !c.equals(declaring)){
            c = c.getSuperclass();
        }
        return c;
    }

    private Object loadPrimitive(Class c, String value){
        if(c == float.class)
            return Float.valueOf(value);
        if(c == double.class)
            return Double.valueOf(value);
        if(c == boolean.class)
            return Boolean.valueOf(value);
        if(c == byte.class)
            return Byte.valueOf(value);
        if(c == char.class)
            return value.charAt(0);
        if(c == int.class)
            return Integer.valueOf(value);
        return null;
    }

    private void getReferences(Element e) throws Exception {
        Class c = getObjectClass(e);

        if(c.isPrimitive() || isType(c)){
            return;
        }

        int id = Integer.parseInt(e.getAttribute("id").getValue());
        Object obj = objects[id];
        if(c.isArray()){
            if(c.getComponentType().isPrimitive()){
                return;
            }

            int length = Integer.parseInt(e.getAttribute("length").getValue());
            List<Element> childFields = e.getChildren();
            for(int i = 0; i < length;i++){
                Element el = childFields.get(i);
                Array.set(obj,i,objects[Integer.parseInt(el.getValue())]);
            }
        } else if (c.equals(ArrayList.class)){
            ArrayList classArray = (ArrayList)obj;
            List<Element> childFields = e.getChildren();
            int length = childFields.size();
            for(int i = 0; i < length;i++){
                Element el = childFields.get(i);
                classArray.set(i,objects[Integer.parseInt(el.getValue())]);
            }
        }else{
            List<Element> childFields = e.getChildren();
            for(Element el: childFields){
                loadFieldReference(obj,e);
            }
        }

    }

    private void loadFieldReference(Object obj, Element e) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class declaringClass = getDeclaringClass(e);
        Class currentClass = findParentClass(obj.getClass(), declaringClass);

        String fieldName = e.getAttributeValue("name");
        Field fieldSet = currentClass.getDeclaredField(fieldName);

        Class fieldType = fieldSet.getType();

        if(fieldType.isPrimitive()){
            fieldSet.setAccessible(true);
            List<Element> elements = e.getChildren();
            Element value = elements.get(0);
            String referenceValue = value.getValue();
            int id = Integer.parseInt(referenceValue);
            fieldSet.set(obj,objects[id]);
        }

    }

    private Object createArray(Class c, Element e){
        int length = Integer.parseInt(e.getAttribute("length").getValue());
        Class baseType = c.getComponentType();
        Object obj = Array.newInstance(baseType,length);

        if(baseType.isPrimitive()){
            List<Element> childFields = e.getChildren();
            for(int i = 0; i < childFields.size();i++){
                Element el = childFields.get(i);
                Array.set(obj,i,loadPrimitive(baseType,el.getValue()));
            }
        }
        return obj;
    }

    private Object createPrimitive(Class c, Element e) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        List<Element> childFields = e.getChildren();
        Element value = childFields.get(0);
        Object obj;
        if(c.equals(String.class)){
            obj = value.getValue();
        }else{
            Method valueMethod = c.getMethod("valueOf",String.class);
            obj = valueMethod.invoke(null,value.getValue());
        }

        return obj;
    }

    private Object createArrayList(Element e){
        ArrayList objArrayList = new ArrayList<>();
        List<Element> children = e.getChildren();
        for (Element el: children) {
            objArrayList.add(null);
        }
        return objArrayList;
    }

    private boolean isType(Class clas){
        HashSet<Class<?>> types = new HashSet<>(
                Arrays.asList(
                        Character.class,
                        Byte.class,
                        Integer.class,
                        Float.class,
                        Double.class,
                        Boolean.class,
                        String.class
                )
        );
        return types.contains(clas);
    }
}
