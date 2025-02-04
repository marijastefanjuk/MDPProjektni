/**
 * AddRemoveUserServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package net.etfbl.mdp.soap;

public class AddRemoveUserServiceLocator extends org.apache.axis.client.Service implements net.etfbl.mdp.soap.AddRemoveUserService {

    public AddRemoveUserServiceLocator() {
    }


    public AddRemoveUserServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AddRemoveUserServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AddRemoveUser
    private java.lang.String AddRemoveUser_address = "http://localhost:8080/SoapService/services/AddRemoveUser";

    public java.lang.String getAddRemoveUserAddress() {
        return AddRemoveUser_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AddRemoveUserWSDDServiceName = "AddRemoveUser";

    public java.lang.String getAddRemoveUserWSDDServiceName() {
        return AddRemoveUserWSDDServiceName;
    }

    public void setAddRemoveUserWSDDServiceName(java.lang.String name) {
        AddRemoveUserWSDDServiceName = name;
    }

    public net.etfbl.mdp.soap.AddRemoveUser getAddRemoveUser() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AddRemoveUser_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAddRemoveUser(endpoint);
    }

    public net.etfbl.mdp.soap.AddRemoveUser getAddRemoveUser(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            net.etfbl.mdp.soap.AddRemoveUserSoapBindingStub _stub = new net.etfbl.mdp.soap.AddRemoveUserSoapBindingStub(portAddress, this);
            _stub.setPortName(getAddRemoveUserWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAddRemoveUserEndpointAddress(java.lang.String address) {
        AddRemoveUser_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (net.etfbl.mdp.soap.AddRemoveUser.class.isAssignableFrom(serviceEndpointInterface)) {
                net.etfbl.mdp.soap.AddRemoveUserSoapBindingStub _stub = new net.etfbl.mdp.soap.AddRemoveUserSoapBindingStub(new java.net.URL(AddRemoveUser_address), this);
                _stub.setPortName(getAddRemoveUserWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AddRemoveUser".equals(inputPortName)) {
            return getAddRemoveUser();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://soap.mdp.etfbl.net", "AddRemoveUserService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://soap.mdp.etfbl.net", "AddRemoveUser"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AddRemoveUser".equals(portName)) {
            setAddRemoveUserEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
