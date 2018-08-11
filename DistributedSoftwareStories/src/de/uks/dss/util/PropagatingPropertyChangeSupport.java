package de.uks.dss.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeListenerProxy;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PropagatingPropertyChangeSupport extends PropertyChangeSupport {

	private SendableEntity sourceBean;
	private SendableEntityCreator creator;

	public PropagatingPropertyChangeSupport(SendableEntity sourceBean, SendableEntityCreator creator) {
		super(sourceBean);
		this.sourceBean = sourceBean;
		this.creator = creator;
	}

	@Override
	public void firePropertyChange(PropertyChangeEvent event) {
		super.firePropertyChange(event);
		PropagationHelper.propagate(sourceBean, creator, event.getPropertyName(), event.getOldValue(),
				event.getNewValue());
	}

	public void firePropertyChangeWithoutPropagation(PropagatedPropertyChangeEvent event) {
		// super.firePropertyChange(event);
		PropertyChangeListener[] propertyChangeListeners = this.getPropertyChangeListeners();
		for (PropertyChangeListener propertyChangeListener : propertyChangeListeners) {
			if (propertyChangeListener instanceof UpdateListener) {
				continue;
			}
			if (!(propertyChangeListener instanceof PropertyChangeListenerProxy)
					|| ((PropertyChangeListenerProxy) propertyChangeListener).getPropertyName()
							.equals(event.getPropertyName())) {
				propertyChangeListener.propertyChange(event);
			}
		}
	}

}
