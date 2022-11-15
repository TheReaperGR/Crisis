package com.crisis.services;

import com.crisis.annotations.CrisisAnnotation;
import com.crisis.annotations.MethodAnnotation;
import com.crisis.resources.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("rawtypes")
public class Service implements Resource {

    /***
     * {@code getParameters} finds the parameters size
     * @param method which is running
     * @return an Integer which represents the parameters size
     */
    protected int getParameters(Method method) {
        return method.getParameters().length;
    }

    /***
     * {@code getTypes} finds the type size
     * @param methodAnnotation Custom Annotation
     * @return an Integer which represents the type size
     */
    protected int getTypes(MethodAnnotation methodAnnotation) {
        return methodAnnotation.types().length;
    }

    /***
     * {@code setParameters} converts the current parameter to a specific type
     * @param methodAnnotation Custom Annotation
     * @param type to be converted
     * @param i position of parameter
     * @return an Object to be stored in parameters
     */
    protected Object setParameters(MethodAnnotation methodAnnotation, Class type, int i) {

        // Integer class :
        if( type == Integer.class )
            return Integer.parseInt(methodAnnotation.parameters()[i]);

        // Boolean class :
        else if( type == Boolean.class )
            return Boolean.parseBoolean(methodAnnotation.parameters()[i]);

        // Double class :
        else if( type == Double.class )
            return Double.parseDouble(methodAnnotation.parameters()[i]);

        // Float class :
        else if( type == Float.class )
            return Float.parseFloat(methodAnnotation.parameters()[i]);

        // Long class :
        else if( type == Long.class )
            return Long.parseLong(methodAnnotation.parameters()[i]);

        // Short class :
        else if( type == Short.class )
            return Short.parseShort(methodAnnotation.parameters()[i]);

        // Byte class :
        else if( type == Byte.class )
            return Byte.parseByte(methodAnnotation.parameters()[i]);

        // Character class :
        else if( type == Character.class )
            return methodAnnotation.parameters()[i].charAt(0);

        // String class :
        return methodAnnotation.parameters()[i];

    }

    /***
     * {@code tableView} creates a string format with method data in order to be displayed
     * @param priority Priority status {LOW, MEDIUM, HIGH}
     * @param state State status {PROCESS, DISABLED, ERROR}
     * @param methodName name of current method
     * @param returnType return type
     * @param returnValue return value if result is true else {result is disabled} message
     * @param parameters parameters of method if exist else None
     * @param exception if an error occurred exception will be displayed else None
     * @return String format with the parameters
     */
    protected String tableView(CrisisAnnotation.PRIORITY priority, String state ,String methodName,
                                   Class<?> returnType, Object returnValue, Object[] parameters,
                               String runtime, String exception) {

        return String.format("%-15s %-15s %-25s %-25s %-25s %-30s %-20s %-20s", priority, state, methodName, returnType,
                returnValue, Arrays.toString(parameters), runtime, exception);
    }

    /***
     * {@code colorizedText} colorizes text if method is enabled
     * @param crisisAnnotation Custom Annotation
     * @param color color
     * @param text to be colorized
     * @return text with color if method is enabled
     */
    protected String colorizedText(CrisisAnnotation crisisAnnotation, String color, String text) {
        return (crisisAnnotation.colorize() ? toString(color, text) : text);
    }

    /***
     * {@code runMethod} run a method with or without parameters
     * @param methodAnnotation Custom Annotation
     * @param method to be executed
     * @param object of class which method is
     * @param parameters to be passed in method
     * @return value of method if result method is enabled else {result is disabled} message
     */
    protected String runMethod(MethodAnnotation methodAnnotation,Method method, Class<?> object, Object[] parameters) {

        //parameters = ((methodAnnotation.array() ? (Object[]) new String[]{Arrays.toString(parameters)} : parameters));

        try {
            return (methodAnnotation.result() ?
                    String.valueOf(method.invoke(object.getDeclaredConstructor().newInstance(),
                            (methodAnnotation.array() ? (Object[]) new String[]{Arrays.toString(parameters)} :
                                    parameters))) :
                    "result is disabled");

            // Error occurred :
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected void displayArrayList(ArrayList<String> methods) {
        methods.forEach(System.out::println);
    }

    /***
     * {@code toString} sets color to text
     * @param color color
     * @param text to be colorized
     * @return string with color
     */
    public static String toString(String color, String text) {
        return String.format("%s%-15s%s", color, text, RESET);
    }
}
