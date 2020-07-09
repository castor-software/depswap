package se.kth.castor.yasjf4j;

import se.kth.castor.yasjf4j.spi.JFactoryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class JFactory {

	private static volatile JFactoryProvider provider;

	private JFactory() {}

	private static void init() {
		ServiceLoader<JFactoryProvider> serviceLoader = ServiceLoader.load(JFactoryProvider.class);
		List<JFactoryProvider> providerList = new ArrayList<JFactoryProvider>();
		for (JFactoryProvider provider : serviceLoader) {
			providerList.add(provider);
		}

		if (providerList != null && !providerList.isEmpty()) {
			provider = providerList.get(0);
			provider.initialize();
		} else {
			throw new IllegalStateException("Unexpected initialization failure");
		}
	}

	private static JFactoryProvider getProvider() {
		if(provider == null) {
			synchronized (JFactory.class) {
				init();
			}
		}
		return provider;
	}

	public static JObject createJObject() {
		return getProvider().createJObject();
	}

	public static JArray createJArray() {
		return getProvider().createJArray();
	}

	public static Object parse(String raw) throws JException {
		return getProvider().parse(raw);
	}
}
