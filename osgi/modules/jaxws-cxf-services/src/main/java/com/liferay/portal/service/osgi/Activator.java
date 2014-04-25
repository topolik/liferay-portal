package com.liferay.portal.service.osgi;

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.service.osgi.cxf.LiferayCXFOSGiServlet;
import com.liferay.portal.service.remote.JAXWSService2;
import com.liferay.portal.service.remote.JAXWSServiceImpl2;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws22.spi.ProviderImpl;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.policy.attachment.external.patched.ExternalAttachmentProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class Activator implements BundleActivator, ServiceListener {
	// todo - test
	private ServiceRegistration _serviceRegistration;
	// TODO: until we have the right @REMOTE annotation, use JSONWebService annotation
	private static final Class PORTAL_REMOTE_SERVICE_ANNOTATION = JSONWebService.class;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		try {
			_bundleContext = bundleContext;
			_cxfServlet = new LiferayCXFOSGiServlet();

			initHTTPServer();

			try {
				initExternalAttachmentsProvider();

				registerPortalServices();

				// TODO: simple test if it works with correct @WebService
				JAXWSService2 service = new JAXWSServiceImpl2();
				_serviceRegistration = _bundleContext.registerService(
					service.getClass().getName(), service, null);

			} catch (Throwable e) {
				stop(_bundleContext);
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(BundleContext bundleContext) {
		if (_endpoints != null) {
			for (Endpoint endpoint : _endpoints) {
				endpoint.stop();
			}
			_endpoints = null;
		}

		if (_httpServer != null) {
			try {
				_httpServer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (_serviceRegistration != null) {
			_bundleContext.ungetService(_serviceRegistration.getReference());
		}

		_httpServer = null;

	}

	public void serviceChanged(ServiceEvent ev) {
		ServiceReference sr = ev.getServiceReference();
		switch(ev.getType()) {
			case ServiceEvent.REGISTERED:
			{
				registerPortalService(sr);
			}
			break;
			case ServiceEvent.UNREGISTERING:
			{
				unregisterPortalService(sr);
			}
			break;
		}
	}

	protected void registerPortalService(ServiceReference serviceReference) {
		Object service = _bundleContext.getService(serviceReference);

		Object webService = service;
		if (hasAnnotation(service.getClass(), PORTAL_REMOTE_SERVICE_ANNOTATION)) {
			webService = wrapPortalService(service);

			//TODO: JAXWS portal service wrapping not implemented yet
			return;
		}
		else if (!hasAnnotation(service.getClass(), WebService.class)) {
			return;
		}

		// register our Bus instance into ThreadLocal for the provider
		BusFactory.setThreadDefaultBus(_cxfServlet.getBus());

		// bad bad ugly thing - call private CXF API to get Provider
		ProviderImpl provider = new ProviderImpl();

		Endpoint endpoint = provider.createEndpoint(null, webService);

		addEndpointSecurityProperties(endpoint);

		StringBuilder url = new StringBuilder();
		url.append("/");
		url.append(serviceReference.getBundle().getSymbolicName());
		url.append("/");
		url.append(serviceReference.getBundle().getVersion().toString());
		url.append("/");
		url.append(service.getClass().getName());

		endpoint.publish(url.toString());

		_endpoints.add(endpoint);
	}

	private Object wrapPortalService(Object service) {
		// Note: Portal service is already ServiceBeanAopProxy

		// now we should add @WebService annotation,
		// probably by creating a new proxy with some interface
		// the interface would have the annotation

		// Because:
		// annotations can be added only by bytecode manipulation
		// => change bytecode of the service class to add the annotation
		// => generate new class object
		// => create new instance of the modified class
		// => register again all aspects on the instance
		// ^^ dirty, dirty, dirty, ugly thing

		// Q: is CXF able to get web service methods if the class is a proxy?
		// A: probably not, let's try first :(

		// I guess, in the end of the day we end up by creating a new interface
		// using bytecode manipulation to:
		// * generate the @WebService annotation
		// * generate all the service methods into the interface so that CXF
		//   is able to know the web service methods
		// Then we create a new proxy of the service with the new interface


		return null;
	}

	private void unregisterPortalService(ServiceReference serviceReference) {
		Object service = _bundleContext.getService(serviceReference);

		if (!hasAnnotation(service.getClass(), WebService.class) &&
			!hasAnnotation(service.getClass(), PORTAL_REMOTE_SERVICE_ANNOTATION)) {

			return;
		}

		for (Iterator<Endpoint> it = _endpoints.iterator(); it.hasNext();){
			Endpoint endpoint = it.next();

			if (service.equals(endpoint.getImplementor())) {
				endpoint.stop();
				it.remove();
			}
		}
	}

	protected boolean hasAnnotation(Class cls, Class annotation) {
		if (cls == null) {
			return false;
		}
		if (null != cls.getAnnotation(annotation)) {
			return true;
		}
		for (Class inf : cls.getInterfaces()) {
			if (null != inf.getAnnotation(annotation)) {
				return true;
			}
		}

		return hasAnnotation(cls.getSuperclass(), annotation);
	}

	protected void registerPortalServices() throws InvalidSyntaxException {
		String filter = "(objectclass=*)";
		_bundleContext.addServiceListener(this, filter);

		ServiceReference[] existingServices =
			_bundleContext.getServiceReferences(null, filter);

		if (existingServices != null) {
			for (int i = 0; i < existingServices.length; i++) {
				ServiceEvent artificialEvent = new ServiceEvent(
						ServiceEvent.REGISTERED, existingServices[i]);

				serviceChanged(artificialEvent);
			}
		}
	}

	protected void initExternalAttachmentsProvider() throws Exception {
		// register our Bus instance into ThreadLocal for the provider
		BusFactory.setThreadDefaultBus(_cxfServlet.getBus());

		ExternalAttachmentProvider externalAttachmentProvider =
			new ExternalAttachmentProvider(_cxfServlet.getBus());
		externalAttachmentProvider.setLocation(
			getClass().getResource("/policy-attachment.xml"));
	}


	protected void initHTTPServer() throws Exception {
		ServletHolder servlet = new ServletHolder(_cxfServlet);
		servlet.setName("soap");
		servlet.setForcedPath("soap");

		_httpServer = new Server(11112);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		_httpServer.setHandler(contexts);
		ServletContextHandler root = new ServletContextHandler(contexts, "/", ServletContextHandler.SESSIONS);

		root.addServlet(servlet, "/soap/*");

		_httpServer.start();
	}

	protected void addEndpointSecurityProperties(Endpoint endpoint) {
		// register WS-Policy to be used with the endpoint
		((EndpointImpl) endpoint).getFeatures().add(new WSPolicyFeature());

		// WSS4J security configuration for JAX-WS
		Map<String, Object> jaxWSProps = endpoint.getProperties();
		jaxWSProps.put("ws-security.callback-handler", new ServiceKeystorePasswordCallback());
		jaxWSProps.put("ws-security.encryption.properties", "serviceKeystore.properties");
		jaxWSProps.put("ws-security.signature.properties", "serviceKeystore.properties");
		jaxWSProps.put("ws-security.encryption.username", "myservicekey");
	}

	private BundleContext _bundleContext;
	private LiferayCXFOSGiServlet _cxfServlet;
	private List<Endpoint> _endpoints = new ArrayList<Endpoint>();
	private Server _httpServer;

}