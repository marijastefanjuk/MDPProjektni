package org.unibl.etf.mdp.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.unibl.etf.mdp.model.User;
import org.unibl.etf.mdp.model.UserChangePassword;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class UserSetup {
	
	private ArrayList<User> users;
	
	public UserSetup() {
		users=new ArrayList<User>();
	}

	public ArrayList<User> getAllUsers(){
		//ArrayList<User> users=new ArrayList<>();
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		try(Jedis jedis=pool.getResource()){
			Map<String,String> fields=jedis.hgetAll(instanceName);
			for(String key:fields.keySet()) {
				String[] help=key.split("##");
				users.add(new User(help[0],fields.get(key)));
			}
		}
		pool.close();
		return users;
	}
	
	public boolean changeUserPassword(UserChangePassword user) {
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		String active="";
		try(Jedis jedis=pool.getResource()){
			boolean userFound=false;
			Map<String,String> fields=jedis.hgetAll(instanceName);
			for(String key:fields.keySet()) {
				String[] help=key.split("##");
				if(user.getUsername().equals(help[0]) && "1".equals(help[1]) && user.getOldPassword().equals(fields.get(key))) {
					userFound=true;
					active=help[2];
					break;
				}
			}
			if(userFound) {
				jedis.hdel(instanceName, user.getUsername()+"##1");
				jedis.hset(instanceName, user.getUsername()+"##1##"+active, user.getNewPassword());
				return true;
			}
		}
		pool.close();
		return false;
	}
	
	public Map<String,String> getActivityLog(String user){
		JedisPool pool=new JedisPool("localhost");
		Map<String,String> activity=new HashMap<>();
		try(Jedis jedis=pool.getResource()){
			activity=jedis.hgetAll(user);
		}
		pool.close();
		return activity;
	}
	
	public boolean addNewLogForUser(String user,String login,String logout) {
		JedisPool pool=new JedisPool("localhost");
		boolean flag=true;
		try(Jedis jedis=pool.getResource()){
			Map<String,String> allUsers=jedis.hgetAll("users");
			String password=allUsers.get(user+"##1##true");
			jedis.hdel("users", user+"##1##true");
			jedis.hset("users", user+"##1##false",password);
			jedis.hset(user, login, logout);
			
		}catch(Exception e) {
			flag=false;
		}
		pool.close();
		return flag;
	}
	
	public boolean checkIfActive(User user) {
		
		JedisPool pool=new JedisPool("localhost");
		Map<String,String> allUsers;
		try(Jedis jedis=pool.getResource()){
			allUsers=jedis.hgetAll("users");
			for(String key:allUsers.keySet()) {
				String[] help=key.split("##");
				if(user.getUsername().equals(help[0]) && "1".equals(help[1]) && user.getPassword().equals(allUsers.get(key))
						&& "false".equals(help[2])) {
					
					jedis.hdel("users", user.getUsername()+"##1##false");
					jedis.hset("users", user.getUsername()+"##1##true",allUsers.get(key));
					return true;
				}
			}
			
		}
		pool.close();
		return false;
	}
	
	public User getByUsername(String username) {
		JedisPool pool=new JedisPool("localhost");
		try(Jedis jedis=pool.getResource()){
			Map<String,String> allUsers=jedis.hgetAll("users");
			for(String key:allUsers.keySet()) {
				String[] help=key.split("##");
				if(username.equals(help[0]) && "1".equals(help[1])) {
					return new User(key,allUsers.get(key));
				}
			}
		}
		pool.close();
		return null;
	}
}
