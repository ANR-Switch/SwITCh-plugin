/*******************************************************************************************************
 *
 * EventQueue.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package irit.gama.util.event_manager;

import java.util.PriorityQueue;

import irit.gama.util.event_manager.Event.EventComparator;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.ActionDescription;

/**
 * Event queue used by the event manager (one queue by species)
 * 
 * @author Jean-François Erdelyi
 */
public class EventQueue extends PriorityQueue<Event> {

	// ############################################
	// Attributs

	/**
	 * The serializable class EventQueue does not declare a static final
	 * serialVersionUID field of type long
	 */
	private static final long serialVersionUID = 1L;

	// ############################################
	// Methods

	/**
	 * Constructor with comparator
	 */
	public EventQueue(EventComparator eventComparator) {
		super(eventComparator);
	}

	/**
	 * Return true if the time of the next element is reached
	 */
	public boolean isTimeReached() {
		return peek().isTimeReached();
	}

	/**
	 * Return true if this queue is greater than the other one. The time of the next
	 * element is compared
	 */
	public boolean isGreaterThan(EventQueue y) {
		if (size() > 0) {
			if (y.size() > 0) { // 1 : 1
				return peek().getDate().isGreaterThan(y.peek().getDate(), true);
			} else { // 1 : 0
				return false;
			}
		} else {
			if (y.size() > 0) { // 0 : 1
				return true;
			} else { // 0 : 0
				return false;
			}
		}
	}

	/**
	 * Return true if this queue is smaller than the other one. The time of the next
	 * element is compared
	 */
	public boolean isSmallerThan(EventQueue y) {
		if (size() > 0) {
			if (y.size() > 0) { // 1 : 1
				return peek().getDate().isSmallerThan(y.peek().getDate(), true);
			} else { // 1 : 0
				return true;
			}
		} else {
			if (y.size() > 0) { // 0 : 1
				return false;
			} else { // 0 : 0
				return true;
			}
		}
	}

	// ############################################
	// Register and execute

	/**
	 * Register with action and arguments as map
	 */
	public void register(final IScope scope, final String species, final ActionDescription action,
			final GamaMap<String, Object> args, final GamaDate date) throws GamaRuntimeException {
		// Create a new event
		add(new Event(scope, species, action, args, date));
	}

	/**
	 * Pop and execute the next event
	 */
	public Object execute() throws GamaRuntimeException {
		// Execute and return result
		return super.poll().execute();
	}
}
