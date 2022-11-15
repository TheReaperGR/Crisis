package com.crisis;

import com.crisis.annotations.CrisisAnnotation;
import com.crisis.annotations.MethodAnnotation;
import com.crisis.exceptions.ExceptionHandler;
import com.crisis.resources.Resource;
import com.crisis.services.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/***
 * {@code Crisis} permits to annotate your methods in order to test them
 * <p> {@code Crisis} is capable of running a method with parameters specified in annotation
 * <p>
 * If @CrisisAnnotation is not added to class, program will throw fatal error
 */
@SuppressWarnings("rawtypes")
public class Crisis extends Service implements Resource {

    // callerClass detect the class name which called crisis :
    private final String callerClass;
    private final ArrayList<String> methods = new ArrayList<>();

    CrisisAnnotation crisisAnnotation;

    // Processing Status :
    enum STATE {
        PROCESSED, DISABLED, ERROR
    }

    public Crisis() {

        callerClass = findClass();

        start();
    }

    /***
     * {@code start} process annotated methods from callerClass<p>
     *  Method state can be PROCESS, DISABLED, ERROR
     *
     * <p>{@code start} prints information about priority level,
     * state, method name, return type, return value, parameters & errors if occurred
     */
    private void start() {

        int success = 0, failed = 0, total = 0, disabled = 0;

        Class<?> object;

        try {

            object = Class.forName(callerClass);

            // FATAL ERROR : Class could not be found
        } catch (ClassNotFoundException err) {

            throw new ExceptionHandler("Class could not be found", err);

        }

        // CrisisAnnotation must exist :
        if ( object.isAnnotationPresent(CrisisAnnotation.class) ) {

            crisisAnnotation = object.getAnnotation(CrisisAnnotation.class);

            // Print information :
            if( !crisisAnnotation.silent() )
                System.out.printf("\nProcessing...\n\nClass : %s\ncreatedBy : %s\n\nlastModified : %s\n",
                        callerClass, crisisAnnotation.createdBy(), crisisAnnotation.lastModified());

            // FATAL ERROR : CrisisAnnotation does not exist
        } else {
            System.out.printf("\n%s%s%s", RED, FATAL_ERROR, RESET);
            System.exit(0);
        }

        // For each method in class :
        for(Method method : object.getDeclaredMethods() ) {

            // For each method that has MethodAnnotation :
            if( method.isAnnotationPresent(MethodAnnotation.class) ) {

                // Start timer :
                long startTime = System.nanoTime();
                String color;
                String state;
                String returnValue;
                String parameters;
                String exception;

                // Bypass private & static keywords :
                method.setAccessible(true);

                MethodAnnotation methodAnnotation = method.getAnnotation(MethodAnnotation.class);

                // Method is enabled :
                if( methodAnnotation.enabled() ) {

                    try {

                        // No parameters :
                        if (getParameters(method) == 0) {

                            returnValue = runMethod(methodAnnotation,method, object, null);
                            parameters = "None";

                        }

                        // Parameters :
                        else {

                            Object[] params = new Object[(( methodAnnotation.array() ) ?
                                    methodAnnotation.parameters().length : getParameters(method))];

                            Class[][] parameter_type = new Class[0][];
                            int type_length = getTypes(methodAnnotation);

                            // One type will be used :
                            if (type_length == 1)
                                parameter_type = new Class[][]{methodAnnotation.types()};

                            // Store parameters & convert their type :
                            for (int i = 0; i < (( methodAnnotation.array() ) ?
                                    methodAnnotation.parameters().length : getParameters(method)); i++) {

                                params[i] = setParameters(methodAnnotation,
                                        type_length == 1 ? parameter_type[0][0] : methodAnnotation.types()[i], i);

                            }

                            returnValue = runMethod(methodAnnotation,method, object, params);
                            parameters = Arrays.toString(params);

                        }

                        color = GREEN;
                        state = String.valueOf(STATE.PROCESSED);
                        exception = "None";

                        success++;
                        total++;

                    } // Error occurred :
                     catch ( Throwable ex ) {

                         color = RED;
                         state = String.valueOf(STATE.ERROR);
                         returnValue = "Error";
                         parameters = "Error";
                         exception = String.valueOf(ex);

                        failed++;
                        total++;

                    }

                } // Method is disabled
                else {

                    color = YELLOW;
                    state = String.valueOf(STATE.DISABLED);
                    returnValue = "Disabled";
                    parameters = "Disabled";
                    exception = "None";

                    disabled++;
                    total++;

                }

                long duration = (System.nanoTime() - startTime) / 1000000;

                // Store method data :
                methods.add(tableView(methodAnnotation.priority(),
                        colorizedText(crisisAnnotation, color, state), method.getName(), method.getReturnType(),
                        returnValue, new String[]{parameters},
                        ((duration < 10) ? "0." + duration : String.valueOf(duration)), exception));
            }
        }

        displayData();

        // Display results :
        System.out.printf("\nTotal : %d, %s, %s, %s\n\n",
                total, toString(GREEN, "Successful : " + success),
                toString(RED, "Failed : " + failed), toString(YELLOW, "Disabled : " + disabled));
    }

