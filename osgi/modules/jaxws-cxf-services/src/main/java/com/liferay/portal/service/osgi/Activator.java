package com.liferay.portal.service.osgi;

import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.service.osgi.cxf.LiferayCXFOSGiServlet;
import com.liferay.portal.service.remote.JAXWSService2;
import com.liferay.portal.service.remote.JAXWSServiceImpl2;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;
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

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 */
public class Activator implements BundleActivator, ServiceListener {
	// todo - test
	private ServiceRegistration _serviceRegistration;
	// TODO: until we have the right @REMOTE annotation, use JSONWebService annotation
	private static final Class PORTAL_REMOTE_SERVICE_ANNOTATION = JSONWebService.class;

	private ClassPool _pool;

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		try {
			_pool = new ClassPool(ClassPool.getDefault());

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

		_pool = null;
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
			try {
				webService = wrapPortalService(service);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		if (!hasAnnotation(webService.getClass(), WebService.class)) {
			return;
		}

		try {
			// register our Bus instance into ThreadLocal for the provider
			BusFactory.setThreadDefaultBus(_cxfServlet.getBus());

			// bad bad ugly thing - call private CXF API to get Provider
			ProviderImpl provider = new ProviderImpl();

			Endpoint endpoint = provider.createEndpoint(null, webService);

			addEndpointSecurityProperties(endpoint);

			StringBuilder url = new StringBuilder();

			Class annotatedClass = getAnnotatedClass(webService.getClass(), WebService.class);

			url.append("/");
			url.append(serviceReference.getBundle().getSymbolicName());
			url.append("/");
			url.append(serviceReference.getBundle().getVersion().toString());
			url.append("/");
			url.append(annotatedClass.getName());

			endpoint.publish(url.toString());

			_endpoints.add(endpoint);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object wrapPortalService(final Object originalService)
		throws NotFoundException, CannotCompileException {

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

		Class annotatedClass = getAnnotatedClass(
			originalService.getClass(), PORTAL_REMOTE_SERVICE_ANNOTATION);

		// We need to reference in the one generated interface all:
		// 1, classes used in the original service, not visible to this bundle
		// 2, JAX-WS annotations references from this bundle, but not referenced
		// from the service bundle
		// >> For this we need AggregatedClassLoader
		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			getClass().getClassLoader());
		aggregateClassLoader.addClassLoader(annotatedClass.getClassLoader());

		// CXF is separatelly loading the generated interface using
		// proxy.getClassLoader().loadClass(generatedInterfaceName).
		// For this to work we need to wrap the aggregated classloader with
		// javassist Loader. It looks first into the pool of generated classes
		// when loading the generated interface and returns correct class
		// instance
		Loader loader = new Loader(aggregateClassLoader, _pool);

		Class serviceInterface = generateServiceInterface(
			annotatedClass, loader);

		aggregateClassLoader.addClassLoader(
			originalService.getClass().getClassLoader());

		// create a new proxy
		Object proxy = Proxy.newProxyInstance(
			loader, new Class[]{serviceInterface},
			new TransparentInvocationHandler(originalService));

		return proxy;
	}

	private Class generateServiceInterface(Class annotatedClass, ClassLoader cl)
		throws NotFoundException, CannotCompileException {

		Set<String> webMethodNames = new HashSet<String>();

		String serviceName = annotatedClass.getName() + "JAXWS";

		_pool.appendClassPath(new ClassClassPath(annotatedClass));

		// create new interface
		CtClass generatedInterface = _pool.makeInterface(serviceName);

		ConstPool constpool = generatedInterface.getClassFile().getConstPool();

		// add runtime visible @WebService(name=serviceName)
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(WebService.class.getName(), constpool);
		annot.addMemberValue("name", new StringMemberValue(serviceName, constpool));
		attr.addAnnotation(annot);
		generatedInterface.getClassFile().addAttribute(attr);

		// add declared methods
		Method[] methods = annotatedClass.getDeclaredMethods();
		for (Method method : methods) {
			CtClass returnType = _pool.getCtClass(method.getReturnType().getName());

			Class[] parameterTypes = method.getParameterTypes();
			CtClass[] parameters = new CtClass[parameterTypes.length];
			for (int i = 0; i < parameters.length; i++) {
				parameters[i] = _pool.getCtClass(parameterTypes[i].getName());
			}

			Class[] exceptionTypes = method.getExceptionTypes();
			CtClass[] exceptions = new CtClass[exceptionTypes.length];
			for (int i = 0; i < exceptions.length; i++) {
				exceptions[i] = _pool.getCtClass(exceptionTypes[i].getName());
			}

			String methodName = method.getName();

			CtMethod serviceMethod = CtNewMethod.abstractMethod(
				returnType, methodName, parameters, exceptions, generatedInterface);

			// Avoid duplicating of web method names (cannot be registered)
			String webMethodName = method.getName();
			for (int i = 0; i < 1000 && webMethodNames.contains(webMethodName); i++) {
				webMethodName = method.getName() + i;
			}
			webMethodNames.add(webMethodName);

			attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
			annot = new Annotation(WebMethod.class.getName(), constpool);
			annot.addMemberValue("operationName", new StringMemberValue(webMethodName, constpool));
			attr.addAnnotation(annot);
			serviceMethod.getMethodInfo().addAttribute(attr);

			generatedInterface.addMethod(serviceMethod);
		}

		// generate
		return generatedInterface.toClass(cl, getClass().getProtectionDomain());
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

		Class serviceClass = getAnnotatedClass(service.getClass(), WebService.class);
		if (serviceClass != null) {
			try {
				CtClass ctClass = _pool.getCtClass(serviceClass.getName() + "JAXWS");
				ctClass.defrost();
				ctClass.detach();
			}
			catch (NotFoundException e) {
			}
		}
	}

	protected boolean hasAnnotation(Class cls, Class annotation) {
		return getAnnotatedClass(cls, annotation) != null;
	}

	protected Class getAnnotatedClass(Class cls, Class annotation) {
		if (cls == null) {
			return null;
		}
		if (null != cls.getAnnotation(annotation)) {
			return cls;
		}
		for (Class inf : cls.getInterfaces()) {
			if (null != inf.getAnnotation(annotation)) {
				return inf;
			}
		}

		return getAnnotatedClass(cls.getSuperclass(), annotation);
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

class TransparentInvocationHandler implements InvocationHandler {
	Object originalClass;
	Map<String, Method> methodsIndex = new HashMap();

	TransparentInvocationHandler(Object originalClass) {
		this.originalClass = originalClass;
		Method[] methods = originalClass.getClass().getMethods();
		for (Method method : methods) {
			methodsIndex.put(generateKey(method), method);
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {

		String key = generateKey(method);

		Method originalMethod = methodsIndex.get(key);

		if (originalMethod == null) {
			throw new Exception("Unable to find original method!");
		}

		return originalMethod.invoke(originalClass, args);
	}

	private String generateKey(Method method) {
		StringBuilder key = new StringBuilder();

		key.append(method.getName());

		key.append("(");

		for (Class parameterType : method.getParameterTypes()) {
			key.append(parameterType.getName());
			key.append(",");
		}

		key.append(") ");

		key.append(method.getReturnType().getName());

		return key.toString();
	}
}