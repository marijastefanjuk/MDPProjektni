package net.etfbl.mdp.controller;
	
import java.io.IOException;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import net.etfbl.mdp.model.ExceptionHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//BorderPane root = new BorderPane();
			//Scene scene = new Scene(root,400,400);
			Parent korisnikView = FXMLLoader.load(getClass().getResource("/net/etfbl/mdp/gui/AdminStart.fxml"));
			Scene scene=new Scene(korisnikView);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(IOException e) {
			ExceptionHandler.exceptionRecord(Main.class.getName(), e.getMessage());
		}
	}
	
	public static void addUsers() {
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		try(Jedis jedis=pool.getResource()){
			//jedis.del(instanceName);
			//jedis.del("marija");
			//jedis.del("jelena");
			//jedis.del("korisnik");
			//jedis.del("test");
			//jedis.del("test2");
			//jedis.hdel(instanceName, "marija##1##true");
			///jedis.hdel(instanceName, "jelena##1##true");
			//jedis.hdel(instanceName, "korisnik##1##true");
			jedis.hset(instanceName,"marija##1##false","marija");
			jedis.hset(instanceName,"korisnik##1##false","korisnik");
			//jedis.hdel(instanceName, "test##1##true");
			jedis.hset(instanceName,"test##1##false","test");
			
			//jedis.del(instanceName);
		}
		pool.close();
	}
	
	public static void main(String[] args) {
		//addUsers();
		launch(args);
	}
}
