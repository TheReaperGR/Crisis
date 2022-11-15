package com.crisis.resources;

public interface Resource {

    String RED = "\u001b[31m";
    String GREEN = "\u001b[32m";
    String YELLOW = "\u001b[33m";
    String CYAN =  "\u001b[36m";
    String RESET = "\u001b[0m";

    String FATAL_ERROR = "FATAL ERROR : Setup is done incorrectly\n" +
            "Please add @CrisisAnnotation() to your class";

    String NONE_PRIORITY_WARNING = "WARNING : You are using NONE PRIORITY," +
            " method data won't be displayed !";

    static String toString(String color, String text) {
        return null;
    }

}
