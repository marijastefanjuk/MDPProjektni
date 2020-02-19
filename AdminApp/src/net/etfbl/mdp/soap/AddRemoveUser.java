/**
 * AddRemoveUser.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.mdp.soap;

public interface AddRemoveUser extends java.rmi.Remote {
    public boolean addUser(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public boolean blockUser(java.lang.String username) throws java.rmi.RemoteException;
}
