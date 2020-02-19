package net.etfbl.mdp.soap;

public class AddRemoveUserProxy implements net.etfbl.mdp.soap.AddRemoveUser {
  private String _endpoint = null;
  private net.etfbl.mdp.soap.AddRemoveUser addRemoveUser = null;
  
  public AddRemoveUserProxy() {
    _initAddRemoveUserProxy();
  }
  
  public AddRemoveUserProxy(String endpoint) {
    _endpoint = endpoint;
    _initAddRemoveUserProxy();
  }
  
  private void _initAddRemoveUserProxy() {
    try {
      addRemoveUser = (new net.etfbl.mdp.soap.AddRemoveUserServiceLocator()).getAddRemoveUser();
      if (addRemoveUser != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)addRemoveUser)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)addRemoveUser)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (addRemoveUser != null)
      ((javax.xml.rpc.Stub)addRemoveUser)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public net.etfbl.mdp.soap.AddRemoveUser getAddRemoveUser() {
    if (addRemoveUser == null)
      _initAddRemoveUserProxy();
    return addRemoveUser;
  }
  
  public boolean addUser(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException{
    if (addRemoveUser == null)
      _initAddRemoveUserProxy();
    return addRemoveUser.addUser(username, password);
  }
  
  public boolean blockUser(java.lang.String username) throws java.rmi.RemoteException{
    if (addRemoveUser == null)
      _initAddRemoveUserProxy();
    return addRemoveUser.blockUser(username);
  }
  
  
}