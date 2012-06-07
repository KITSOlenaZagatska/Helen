package kf.axis.config;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import kf.core.logging.Logger;

// changes from repository

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.deployment.repository.util.ArchiveReader;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;

public class AxisConfig {

    private ConfigurationContext configurationContext;
    private String axis2ConfigResource = "axis2config.xml";
    private String servicesConfigResource = "services.xml";
    private String serviceGroupName;

    public MessageContext createMessageContext() {
        return getConfigurationContext().createMessageContext();
    }

    public AxisService getServiceByName(final String serviceName) {
        final Map<String, AxisService> services = getConfigurationContext().getAxisConfiguration().getServices();
        return services.get(serviceName);
    }

    public void init() {
        if(getConfigurationContext() == null) {
            doInit();
        }
    }

     @SuppressWarnings("rawtypes")
    private void doInit() {
        try {
            setConfigurationContext(ConfigurationContextFactory
                    .createBasicConfigurationContext(getAxis2ConfigResource()));

            InputStream configInputStream = null;
            ClassLoader classLoader;
            classLoader = Thread.currentThread().getContextClassLoader();
            try {
                configInputStream = classLoader.getResourceAsStream(getServicesConfigResource());
                AxisServiceGroup service; 
                // TODO: are nulls possible? 
                service = DeploymentEngine.buildServiceGroup(configInputStream, classLoader, getServiceGroupName(),
                        getConfigurationContext(), new ArchiveReader(), new HashMap());
                getConfigurationContext().getAxisConfiguration().addServiceGroup(service);
            }
            finally {
                if(configInputStream != null) {
                    configInputStream.close();
                }
            }

        }
        catch(final AxisFault exc) {
            Logger.out.exception(exc);
        }
        catch(final Exception exc) {
            Logger.out.exception(exc);
        }
    }

    public String getAxis2ConfigResource() {
        return this.axis2ConfigResource;
    }

    public void setAxis2ConfigResource(final String axis2ConfigResource) {
        this.axis2ConfigResource = axis2ConfigResource;
    }

    public String getServicesConfigResource() {
        return this.servicesConfigResource;
    }

    public void setServicesConfigResource(final String servicesConfigResource) {
        this.servicesConfigResource = servicesConfigResource;
    }

    public String getServiceGroupName() {
        return this.serviceGroupName;
    }

    public void setServiceGroupName(final String serviceGroupName) {
        this.serviceGroupName = serviceGroupName;
    }

    public ConfigurationContext getConfigurationContext() {
        return this.configurationContext;
    }

    public void setConfigurationContext(final ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

}
