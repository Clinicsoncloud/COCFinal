package com.abhaybmicoc_test.app.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorUtils {

    public static void logErrors(Exception e,String fileName,String methodName,String msg){
        File logFile = new File(Environment.DIRECTORY_DOCUMENTS+"/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException ex) {
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(msg);
            buf.newLine();
            buf.close();
        }
        catch (IOException ex) {

        }
    }
}
