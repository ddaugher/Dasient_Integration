/*
 * From http://jira.opensymphony.com/browse/QUARTZ-732 - not part of Quartz
 *
 * Added functionality for the caller to pass in a context ClassLoader.
 */
package org.quartz.jobs.ee.ejb;

import java.lang.reflect.Method;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>
 * A <code>Job</code> that invokes a method on an EJB3 EJB. Based on the EJBInvokerJob.
 * </p>
 * <p>
 *
 * Expects the properties corresponding to the following keys to be in the <code>JobDataMap</code> when it executes:
 *
 * <ul>
 * <li><code>EJB_JNDI_NAME_KEY</code>- the JNDI name (location) of the EJB</li>
 * <li><code>EJB_INTERFACE_NAME_KEY</code>- the name of the EJB's business interface</li>
 * <li><code>EJB_METHOD_KEY</code>- the name of the method to invoke on the EJB.</li>
 * <li><code>EJB_ARGS_KEY</code>- an Object[] of the args to pass to the method (optional, if left out, there are no arguments).</li>
 * <li><code>EJB_ARG_TYPES_KEY</code>- a Class[] of the types of the args to pass to the method (optional, if left out, the types will be derived by calling getClass() on each of the arguments).</li>
 * </ul>
 *
 * <br/>
 * The following keys can also be used at need:
 * <ul>
 * <li><code>INITIAL_CONTEXT_FACTORY</code> - the context factory used to build the context.</li>
 * <li><code>PROVIDER_URL</code> - the name of the environment property for specifying configuration information for the service provider to use.</li>
 * </ul>
 * </p>
 * <p>
 * The result of the EJB method invocation will be available to <code>Job/TriggerListener</code>s via
 * <code>{@link org.quartz.JobExecutionContext#getResult()}</code>.
 * </p>
 *
 * @author Adrian Brennan
 * @author Andrew Collins
 * @author James House
 * @author Joel Shellman
 * @author <a href="mailto:bonhamcm@thirdeyeconsulting.com">Chris Bonham</a>
 */
public class EJB3InvokerJob implements Job {

    public static final String CONTEXT_CLASS_LOADER = "contextClassLoader";

    public static final String EJB_JNDI_NAME_KEY = "ejb";

    public static final String EJB_INTERFACE_NAME_KEY = "interfaceName";

    public static final String EJB_METHOD_KEY = "method";

    public static final String EJB_ARG_TYPES_KEY = "argTypes";

    public static final String EJB_ARGS_KEY = "args";

    public static final String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";

    public static final String PROVIDER_URL = "java.naming.provider.url";

    public static final String PRINCIPAL = "java.naming.security.principal";

    public static final String CREDENTIALS = "java.naming.security.credentials";

    private InitialContext initialContext = null;

    public EJB3InvokerJob() {
        // do nothing
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        JobDataMap dataMap = context.getMergedJobDataMap();

        ClassLoader classLoader = (ClassLoader) dataMap.get(CONTEXT_CLASS_LOADER);

        if (classLoader != null) {
            ClassLoader earlierClassLoader = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(classLoader);

                executeJob(context, dataMap);
            } finally {
                Thread.currentThread().setContextClassLoader(earlierClassLoader);
            }
        } else {
            executeJob(context, dataMap);
        }
    }

    @SuppressWarnings("unchecked")
    private void executeJob(JobExecutionContext context, JobDataMap dataMap)
            throws JobExecutionException {

        String ejbJNDIName = dataMap.getString(EJB_JNDI_NAME_KEY);
        String methodName = dataMap.getString(EJB_METHOD_KEY);
        Object[] arguments = (Object[]) dataMap.get(EJB_ARGS_KEY);

        if (null == ejbJNDIName || ejbJNDIName.length() == 0) {
            throw new JobExecutionException("must specify ejb JNDI name");
        }

        if (arguments == null) {
            arguments = new Object[0];
        }

        Object ejb = locateEjb(dataMap);

        Class<?>[] argTypes = (Class<?>[]) dataMap.get(EJB_ARG_TYPES_KEY);

        if (argTypes == null) {
            argTypes = new Class<?>[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                argTypes[i] = arguments[i].getClass();
            }
        }

        try {
            Method methodToExecute = ejb.getClass().getDeclaredMethod(methodName, argTypes);
            Object returnObj = methodToExecute.invoke(ejb, arguments);

            context.setResult(returnObj);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {

            // Don't close jndiContext until after method execution because
            // WebLogic requires context to be open to keep the user credentials
            // available. See JIRA Issue: QUARTZ-401

            if (initialContext != null) {
                try {
                    initialContext.close();
                } catch (NamingException e) {
                    // Ignore any errors closing the initial context
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T locateEjb(JobDataMap dataMap) throws JobExecutionException {

        String ejbJNDIName = dataMap.getString(EJB_JNDI_NAME_KEY);

        Object object = null;

        try {
            initialContext = getInitialContext(dataMap);

            object = initialContext.lookup(ejbJNDIName);

            if (object == null) {
                throw new JobExecutionException("Cannot find " + ejbJNDIName);
            }

        } catch (NamingException e) {
            throw new JobExecutionException(e);
        }

        String ejbInterfaceName = dataMap.getString(EJB_INTERFACE_NAME_KEY);

        Class<?> ejbInterface = null;

        try {
            ejbInterface = Class.forName(ejbInterfaceName);
        } catch (ClassNotFoundException e) {
            throw new JobExecutionException(e);
        }

        if (!ejbInterface.isAssignableFrom(object.getClass())) {
            object = PortableRemoteObject.narrow(object, ejbInterface);
        }

        return (T) object;
    }

    private InitialContext getInitialContext(JobDataMap jobDataMap) throws NamingException {

        Hashtable<String, String> params = new Hashtable<String, String>();

        String initialContextFactory = jobDataMap.getString(INITIAL_CONTEXT_FACTORY);

        if (initialContextFactory != null) {
            params.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        }

        String providerUrl = jobDataMap.getString(PROVIDER_URL);

        if (providerUrl != null) {
            params.put(Context.PROVIDER_URL, providerUrl);
        }

        String principal = jobDataMap.getString(PRINCIPAL);

        if (principal != null) {
            params.put(Context.SECURITY_PRINCIPAL, principal);
        }

        String credentials = jobDataMap.getString(CREDENTIALS);

        if (credentials != null) {
            params.put(Context.SECURITY_CREDENTIALS, credentials);
        }

        return (params.size() == 0) ? new InitialContext() : new InitialContext(params);
    }
}
