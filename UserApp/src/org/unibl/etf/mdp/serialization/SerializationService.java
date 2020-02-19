package org.unibl.etf.mdp.serialization;

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.unibl.etf.mdp.model.ExceptionHandler;
import org.unibl.etf.mdp.service.UserChat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.google.gson.Gson;

public class SerializationService {
	
	private static final String PROPERTIES_FILE = "configuration"+File.separator+"user-configuration.properties";
	public static String SERIALIZATION_FOLDER="";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			SERIALIZATION_FOLDER = prop.getProperty("SERIALIZATION_FOLDER");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(UserChat.class.getName(), e.getMessage());
		}
	}
	
	public static void serializeWithGson(String message,String username) {
		Gson gson = new Gson();
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		Date date = new Date();
		String datef = dateFormat.format(date);
		if(!(new File(SERIALIZATION_FOLDER+File.separator+username).exists())) {
			File f=new File(SERIALIZATION_FOLDER+File.separator+username);
			f.mkdir();
		}
		try {
			FileWriter out = new FileWriter(new File(SERIALIZATION_FOLDER+File.separator+username+File.separator+"gson"+datef+".out"));
			out.write(gson.toJson(message));
			out.close();
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(SerializationService.class.getName(), e.getMessage());
		}
	}
	
	/*public static void deserializeWithGson(boolean printData) {
		Gson gson = new Gson();

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(SERIALIZATION_FOLDER+File.separator+"gson.out")));
			String data = gson.fromJson(in.readLine(), String.class);
			if (printData)
				System.out.println(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public static void serializeWithKryo(String message,String username) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		Date date = new Date();
		String datef = dateFormat.format(date);
		if(!(new File(SERIALIZATION_FOLDER+File.separator+username).exists())) {
			File f=new File(SERIALIZATION_FOLDER+File.separator+username);
			f.mkdir();
		}
		Kryo kryo = new Kryo();
		kryo.register(String.class);
		try {
			Output out = new Output(new FileOutputStream(new File(SERIALIZATION_FOLDER+File.separator+username+File.separator+"kryo"+datef+".out")));
			kryo.writeClassAndObject(out, message);
			out.close();
		} catch (Exception e) {
			ExceptionHandler.exceptionRecord(SerializationService.class.getName(), e.getMessage());
		}
	}

	/*public static void deserializeWithKryo(boolean printData) {
		Kryo kryo = new Kryo();
		kryo.register(String.class);
		try {
			Input in = new Input(new FileInputStream(new File(SERIALIZATION_FOLDER+File.separator+"kryo.out")));
			String data = (String) kryo.readClassAndObject(in);
			if (printData)
				System.out.println(data);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static void serializeWithJava(String message,String username) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		Date date = new Date();
		String datef = dateFormat.format(date);
		if(!(new File(SERIALIZATION_FOLDER+File.separator+username).exists())) {
			File f=new File(SERIALIZATION_FOLDER+File.separator+username);
			f.mkdir();
		}
		try {
			FileOutputStream fileOut = new FileOutputStream(SERIALIZATION_FOLDER+File.separator+username+File.separator+"java"+datef+".out");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(message);
			out.close();
		} catch (Exception e) {
			ExceptionHandler.exceptionRecord(SerializationService.class.getName(), e.getMessage());
		}
	}

	/*public static void deserializeWithJava(boolean printData) {
		try {
			FileInputStream fileIn = new FileInputStream(SERIALIZATION_FOLDER+File.separator+"java.out");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			String data = (String) in.readObject();
			if (printData)
					System.out.println(data);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public static void serializeWithXML(String message,String username) {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
		Date date = new Date();
		String datef = dateFormat.format(date);
		if(!(new File(SERIALIZATION_FOLDER+File.separator+username).exists())) {
			File f=new File(SERIALIZATION_FOLDER+File.separator+username);
			f.mkdir();
		}
		try {
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(new File(SERIALIZATION_FOLDER+File.separator+username+File.separator+"xml"+datef+".out")));
			encoder.writeObject(message);
			encoder.close();
		} catch (Exception e) {
			ExceptionHandler.exceptionRecord(SerializationService.class.getName(), e.getMessage());
		}
	}

	/*public static void deserializeWithXML(boolean printData) {
		try {
			XMLDecoder decoder = new XMLDecoder(new FileInputStream(new File(SERIALIZATION_FOLDER+File.separator+"xml.out")));
			String data = (String) decoder.readObject();
			if (printData)
				System.out.println(data);
			decoder.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
