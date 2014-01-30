package com.gpigc.dataemitter;

import java.io.File;
import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JvmHook {
	private static final String AGENT_OPTIONS = "com.sun.management.jmxremote";
	private static final String RELATIVE_AGENT_FILE_PATH = "lib" + File.separator + "management-agent.jar";
	private static final String CONNECTOR_ADDRESS_PROPERTY = "com.sun.management.jmxremote.localConnectorAddress";
	private static final String MEMORY_OBJECT_NAME = "java.lang:type=Memory";
	private static final String MEMORY_USAGE_ATTRIBUTE = "HeapMemoryUsage";
	private static final String MEMORY_USED_KEY = "used";
	
	private long pid;
	private MBeanServerConnection serverConnection;
	
	public JvmHook(String appName) throws AppNotRunningException, AttachException, LoadAgentException, ServerConnectionException
	{
		VirtualMachineDescriptor descriptor = getVirtualMachineDescriptor(appName);
		pid = Long.parseLong(descriptor.id());
		
        AttachProvider attachProvider = getSunAttachProvider();

        VirtualMachine virtualMachine;
        try {
			virtualMachine = attachProvider.attachVirtualMachine(descriptor);
		} catch (AttachNotSupportedException | IOException e) {
			throw new AttachException("Failed to attach to JVM", e);
		}
        
        loadAgent(virtualMachine);
        serverConnection = serverConnect(virtualMachine);
	}
	
	public long getPid() {
		return pid;
	}

    public long getUsedMemory() throws Exception {
        ObjectName memory = new ObjectName(MEMORY_OBJECT_NAME);
        CompositeData cd = (CompositeData) serverConnection.getAttribute(memory, MEMORY_USAGE_ATTRIBUTE);
        
        return Long.valueOf((Long)cd.get(MEMORY_USED_KEY));
    }
    
    private VirtualMachineDescriptor getVirtualMachineDescriptor(String appName) throws AppNotRunningException {
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (descriptor.displayName().equals(appName)) {
            	return descriptor;
            }
        }
        
        throw new AppNotRunningException("Test application not running");
    }
    
    private static AttachProvider getSunAttachProvider() throws AttachException {
        for (AttachProvider attachProvider : AttachProvider.providers()) {
            if (attachProvider.name().equals("sun")) {
            	return attachProvider;
            }
        }
        
        throw new AttachException("Sun attach provider not found");
    }
    
    private static void loadAgent(VirtualMachine virtualMachine) throws LoadAgentException {
    	try {
	        String javaHomePath = virtualMachine.getSystemProperties().getProperty("java.home");
	        String agentFilePath = javaHomePath + File.separator + RELATIVE_AGENT_FILE_PATH;
	        virtualMachine.loadAgent(agentFilePath, AGENT_OPTIONS);
    	} catch (IOException | AgentInitializationException | AgentLoadException e) {
    		throw new LoadAgentException("Failed to load agent", e);
    	}
    }
    
    private static MBeanServerConnection serverConnect(VirtualMachine virtualMachine) throws ServerConnectionException {
    	try {
	        String connectorAddress = virtualMachine.getAgentProperties().getProperty(CONNECTOR_ADDRESS_PROPERTY);
	        
	        JMXServiceURL jmxServerAddress = new JMXServiceURL(connectorAddress);
	        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServerAddress);
	        return jmxConnector.getMBeanServerConnection();
    	} catch (IOException e) {
    		throw new ServerConnectionException("Failed to connect to JMX server", e);
    	}
    }
	
	public static class AppNotRunningException extends Exception {
		private static final long serialVersionUID = 1L;
		public AppNotRunningException() {}
		public AppNotRunningException(String message) { super(message); }
		public AppNotRunningException(String message, Exception cause) { super(message, cause); }
	}
	
	public static class AttachException extends Exception {
		private static final long serialVersionUID = 1L;
		public AttachException() {}
		public AttachException(String message) { super(message); }
		public AttachException(String message, Exception cause) { super(message, cause); }
	}
	
	public static class LoadAgentException extends Exception {
		private static final long serialVersionUID = 1L;
		public LoadAgentException() {}
		public LoadAgentException(String message) { super(message); }
		public LoadAgentException(String message, Exception cause) { super(message, cause); }
	}
	
	public static class ServerConnectionException extends Exception {
		private static final long serialVersionUID = 1L;
		public ServerConnectionException() {}
		public ServerConnectionException(String message) { super(message); }
		public ServerConnectionException(String message, Exception cause) { super(message, cause); }
	}
}