package de.uks.dss.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.sdmlib.models.modelsets.SDMSet;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public final class PropagationHelper {

	private static LinkedHashMap<Class, HashSet<String>> edges = new LinkedHashMap<>();

	public static HashSet<String> getEdges(SendableEntityCreator creator) {
		HashSet<String> hashSet = edges.get(creator.getSendableInstance(true).getClass());
		if (hashSet == null) {
			hashSet = new HashSet<>();
			edges.put(creator.getSendableInstance(true).getClass(), hashSet);
			String[] properties = creator.getProperties();
			SendableEntity sendableInstance = (SendableEntity) creator.getSendableInstance(true);
			for (String string : properties) {
				Object value = creator.getValue(sendableInstance, string);
				if (value instanceof SDMSet) {
					hashSet.add(string);
				}
			}
		}
		return hashSet;
	}

	private static LinkedHashMap<Class<? extends SendableEntity>, SendableEntityCreator> creatorMap = new LinkedHashMap<>();

	public static SendableEntityCreator getCreator(SendableEntity clazz) {
		SendableEntityCreator sendableEntityCreator = creatorMap.get(clazz.getClass());
		if (sendableEntityCreator != null) {
			return sendableEntityCreator;
		}
		try {
			SendableEntityCreator invoke = (SendableEntityCreator) clazz.getClass().getMethod("getCreator")
					.invoke(clazz);
			if (invoke != null) {
				creatorMap.put(clazz.getClass(), invoke);
				return invoke;
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static LinkedHashMap<String, Integer> maxHopsMap = new LinkedHashMap<>();

	public static int getMaxHops(Class<? extends SendableEntity> targetClass, String propertyName) {
		Integer integer = maxHopsMap.get(targetClass.getName() + ":" + propertyName);
		if (integer != null) {
			return integer;
		}

		// if maxHops is not already cached, try to find it in the class:
		Field maxHopsField;
		try {
			maxHopsField = targetClass.getDeclaredField("MAX_HOPS_" + propertyName.toUpperCase());
			int maxHops = maxHopsField.getInt(targetClass);
			return maxHops;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return 0;
	}

	public static void setMaxHops(Class<? extends SendableEntity> targetClass, String propertyName, Integer maxHops) {
		maxHopsMap.put(targetClass.getName() + ":" + propertyName, maxHops);
	}

	public static void propagate(SendableEntity source, SendableEntityCreator creator, String propertyName,
			Object newValue, Object oldValue) {
		int maxHops = getMaxHops(source.getClass(), propertyName);
		if (maxHops > 0) {
			propagate(source, creator,
					new PropagatedPropertyChangeEvent(source, propertyName, oldValue, newValue, maxHops));
		}
	}

	@SuppressWarnings("unchecked")
	public static void propagate(SendableEntity object, SendableEntityCreator creator,
			PropagatedPropertyChangeEvent event) {
		event.addToPath(object);
		Method getPropertyChangeSupport;
		try {
			// Fire PropertyChange, if object has Property Change Support
			getPropertyChangeSupport = object.getClass().getMethod("getPropertyChangeSupport");
			PropagatingPropertyChangeSupport pcs = (PropagatingPropertyChangeSupport) getPropertyChangeSupport
					.invoke(object);

			pcs.firePropertyChangeWithoutPropagation(event);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		if (event.getRemainingHops() > 0) {
			getEdges(creator).forEach(o -> {
				SDMSet value = (SDMSet) getCreator(object).getValue(object, o);
				value.forEach((e) -> {
					if (event.shouldVisit(e)) {
						SendableEntityCreator targetCreator = getCreator((SendableEntity) e);
						propagate((SendableEntity) e, targetCreator, event);
					}
				});
			});
		}
		event.removeFromPath(object);
	}

}
