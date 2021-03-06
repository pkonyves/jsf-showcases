
package hu.palkonyves.javaproxy;

import hu.palkonyves.rest.api.BookResource;
import hu.palkonyves.rest.impl.BookResourceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ServiceLocator {

	public BookResource getBookResource(boolean doLog) {
		// Prented that the stub is generated by JAX-WS/RS
		return new BookResourceImpl("library.zh.adnovum.ch", doLog);
	}

	public BookResource getBookResourceProxySimple(boolean doLog) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] ifaces = new Class<?>[] { BookResource.class };

		final BookResourceImpl realObject = new BookResourceImpl("library0.zh.adnovum.ch", doLog);

		InvocationHandler invocationHandler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return method.invoke(realObject, args);
			}

		};

		return (BookResource) Proxy.newProxyInstance(contextClassLoader, ifaces, invocationHandler);
	}

	public BookResource getBookResourceProxy(boolean doLog) {
		// Why contextclassloader and not classloader?

		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Class<?>[] ifaces = new Class<?>[] { BookResource.class };

		RoundRobinCache<BookResource> cache = new RoundRobinCache<BookResource>();
		cache.add(new BookResourceImpl("library0.zh.adnovum.ch", doLog));
		cache.add(new BookResourceImpl("library1.zh.adnovum.ch", doLog));
		LoadBalancerProxy<BookResource> invocationHandler = new LoadBalancerProxy<BookResource>(
				cache);

		return (BookResource) Proxy.newProxyInstance(contextClassLoader, ifaces, invocationHandler);
	}

}
