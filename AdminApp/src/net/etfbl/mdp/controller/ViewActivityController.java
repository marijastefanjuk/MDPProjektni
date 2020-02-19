package net.etfbl.mdp.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.etfbl.mdp.model.ExceptionHandler;
import net.etfbl.mdp.model.LogInLogOutTable;

public class ViewActivityController implements Initializable {

	@FXML
	private TableView<LogInLogOutTable> tabelaLoginLogout;

	@FXML
	private TableColumn<LogInLogOutTable, String> vrijemePrijave;

	@FXML
	private TableColumn<LogInLogOutTable, String> vrijemeOdjave;

	@FXML
	private TableColumn<LogInLogOutTable, String> razlikaVremena;

	public static String user = "";

	public static String BASE_URL = "";
	public static ArrayList<LogInLogOutTable> logs = new ArrayList<>();
	
	private static final String PROPERTIES_FILE = "configuration" + File.separator + "adminApp.properties";
	
	static {
		InputStream input;
		try {
			Properties prop = new Properties();
			input = new FileInputStream(PROPERTIES_FILE);
			prop.load(input);
			BASE_URL = prop.getProperty("REST_SERVICE_URL");
		} catch (FileNotFoundException e1) {
			ExceptionHandler.exceptionRecord(ViewActivityController.class.getName(), e1.getMessage());
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(ViewActivityController.class.getName(), e.getMessage());
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setTabela();
		getActivity();
	}

	public void setTabela() {
		vrijemePrijave.setCellValueFactory(new PropertyValueFactory<>("login"));
		vrijemeOdjave.setCellValueFactory(new PropertyValueFactory<>("logout"));
		razlikaVremena.setCellValueFactory(new PropertyValueFactory<>("timeSpent"));
	}

	public void getActivity() {
		BufferedReader br;
		logs.clear();
		try {
			URL url = new URL(BASE_URL + "getActivityLog/" + user);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			JSONArray rezultat = new JSONArray();
			while ((output = br.readLine()) != null) {
				try {
					JSONObject jsonObject = new JSONObject(output);
					rezultat.put(jsonObject);
					Map<String, Object> map = toMap(jsonObject);
					if (map.size() > 0) {
						for (String key : map.keySet()) {
							String timeLogin = key.replace("T", " ");
							String timeLogout = String.valueOf(map.get(key)).replace("T", " ");
							SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
							formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
							long date1 = formatter.parse(timeLogout).getTime() - formatter.parse(timeLogin).getTime();
							SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm:ss");
							formatterTime.setTimeZone(TimeZone.getTimeZone("GMT"));
							LogInLogOutTable log = new LogInLogOutTable(timeLogin, timeLogout,
									formatterTime.format(date1));
							logs.add(log);
						}
					}
				} catch (JSONException e) {
					ExceptionHandler.exceptionRecord(ViewActivityController.class.getName(), e.getMessage());
					e.printStackTrace();
				} catch (ParseException e) {
					ExceptionHandler.exceptionRecord(ViewActivityController.class.getName(), e.getMessage());
					e.printStackTrace();
				}
			}
			tabelaLoginLogout.getItems().clear();
			tabelaLoginLogout.getItems().addAll(logs);
			conn.disconnect();
			br.close();
		} catch (IOException e) {
			ExceptionHandler.exceptionRecord(ViewActivityController.class.getName(), e.getMessage());
			e.printStackTrace();
		}
		
	}

	public static Map<String, Object> toMap(JSONObject jsonobj) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> keys = jsonobj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = jsonobj.get(key);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;
	}
}
