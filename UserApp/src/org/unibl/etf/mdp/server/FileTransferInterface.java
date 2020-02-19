package org.unibl.etf.mdp.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.healthmarketscience.rmiio.RemoteInputStream;

public interface FileTransferInterface extends Remote {

	public void uploadFileToServer(byte[] mybyte, String username, int length,String fileName) throws RemoteException;
	
	public RemoteInputStream downloadFileFromServer(String username,String fileName) throws RemoteException;
	
	public void uploadFileToServerRMIIO(RemoteInputStream ristream, String username, String fileName) throws RemoteException;
	
	public ArrayList<String> getFilesForUser(String username) throws RemoteException;
}
