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
	// Attributs

	/**
	 * The serializable class EventQueues does not declare a static final
	 * serialVersionUID field of type long
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Best event this step
	 */
	private EventQueue bestQueue = null;

	/**
	 * If true past is allowed
	 */
	private boolean pastAllowed = true;

	/**
	 * If true past used the naive method
	 */
	private boolean naiveMethod = true;

	/**
	 * If true execution is actived
	 */
	private boolean executeActive = false;

	/**
	 * Inner copy
	 */
	HashMap<String, EventQueue> buffer;

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
				bestQueue = entry.getValue();
			}
		}
	}

	/**
	 * Get and remove next event
	 */
	private Event pop() {
		return bestQueue.poll();
	}

	/**
	 * If the next event time is reached
	 */
	private boolean isTimeReached() {
		prepareBestQueue();
		return bestQueue.isTimeReached();
	}

	/**
	 * Get or create a queue (sorted by species)
	 */
	private EventQueue getOrCreateQueue(String species) {
		EventQueue ret;
		if (executeActive && !naiveMethod) {
			ret = buffer.get(species);
		} else {
			ret = get(species);
		}

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
	private Object innerRegister(Event event, GamaDate date, String species) {
		if (date == null || (event.isTimeReached() && naiveMethod)) {
			return event.execute();
		} else {
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
	private GamaMap<String, Object> innerExecute(IScope scope) {
		GamaMap<String, Object> results = (GamaMap<String, Object>) GamaMapFactory.create();

		while ((size() > 0) && isTimeReached()) {
			// Execute action
			Event event = pop();
			results.addValue(scope, new GamaPair<String, Object>(event.toString(), event.execute(),
					Types.get(IType.STRING), Types.get(IType.NONE)));
		}

		return results;
	}

	/**
	 * Copy buffer into queues
	 */
	private void copyBuffer() {
		for (Entry<String, EventQueue> entry : buffer.entrySet()) {
			EventQueue queue = getOrCreateQueue(entry.getKey());
			queue.addAll(entry.getValue());
		}
		buffer.clear();
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

	/**
	 * Return true if past is alowed
	 */
	public boolean isPastAllowed() {
		return pastAllowed;
	}

	/**
	 * Set if past is allowed
	 */
	public void setPastAllowed(Boolean value) {
		pastAllowed = value;
	}

	// ############################################
	// Register and execute

	/**
	 * Register with action and arguments as map
	 */
	public Object register(final IScope scope, final String species, final ActionDescription action,
			final GamaMap<String, Object> args, final GamaDate date) throws GamaRuntimeException {

		if (!pastAllowed && (date != null && scope.getClock().getCurrentDate().isGreaterThan(date, true))) {
			throw GamaRuntimeException.warning("Past is not allowed " + species + " at " + date, scope);
		}

		// Create a new event
		return innerRegister(new Event(scope, species, action, args, date), date, species);
	}

	/**
	 * Execute the next events
	 */
	@SuppressWarnings("unchecked")
	public Object execute(final IScope scope) throws GamaRuntimeException {
		GamaMap<String, Object> results = (GamaMap<String, Object>) GamaMapFactory.create();

		// If non naive method
		if (!naiveMethod) {
			// Set the flag active to true (in order too save new event in the buffer)
			executeActive = true;
			// Partial result (one inner execute)
			GamaMap<String, Object> partialResults;

			// Do inner execute once
			partialResults = innerExecute(scope);
			// Add partial results in the final results
			results.addValues(scope, partialResults);

			// While until there is no new results
			while (partialResults.size() > 0) {
				// Add buffer to events queues
				copyBuffer();
				// Do inner execute
				partialResults = innerExecute(scope);
				// Add partial results in the final results
				results.addValues(scope, partialResults);
			}

			// Set the flag to false (allow to save directly in the queues)
			executeActive = false;
		} else {
			// Just inner execute
			results = innerExecute(scope);
		}

		// Return result
		return results;
	}
}
