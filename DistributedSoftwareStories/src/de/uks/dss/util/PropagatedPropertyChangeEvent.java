package de.uks.dss.util;

import java.beans.PropertyChangeEvent;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

public class PropagatedPropertyChangeEvent extends PropertyChangeEvent {

	private Integer maxHops;
	private LinkedList<Object> propagationPath = new LinkedList<>();
	private LinkedHashMap<Object, Integer> visitedHops = new LinkedHashMap<>();

	public PropagatedPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
		super(source, propertyName, oldValue, newValue);
		// propagationPath.add(source);
		maxHops = Integer.MAX_VALUE;
	}

	public PropagatedPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue,
			Integer maxHops) {
		this(source, propertyName, oldValue, newValue);
		this.maxHops = maxHops;
	}

	public PropagatedPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue,
			Integer maxHops, LinkedList<Object> propagationPath) {
		this(source, propertyName, oldValue, newValue);
		this.maxHops = maxHops;
		this.propagationPath = propagationPath;
	}

	// public PropagatedPropertyChangeEvent(PropertyChangeEvent evt) {
	// this(evt.getSource(), evt.getPropertyName(), evt.getOldValue(),
	// evt.getNewValue(), evt.maxHops, evt.getPropagationPath());
	// }

	public PropagatedPropertyChangeEvent(PropagatedPropertyChangeEvent evt) {
		this(evt.getSource(), evt.getPropertyName(), evt.getOldValue(), evt.getNewValue(), evt.maxHops,
				(LinkedList<Object>) evt.getPropagationPath().clone());
	}

	public LinkedList<Object> getPropagationPath() {
		return propagationPath;
	}

	public boolean addToPath(Object o) {
		if (visitedHops.keySet().contains(o) || propagationPath.contains(o)) {
			return false;
		}
		propagationPath.add(o);
		visitedHops.put(o, 0);
		return true;
	}

	public boolean removeFromPath(Object o) {
		return propagationPath.remove(o);
	}

	public Integer getRemainingHops() {
		int res = Math.max(0, (maxHops) - (getCurrentHops()));
		return res;
	}

	public Integer getCurrentHops() {
		// one is the source (that is no hop...)
		return propagationPath.size() - 1;
	}

	public Set<Object> getVisitedHops() {
		return visitedHops.keySet();
	}

	public boolean shouldVisit(Object o) {
		boolean res = (getRemainingHops() > 0 && !visitedHops.keySet().contains(o) && !propagationPath.contains(o));
		return res;
	}

	@Override
	public String toString() {
		return super.toString() + " Path: " + getPropagationPath();
	}
}
