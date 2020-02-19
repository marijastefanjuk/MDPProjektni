package net.etfbl.mdp.model;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionHandler {

	private static FileHandler handler;
	private static Logger loger;

	public static void exceptionRecord(String className, String message) {
		loger = Logger.getLogger(className);
		handler = null;
		try {
			handler = new FileHandler("logs"+File.separator+"error.log", true);
			Logger.getLogger("").addHandler(handler);
			loger.log(Level.SEVERE, message);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(ExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			java.util.logging.Logger.getLogger(ExceptionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
