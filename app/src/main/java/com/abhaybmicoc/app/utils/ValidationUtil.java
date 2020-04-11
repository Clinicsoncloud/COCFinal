package com.abhaybmicoc.app.utils;

import java.util.*;

public class ValidationUtil {

    // Java program to compare two version number

    // Method to compare two versions. Returns 1 if v2 is
    // smaller, -1 if v1 is smaller, 0 if equal
    public static int versionCompare(String version1, String version2) {
        // vnum stores each numeric part of version
        int vnum1 = 0, vnum2 = 0;

        // loop untill both String are processed
        for (int i = 0, j = 0; (i < version1.length() || j < version2.length()); ) {
            // storing numeric part of version 1 in vnum1
            while (i < version1.length() && version1.charAt(i) != '.') {
                vnum1 = vnum1 * 10 + (version1.charAt(i) - '0');
                i++;
            }

            // storing numeric part of version 2 in vnum2
            while (j < version2.length() && version2.charAt(j) != '.') {
                vnum2 = vnum2 * 10 + (version2.charAt(j) - '0');
                j++;
            }

            if (vnum1 > vnum2)
                return 1;
            if (vnum2 > vnum1)
                return -1;

            // if equal, reset variables and go for next numeric
            // part
            vnum1 = vnum2 = 0;
            i++;
            j++;
        }
        return 0;
    }

        /*// Driver method to check above comparison function
        public static void main(String[] args)
        {
            String version1 = "1.0.3";
            String version2 = "1.0.7";

            if (versionCompare(version1, version2) < 0)
                System.out.println(version1 +" is smaller");
            else if (versionCompare(version1, version2) > 0)
                System.out.println(version2 +" is smaller");
            else
                System.out.println("Both version are equal");
        }
    }*/

// This code is contributed by shivanisinghss2110

}