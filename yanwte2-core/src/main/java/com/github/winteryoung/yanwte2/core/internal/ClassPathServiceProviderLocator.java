package com.github.winteryoung.yanwte2.core.internal;

import com.github.winteryoung.yanwte2.core.spi.ServiceProviderLocator;
import java.net.URI;
import java.util.function.Function;

/**
 * @author Winter Young
 * @since 2017/12/13
 */
public class ClassPathServiceProviderLocator implements ServiceProviderLocator {
    @Override
    public Function<Object, Object> getProvider(URI providerURI) {
        if (!providerURI.getScheme().toLowerCase().equals("class")) {
            return null;
        }

        String className = providerURI.getSchemeSpecificPart();
        Class<?> providerClass;
        try {
            providerClass = Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (!Function.class.isAssignableFrom(providerClass)) {
            throw new RuntimeException(
                    "Service provider is expected to implement java.util.function.Function: "
                            + providerClass);
        }

        try {
            //noinspection unchecked
            return (Function<Object, Object>) providerClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}