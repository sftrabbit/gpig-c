package uk.co.gpigc.emitter.testapp1;

import java.io.File;
import java.io.IOException;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Connects to a Java Virtual Machine to monitor internal information about a
 * running Java application.
 */
public class JavaVirtualMachineMonitor {
	private static final String JAVA_HOME_PROPERTY = "java.home";
	private static final String ATTACH_PROVIDER_NAME = "sun";
	private static final String AGENT_OPTIONS = "com.sun.management.jmxremote";
	private static final String RELATIVE_AGENT_FILE_PATH = "lib"
			+ File.separator + "management-agent.jar";
	private static final String CONNECTOR_ADDRESS_PROPERTY = "com.sun.management.jmxremote.localConnectorAddress";
	private static final String MEMORY_OBJECT_NAME = "java.lang:type=Memory";
	private static final String MEMORY_USAGE_ATTRIBUTE = "HeapMemoryUsage";
	private static final String MEMORY_USED_KEY = "used";

	/**
	 * Process ID of the attached JVM.
	 */
	private long processId;

	/**
	 * Connection to the JVM.
	 */
	private MBeanServerConnection serverConnection;

	/**
	 * Monitor the JVM running the Java application with the given name.
	 * 
	 * @param appName
	 *            Name of the application being run by the JVM
	 * @throws AppNotRunningException
	 *             There is no JVM running the requested application
	 * @throws AttachException
	 *             Unable to attach to the JVM
	 * @throws LoadAgentException
	 *             Unable to load an agent into the JVM
	 * @throws ServerConnectionException
	 *             Unable to set up a connection to the JVM
	 */
	public JavaVirtualMachineMonitor(String appName)
			throws AppNotRunningException, AttachException, LoadAgentException,
			ServerConnectionException {
		VirtualMachineDescriptor descriptor = getVirtualMachineDescriptor(appName);
		processId = Long.parseLong(descriptor.id());

		connectVirtualMachine(descriptor);
	}

	/**
	 * Monitor the JVM with the given process ID.
	 * 
	 * @param processId
	 *            Process ID of the JVM
	 * @throws AppNotRunningException
	 *             There is no JVM running the requested application
	 * @throws AttachException
	 *             Unable to attach to the JVM
	 * @throws LoadAgentException
	 *             Unable to load an agent into the JVM
	 * @throws ServerConnectionException
	 *             Unable to set up a connection to the JVM
	 */
	public JavaVirtualMachineMonitor(long processId) throws MonitorJvmException {
		this.processId = processId;

		VirtualMachineDescriptor descriptor = getVirtualMachineDescriptor(processId);

		connectVirtualMachine(descriptor);
	}

	/**
	 * @return Process ID of the attached JVM.
	 */
	public long getProcessId() {
		return processId;
	}

	/**
	 * Get current heap memory usage (in bytes) of the Java application running
	 * in the attached JVM.
	 * 
	 * @return Heap memory usage of Java application
	 * @throws ServerFetchException
	 *             Unable to fetch memory object from JMX server
	 */
	public long getUsedMemory() throws ServerFetchException {
		ObjectName memoryObjectName;
		CompositeData compositeData;
		try {
			memoryObjectName = new ObjectName(MEMORY_OBJECT_NAME);
			compositeData = (CompositeData) serverConnection.getAttribute(
					memoryObjectName, MEMORY_USAGE_ATTRIBUTE);
		} catch (MalformedObjectNameException | AttributeNotFoundException
				| InstanceNotFoundException | MBeanException
				| ReflectionException | IOException e) {
			throw new ServerFetchException(
					"Failed to fetch memory object from JMX server", e);
		}

		return Long.valueOf((Long) compositeData.get(MEMORY_USED_KEY));
	}

	/**
	 * Attach and connect to the requested virtual machine.
	 * 
	 * @param descriptor
	 *            Descriptor of the JVM to connect to.
	 * @throws AttachException
	 *             Unable to attach to the JVM
	 * @throws LoadAgentException
	 *             Unable to load an agent into the JVM
	 * @throws ServerConnectionException
	 *             Unable to set up a connection to the JVM
	 */
	private void connectVirtualMachine(VirtualMachineDescriptor descriptor)
			throws AttachException, LoadAgentException,
			ServerConnectionException {
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

	/**
	 * Gets the virtual machine descriptor for the JVM running the application
	 * with the given name.
	 * 
	 * @param appName
	 *            Name of the application
	 * @return Descriptor of the JVM running the requested application
	 * @throws AppNotRunningException
	 *             There is no JVM running the requested application
	 */
	private static VirtualMachineDescriptor getVirtualMachineDescriptor(
			String appName) throws AppNotRunningException {
		for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
			System.out.println(descriptor.displayName());
			if (descriptor.displayName().endsWith(appName)) {
				return descriptor;
			}
		}

		throw new AppNotRunningException("Application " + appName
				+ " not running");
	}