    /***
     * {@code displayData} creates three arraylists,<p>
     * Detects the priority for each method & saves it to accordingly arraylist<p>
     * Throws NONE_PRIORITY_WARNING if PRIORITY.NONE is being used<p>
     * If priority_order() is LOW display order -> LOW - MEDIUM - HIGH<p>
     * If priority_order() is HIGH display order -> HIGH - MEDIUM - LOW<p>
     * If priority_only() is LOW -> LOW<p>
     * If priority_only() is MEDIUM -> MEDIUM<p>
     * If priority_only() is HIGH -> HIGH
     */
    private void displayData() {

        // Create arraylists to store each method depend on their priority :
        ArrayList<String> LOW_PRIORITY = new ArrayList<>();
        ArrayList<String> MEDIUM_PRIORITY = new ArrayList<>();
        ArrayList<String> HIGH_PRIORITY = new ArrayList<>();

        boolean usingNonePriority = false;

        for(String method : methods) {

            if (method.contains("NONE"))
                usingNonePriority = true;

            else if(method.contains("LOW"))
                LOW_PRIORITY.add(method);

            else if (method.contains("MEDIUM"))
                MEDIUM_PRIORITY.add(method);

            else
                HIGH_PRIORITY.add(method);
        }

        if( usingNonePriority && !crisisAnnotation.suppress_warnings())
            System.out.println(toString(YELLOW, NONE_PRIORITY_WARNING));

        // Print table label :
        System.out.printf("\n%-15s %-15s %-25s %-25s %-25s %-30s %-20s %-20s\n", "Priority", "State",
                "Method name", "Return Type", "Return Value", "Parameters", "Runtime", "Error");

        // Display order -> LOW - MEDIUM - HIGH
        if(crisisAnnotation.priority_order().equals(CrisisAnnotation.PRIORITY.LOW) &&
                crisisAnnotation.priority_only().equals(CrisisAnnotation.PRIORITY.NONE)) {

            displayArrayList(LOW_PRIORITY);
            displayArrayList(MEDIUM_PRIORITY);
            displayArrayList(HIGH_PRIORITY);

            // Display order -> HIGH - MEDIUM - LOW
        } else if( (crisisAnnotation.priority_order().equals(CrisisAnnotation.PRIORITY.HIGH) ||
                crisisAnnotation.priority_order().equals(CrisisAnnotation.PRIORITY.MEDIUM)) &&
                crisisAnnotation.priority_only().equals(CrisisAnnotation.PRIORITY.NONE) ) {

            displayArrayList(HIGH_PRIORITY);
            displayArrayList(MEDIUM_PRIORITY);
            displayArrayList(LOW_PRIORITY);

            // Display only LOW PRIORITY :
        } else if( crisisAnnotation.priority_only().equals(CrisisAnnotation.PRIORITY.LOW) ) {
            displayArrayList(LOW_PRIORITY);

            // Display only MEDIUM PRIORITY :
        } else if( crisisAnnotation.priority_only().equals(CrisisAnnotation.PRIORITY.MEDIUM) ) {
            displayArrayList(MEDIUM_PRIORITY);

            // Display only HIGH PRIORITY :
        } else if( crisisAnnotation.priority_only().equals(CrisisAnnotation.PRIORITY.HIGH) ) {
            displayArrayList(HIGH_PRIORITY);
        }
    }

    /***
     * {@code findClass} detects the class name which called Crisis
     * @return class name if found else an empty string
     */
    private String findClass() {

        StackTraceElement[] Elements = Thread.currentThread().getStackTrace();

        for (int i = 1; i <  Elements.length; i++) {

            StackTraceElement element = Elements[i];

            // Get caller Class :
            if ( !element.getClassName().equals(Crisis.class.getName() )
                    && element.getClassName().indexOf("java.lang.Thread") != 0 ) {
                return element.getClassName();
            }
        }

        return "";
    }

    public static String toString(String color, String text) {
        return String.format("%s%s%s", color, text, RESET);
    }
}
