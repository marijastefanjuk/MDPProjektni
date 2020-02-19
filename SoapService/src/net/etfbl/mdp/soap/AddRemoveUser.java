package net.etfbl.mdp.soap;

import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class AddRemoveUser {

	public boolean addUser(String username,String password) {
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		try(Jedis jedis=pool.getResource()){
			
			Map<String,String> fields=jedis.hgetAll(instanceName);
			boolean userExists=false;
			for(String key:fields.keySet()) {
				String[] help=key.split("##");
				String user=help[0];
				if(username.equals(user)) {
					userExists=true;
					break;
				}
			}
			if(userExists)
				return false;
			else {
				jedis.hset(instanceName, username+"##1##false", password);
			}
		}
		pool.close();
		return true;
	}
	
	public boolean blockUser(String username) {
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		String active="";
		try(Jedis jedis=pool.getResource()){
			
			boolean userActive=false;
			Map<String,String> fields=jedis.hgetAll(instanceName);
			String password="";
			for(String key:fields.keySet()) {
				String[] help=key.split("##");
				if(username.equals(help[0]) && "1".equals(help[1])) {
					userActive=true;
					password=fields.get(key);
					active=help[2];
					break;
				}
			}
			if(userActive) {
				jedis.hdel(instanceName, username+"##1##"+active);
				jedis.hset(instanceName, username+"##0##false", password);
				return true;
			}
		}
		pool.close();
		return false;
	}
	
	/*public static void main(String[] args) {
		AddRemoveUser add=new AddRemoveUser();
		//boolean uspjesno=add.addUser("marija", "marija");
		//boolean uspjesno1=add.addUser("marija2", "marija2");
		
		JedisPool pool=new JedisPool("localhost");
		String instanceName="users";
		try(Jedis jedis=pool.getResource()){
			
			Map<String,String> fields=jedis.hgetAll(instanceName);
			for(String key:fields.keySet()) {
				System.out.println(key);
			}
		}
		
		boolean remove=add.blockUser("marija2");
		System.out.println("After remove");
		try(Jedis jedis=pool.getResource()){
			
			Map<String,String> fields=jedis.hgetAll(instanceName);
			for(String key:fields.keySet()) {
				System.out.println(key);
			}
			//jedis.del(instanceName);
		}
		pool.close();
	}*/

}
