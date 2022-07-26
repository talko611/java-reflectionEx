import reflection.api.Investigator;

import java.lang.reflect.*;
import java.util.HashSet;

import java.util.Set;


public class InvestigatorImp implements Investigator {
    private Object obj;
    private Class<?> aClass;


    @Override
    public void load(Object anInstanceOfSomething) {
        this.obj = anInstanceOfSomething;
        this.aClass = anInstanceOfSomething.getClass();
    }

    @Override
    public int getTotalNumberOfMethods() {
        return aClass.getDeclaredMethods().length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        return aClass.getDeclaredConstructors().length;
    }

    @Override
    public int getTotalNumberOfFields() {
        return aClass.getDeclaredFields().length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Class<?>[] interfaces = aClass.getInterfaces();
        Set<String> interfacesNames = new HashSet<>();
        for (Class<?> anInterface : interfaces) {
            interfacesNames.add(anInterface.getSimpleName());
        }
        return interfacesNames;
    }

    @Override
    public int getCountOfConstantFields() {
        try{
            int counter = 0;
            for (Field field : aClass.getDeclaredFields()){
                if(Modifier.isFinal(field.getModifiers())){
                    ++counter;
                }
            }
            return counter;
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public int getCountOfStaticMethods() {
        try{
            int counter = 0;
            for (Method field : aClass.getDeclaredMethods()){
                if(Modifier.isStatic(field.getModifiers())){
                    ++counter;
                }
            }
            return counter;
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public boolean isExtending() {
        Class<?> superClass = aClass.getSuperclass();
        if(superClass == null){
            return false;
        }
        return !superClass.equals(Object.class);
    }

    @Override
    public String getParentClassSimpleName() {
        if(isExtending()){
            return aClass.getSuperclass().getSimpleName();
        }
        return null;
    }

    @Override
    public boolean isParentClassAbstract() {
        if(isExtending()){
            return Modifier.isAbstract(aClass.getSuperclass().getModifiers());
        }
        return false;
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Set<String> allFields = new HashSet<>();
        for(Class<?> currentClazz = aClass; currentClazz != null; currentClazz = currentClazz.getSuperclass()){
            for(Field field : currentClazz.getDeclaredFields()){
                allFields.add(field.getName());
            }
        }
        return allFields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        try{
            Class<?>[] argClasses = new Class<?>[args.length];
            for(int i = 0; i < args.length; ++i){
                argClasses[i] = args[i].getClass();
            }
            Method func = aClass.getMethod(methodName, argClasses);
            if(Modifier.isStatic(func.getModifiers())){
                return (int) func.invoke(null,args);
            }
            return (int) func.invoke(this.obj, args);
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        try {
            for (Constructor<?> ctor : aClass.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == numberOfArgs) {
                    return ctor.newInstance(args);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        try{
            Method method = aClass.getDeclaredMethod(name, parametersTypes);
            method.setAccessible(true);
            if(Modifier.isStatic(method.getModifiers()))
                return method.invoke(null,args);
            return method.invoke(this.obj,args);
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        Class<?> clazz = aClass;
        StringBuilder res = new StringBuilder();

        while(!clazz.getSimpleName().equals("Object")){
            res.insert(0, clazz.getSimpleName());
            res.insert(0,delimiter);
            clazz = clazz.getSuperclass();
        }
        res.insert(0, clazz.getSimpleName());

        return res.toString();
    }
}
