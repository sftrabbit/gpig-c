package com.gpigc.dataemitter.testapp1;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.attach.spi.AttachProvider;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Attach {
	
	String agent;
	String pid;
	
	public Attach(String agent, long pid)
	{
		this.agent = agent;
		this.pid = String.valueOf(pid);
	}

    public long getmem() throws Exception {
        AttachProvider attachProvider = AttachProvider.providers().get(0);

        VirtualMachineDescriptor descriptor = null;
        for (VirtualMachineDescriptor virtualMachineDescriptor : attachProvider.listVirtualMachines()) {
            if(virtualMachineDescriptor.id().equals(pid))
            	descriptor = virtualMachineDescriptor;
        }

        VirtualMachine virtualMachine = attachProvider.attachVirtualMachine(descriptor);
        virtualMachine.loadAgent(agent, "com.sun.management.jmxremote");
        Object portObject = virtualMachine.getAgentProperties().get("com.sun.management.jmxremote.localConnectorAddress");

        JMXServiceURL target = new JMXServiceURL(portObject + "");
        JMXConnector connector = JMXConnectorFactory.connect(target);
        MBeanServerConnection remote = connector.getMBeanServerConnection();

        ObjectName memory = new ObjectName("java.lang:type=Memory");
        CompositeData cd = (CompositeData) remote.getAttribute(memory, "HeapMemoryUsage");
        
        return Long.valueOf((Long)cd.get("used"));
    }
}