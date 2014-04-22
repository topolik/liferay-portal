package com.liferay.portal.service.osgi;

import com.liferay.portal.service.osgi.cxf.LiferayCXFOSGiServlet;
import com.liferay.portal.service.osgi.cxf.externalpolicy.LiferayWSPolicyFeature;
import com.liferay.portal.service.osgi.cxf.externalpolicy.LiferayExternalAttachmentProvider;
import com.liferay.portal.service.remote.JAXWSServiceImpl;
import com.liferay.portal.service.remote.JAXWSServiceImpl1;
import com.liferay.portal.service.remote.JAXWSServiceImpl2;
import org.apache.cxf.BusFactory;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws22.spi.ProviderImpl;
//import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
//import org.apache.ws.security.handler.WSHandlerConstants;
//import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
//import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Tomas Polesovsky
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		try {
			_bundleContext = bundleContext;

			_cxfServlet = new LiferayCXFOSGiServlet();

			initHTTPServer();

			try {
				initCXFEnv();
			} catch (Exception e) {
				stop(_bundleContext);
				e.printStackTrace();
			}
		} catch (Exception e) {
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

		_httpServer = null;
	}

	protected void initCXFEnv() throws Exception {

		_endpoints = new ArrayList<Endpoint>();

		// register our Bus instance into ThreadLocal for the provider
		BusFactory.setThreadDefaultBus(_cxfServlet.getBus());

		// bad bad ugly thing - call private CXF API to get Provider
		ProviderImpl provider = new ProviderImpl();

		// another bad bad ugly thing
		// CXF's WSPolicyFeature is a package-private class and cannot be used
		// externally, only from Spring XML config :(
		LiferayWSPolicyFeature wsPolicyFeature = new LiferayWSPolicyFeature();

		// LiferayExternalAttachmentProvider ...
		// Copy of CXF's ExternalAttachmentProvider adapted to non-Spring env
		// Registers itself as a policy provider for this CXF bus
		LiferayExternalAttachmentProvider externalAttachmentProvider =
			new LiferayExternalAttachmentProvider(_cxfServlet.getBus());
		externalAttachmentProvider.setLocation(
			getClass().getResource("/policy-attachment.xml"));

		Endpoint endpoint;

		/*
		 * A service using WS-Policy using annotations with specific WS-Policy
		 * files
		 */
		JAXWSServiceImpl serviceImpl = new JAXWSServiceImpl();
		endpoint = provider.createEndpoint(null, serviceImpl);
		addEndpointProperties(endpoint, wsPolicyFeature);
		endpoint.publish("/service");
		_endpoints.add(endpoint);

		/*
		 * A service using PortalWSPolicy class to find & load WS-Policy file
		 */
		JAXWSServiceImpl1 serviceImpl1 = new JAXWSServiceImpl1();
		endpoint = provider.createEndpoint(null, serviceImpl1);
		addEndpointProperties(endpoint, wsPolicyFeature);
		endpoint.publish("/service1");
		_endpoints.add(endpoint);

		/*
		 * A service that is configured using LiferayExternalAttachmentProvider,
		 * there is no direct reference to WS-Policy from the service itself
		 *  => WIN!
		 */
		JAXWSServiceImpl2 serviceImpl2 = new JAXWSServiceImpl2();
		endpoint = provider.createEndpoint(null, serviceImpl2);
		addEndpointProperties(endpoint, wsPolicyFeature);
		endpoint.publish("/service2");
		_endpoints.add(endpoint);
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

	protected void addEndpointProperties(Endpoint endpoint, LiferayWSPolicyFeature wsPolicyFeature) {
		// register WS-Policy to be used with the endpoint
		((EndpointImpl) endpoint).getFeatures().add(wsPolicyFeature);

		// WSS4J security configuration for JAX-WS
		Map<String, Object> jaxWSProps = endpoint.getProperties();
		jaxWSProps.put("ws-security.callback-handler", new ServiceKeystorePasswordCallback());
		jaxWSProps.put("ws-security.encryption.properties", "serviceKeystore.properties");
		jaxWSProps.put("ws-security.signature.properties", "serviceKeystore.properties");
		jaxWSProps.put("ws-security.encryption.username", "myservicekey");
	}

	private LiferayCXFOSGiServlet _cxfServlet;
	private List<Endpoint> _endpoints;
	private Server _httpServer;
	private BundleContext _bundleContext;
}