	/**
	 * Gets the virtual machine descriptor for the JVM with the given process
	 * ID.
	 * 
	 * @param processId
	 *            JVM process ID
	 * @return Descriptor of the JVM with given process ID
	 * @throws AppNotRunningException
	 *             There is no JVM with the given process ID
	 */
	private static VirtualMachineDescriptor getVirtualMachineDescriptor(
			long processId) throws AppNotRunningException {
		for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
			if (descriptor.id().equals(processId)) {
				return descriptor;
			}
		}

		throw new AppNotRunningException("Application with process ID "
				+ processId + " not running");
	}

	/**
	 * Get the Sun attach provider, which can be used to attach to a JVM.
	 * 
	 * @return Sun attach provider
	 * @throws AttachException
	 *             Unable to attach to the JVM
	 */
	private static AttachProvider getSunAttachProvider() throws AttachException {
		for (AttachProvider attachProvider : AttachProvider.providers()) {
			if (attachProvider.name().equals(ATTACH_PROVIDER_NAME)) {
				return attachProvider;
			}
		}

		throw new AttachException(
				"Sun attach provider not installed on this machine");
	}

	/**
	 * Load JMX agent into the given virtual machine.
	 * 
	 * @param virtualMachine
	 *            JVM to attach JMX agent to
	 * @throws LoadAgentException
	 *             Unable to load an agent into the JVM
	 */
	private static void loadAgent(VirtualMachine virtualMachine)
			throws LoadAgentException {
		try {
			String javaHomePath = virtualMachine.getSystemProperties()
					.getProperty(JAVA_HOME_PROPERTY);
			String agentFilePath = javaHomePath + File.separator
					+ RELATIVE_AGENT_FILE_PATH;
			virtualMachine.loadAgent(agentFilePath, AGENT_OPTIONS);
		} catch (IOException | AgentInitializationException
				| AgentLoadException e) {
			throw new LoadAgentException("Failed to load agent: "
					+ RELATIVE_AGENT_FILE_PATH, e);
		}
	}

	/**
	 * Connect to the JMX agent loaded into the given JVM to be able to retrieve
	 * information about the running Java application.
	 * 
	 * @param virtualMachine
	 *            JVM to connect to
	 * @return Server connection
	 * @throws ServerConnectionException
	 *             Unable to set up a connection to the JVM
	 */
	private static MBeanServerConnection serverConnect(
			VirtualMachine virtualMachine) throws ServerConnectionException {
		try {
			String connectorAddress = virtualMachine.getAgentProperties()
					.getProperty(CONNECTOR_ADDRESS_PROPERTY);

			JMXServiceURL jmxServerAddress = new JMXServiceURL(connectorAddress);
			JMXConnector jmxConnector = JMXConnectorFactory
					.connect(jmxServerAddress);
			return jmxConnector.getMBeanServerConnection();
		} catch (IOException e) {
			throw new ServerConnectionException(
					"Failed to connect to JMX server", e);
		}
	}

	/**
	 * Super class for all exceptions that might occur while attempting to
	 * monitor a JVM.
	 * 
	 */
	public static class MonitorJvmException extends Exception {
		private static final long serialVersionUID = 1L;

		public MonitorJvmException() {
		}

		public MonitorJvmException(String message) {
			super(message);
		}

		public MonitorJvmException(String message, Exception cause) {
			super(message, cause);
		}
	};

	/**
	 * Thrown if the application that has been requested to be monitored is not
	 * currently running in any available JVM.
	 */
	public static class AppNotRunningException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public AppNotRunningException() {
		}

		public AppNotRunningException(String message) {
			super(message);
		}

		public AppNotRunningException(String message, Exception cause) {
			super(message, cause);
		}
	}

	/**
	 * Thrown if unable to attach to a JVM.
	 */
	public static class AttachException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public AttachException() {
		}

		public AttachException(String message) {
			super(message);
		}

		public AttachException(String message, Exception cause) {
			super(message, cause);
		}
	}

	/**
	 * Thrown if unable to load an agent into a JVM.
	 */
	public static class LoadAgentException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public LoadAgentException() {
		}

		public LoadAgentException(String message) {
			super(message);
		}

		public LoadAgentException(String message, Exception cause) {
			super(message, cause);
		}
	}

	/**
	 * Thrown if unable to connect to the JMX agent server.
	 */
	public static class ServerConnectionException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public ServerConnectionException() {
		}

		public ServerConnectionException(String message) {
			super(message);
		}

		public ServerConnectionException(String message, Exception cause) {
			super(message, cause);
		}
	}

	/**
	 * Thrown if unable to retrieve an object from the JMX agent server.
	 */
	public static class ServerFetchException extends MonitorJvmException {
		private static final long serialVersionUID = 1L;

		public ServerFetchException() {
		}

		public ServerFetchException(String message) {
			super(message);
		}

		public ServerFetchException(String message, Exception cause) {
			super(message, cause);
		}
	}
}