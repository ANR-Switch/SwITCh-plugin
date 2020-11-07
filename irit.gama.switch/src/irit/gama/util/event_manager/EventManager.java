/*******************************************************************************************************
 *
 * EventManager.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package irit.gama.util.event_manager;

import java.util.HashMap;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.GamaPair;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A queue of event queue
 */
public class EventManager extends HashMap<String, EventQueue> {

	// ############################################
	// Attributes

	/**
	 * The serializable class EventQueues does not declare a static final
	 * serialVersionUID field of type long
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Best event this step
	 */
	private Entry<String, EventQueue> bestEntry = null;

	/**
	 * If true execution is active
	 */
	private boolean executeActive = false;

	/**
	 * The last event executed
	 */
	Event lastEvent = null;

	// ############################################
	// Methods

	/**
	 * Prepare event
	 */
	private void prepareBestQueue() {
		Event bestEvent = null;

		for (Entry<String, EventQueue> entry : entrySet()) {
			Event currentEvent = entry.getValue().peek();

			if (currentEvent == null) {
				continue;
			}

			if ((bestEvent == null) || bestEvent.isGreaterThan(currentEvent)) {
				bestEvent = currentEvent;
				bestEntry = entry;
			}
		}
	}

	/**
	 * Get and remove next event
	 */
	private Event pop() {
		Event e = bestEntry.getValue().poll();

		// TODO Maybe -> if the queue is empty then remove it (architecture param ?)
		// if (bestEntry.getValue().size() <= 0) {
		// remove(bestEntry.getKey());
		// }

		return e;
	}

	/**
	 * If the next event time is reached
	 */
	private boolean isTimeReached() {
		prepareBestQueue();
		return bestEntry.getValue().isTimeReached();
	}

	/**
	 * Get or create a queue (sorted by species)
	 */
	private EventQueue getOrCreateQueue(String species) {
		EventQueue ret = get(species);

		// If not found, create and add a nex queue
		if (ret == null) {
			ret = new EventQueue(new Event.EventComparator());
			put(species, ret);
		}

		return ret;
	}

	/**
	 * Inner Register
	 */
	private Object innerRegister(Event event, String species) throws GamaRuntimeException {
		if (event.getDate() == null) {
			return event.execute();
		} else {
			if (executeActive) {
				if (lastEvent.getDate().isGreaterThan(event.getDate(), true)) {
					throw GamaRuntimeException.warning("Past is not allowed " + species + " at " + event.getDate(),
							event.getScope());
				}
			}
			// Add event
			EventQueue events = getOrCreateQueue(species);
			events.add(event);
			return ExecutionResult.withValue(true);
		}
	}

	/**
	 * Inner Execute
	 */
	@SuppressWarnings("unchecked")
	private GamaMap<String, Object> innerExecute(IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> results = (GamaMap<String, Object>) GamaMapFactory.create();

		executeActive = true;
		while ((size() > 0) && isTimeReached()) {
			// Execute action
			lastEvent = pop();
			results.addValue(scope, new GamaPair<String, Object>(lastEvent.toString(), lastEvent.execute(),
					Types.get(IType.STRING), Types.get(IType.NONE)));
		}
		executeActive = false;

		return results;
	}

	/**
	 * Clear all priority queue
	 */
	@Override
	public void clear() {
		for (Entry<String, EventQueue> entry : entrySet()) {
			entry.getValue().clear();
		}

		super.clear();
	}

	/**
	 * Get size by species
	 */
	@SuppressWarnings("unchecked")
	public GamaMap<String, Integer> sizeBySpecies() {
		GamaMap<String, Integer> ret = (GamaMap<String, Integer>) GamaMapFactory.create();

		for (Entry<String, EventQueue> entry : entrySet()) {
			ret.put(entry.getKey(), entry.getValue().size());
		}

		return ret;
	}

	/**
	 * Get size of all queues
	 */
	@Override
	public int size() {
		int ret = 0;

		for (Entry<String, EventQueue> entry : entrySet()) {
			ret += entry.getValue().size();
		}

		return ret;
	}

	// ############################################
	// Register and execute

	/**
	 * Register with action and arguments as map
	 */
	public Object register(final IScope scope, final String species, final ActionDescription action,
			final GamaMap<String, Object> args, final GamaDate date, final IAgent referredAgent)
			throws GamaRuntimeException {

		// Create a new event
		return innerRegister(new Event(scope, species, action, args, date, referredAgent), species);
	}

	/**
	 * Execute the next events
	 */
	public Object execute(final IScope scope) throws GamaRuntimeException {

		// Return result
		return innerExecute(scope);
	}
}
