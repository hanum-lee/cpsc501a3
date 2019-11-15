package Main;
//import java.lang.*;

import java.lang.reflect.*;
import java.util.Arrays;

public class Inspector {

    public void inspect(Object obj, boolean recursive) throws NoSuchMethodException, IllegalAccessException {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) throws NoSuchMethodException, IllegalAccessException {
        // System.out.println(c);

        if(c.isArray()){
            Object[] tempArr = new Object[Array.getLength(obj)];

            System.out.println(c.getComponentType());
            c = c.getComponentType();
            for(int i = 0; i < tempArr.length; i++ ){
                Object objInArray = Array.get(obj,i);
                //obj = Array.get(obj,i);
                if(objInArray != null && objInArray.getClass().isArray()){
                    Object[] tempArr2 = new Object[Array.getLength(objInArray)];
                    c = objInArray.getClass().getComponentType();
                    String arrOut = "[";
                    for(int j = 0; j < tempArr2.length; j++){
                        //System.out.println(Array.get(objInArray,j));
                        //obj = Array.get(objInArray,j);
                        if(j == tempArr2.length -1){
                            arrOut = arrOut + Array.get(objInArray,j);
                        }else{
                            arrOut = arrOut + Array.get(objInArray,j) + ", ";
                        }
                    }
                    arrOut = arrOut + "]";
                    System.out.println(arrOut);
                    arrOut = "[";
                } else{
                    System.out.println(Array.get(obj,i));
                }

            }

        }
        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }

        String innerFormat = formatting + "   ";
        System.out.println(formatting + "Super Class(" + c.getName() + ")" );
        recurSuper(c,obj,depth);

        System.out.println(formatting + "Interfaces(" + c.getName() + ")");
        recurInter(c,obj,depth);

        System.out.println(formatting + "Constructor (" + c.getName() + ")");
        constructorInfo(c,depth);

        methodInfo(c,depth);

        System.out.println(formatting + "Fields (" + c.getName() + ")");
        fieldInfo(c,obj,recursive,depth);

    }

    public void recurSuper(Class c, Object obj, int depth) throws NoSuchMethodException, IllegalAccessException {
        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }
        String innerFormat = formatting + "   ";

        Class superC = c.getSuperclass();
        if(superC != null){
            //System.out.println("Super Class of " + c.getName());
            System.out.println(innerFormat + "Name: " + superC.getName());
            //recurSuper(superC, depth+1);
            inspectClass(superC,obj,false,depth+1);;
        }
    }

    public void recurInter(Class c, Object obj, int depth) throws NoSuchMethodException, IllegalAccessException {
        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }
        String innerFormat = formatting + "   ";

        Class[] inter = c.getInterfaces();
        for(int i = 0; i < inter.length; i++){
            System.out.println(innerFormat + inter[i].getName());
            //recurInter(inter[i],depth+1);
            inspectClass(inter[i],obj, false, depth+1);
        }
    }

    public void constructorInfo(Class c, int depth){

        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }
        String innerFormat = formatting + "   ";

        for(int i = 0; i < c.getDeclaredConstructors().length; i++){
            //System.out.println(c.getDeclaredConstructors()[i]);
            Constructor con = c.getDeclaredConstructors()[i];
            System.out.println(innerFormat + "Name: " +con.getName());
            //System.out.println(con.getDeclaringClass());
            System.out.println(innerFormat + "Parameter Types: ");
            for(int j = 0; j < con.getParameterCount(); j++){
                System.out.println(innerFormat + "   " + con.getParameterTypes()[j]);
            }
            System.out.println(innerFormat + "Modifiers: " + con.getModifiers());

        }
    }

    public void methodInfo(Class c, int depth){
        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }

        String innerFormat = formatting + "   ";
        System.out.println(formatting + "Methods (" + c.getName() + ")");
        for(int i = 0; i < c.getDeclaredMethods().length ; i ++){
            Method met = c.getDeclaredMethods()[i];
            //System.out.println(met);
            System.out.println(innerFormat + "Name: " + met.getName());
            Class[] metException = met.getExceptionTypes();
            System.out.println(innerFormat + "Exceptions: ");
            for (Class excp: metException) {
                System.out.println(innerFormat + "   " + excp);
            }
            Class[] metParam = met.getParameterTypes();
            System.out.println(innerFormat + "Parameters: ");
            for(Class param:metParam){
                System.out.println(innerFormat + "   " + param.getName());
            }
            Class metReturn = met.getReturnType();
            System.out.println(innerFormat + "Return type: " + metReturn.getName());
            int metModint = met.getModifiers();
            System.out.println(innerFormat + "Modifiers: " + Modifier.toString(metModint));


        }


    }
    public void fieldInfo(Class c, Object obj, boolean recursive, int depth){
        String formatting = "";
        for(int i =0; i < depth; i++){
            formatting = formatting + "\t";
        }

        String innerFormat = formatting + "   ";

        for(int i = 0; i < c.getDeclaredFields().length; i++){
            try{
                Field f = c.getDeclaredFields()[i];
                f.setAccessible(true);
                //System.out.println(c.getDeclaredFields()[i]);
                Object val = f.get(obj);
                System.out.println(innerFormat + "Name:"+f.getName());
/*            if(f.getType().isArray()){
                System.out.println(innerFormat + "Type: " + f.getType().getComponentType());
            }else{
            }*/
                System.out.println(innerFormat + "Type: " + f.getType().getName());
                int fval = f.getModifiers();
                System.out.println(innerFormat + "Modifier: " + Modifier.toString(fval));

                if(f.getType().isArray()){
                    Object[] temparr = new Object[Array.getLength(val)];
                    System.out.println(innerFormat + "Array Value:");
                    for(int j = 0; j < temparr.length ; j++){
                        System.out.println(innerFormat + "  " + Array.get(val,j));
                    }
                }else{
                    System.out.println(innerFormat + "Value: " + val);
                }

                if(recursive && !f.getType().isPrimitive()){
                    System.out.println("recursing");
                    inspectClass(f.getType(),f.get(obj),recursive,depth + 1);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}