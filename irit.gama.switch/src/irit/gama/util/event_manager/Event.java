/*******************************************************************************************************
 *
 * Event.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package irit.gama.util.event_manager;

import java.util.Comparator;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * Event used by the event manager
 * 
 * @author Jean-François Erdelyi
 */
public class Event {

	// ############################################
	// Comparator

	/**
	 * Event comparator -> priority queue
	 */
	public static class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event x, Event y) {
			if (x.getDate().isSmallerThan(y.getDate(), true)) {
				return -1;
			}
			if (x.getDate().isGreaterThan(y.getDate(), true)) {
				return 1;
			}
			return 0;
		}
	}

	// ############################################
	// Attributs

	/**
	 * The execution date
	 */
	private GamaDate date;

	/**
	 * Simualatio scope
	 */
	private IScope scope;

	/**
	 * Species
	 */
	private String species;

	/**
	 * Executer
	 */
	private IStatement.WithArgs action;

	/**
	 * Arguments
	 */
	private Arguments arguments;

	// ############################################
	// Constructor

	/**
	 * Create a new event with action and Arguments as map
	 */
	public Event(IScope scope, String species, ActionDescription action, final GamaMap<String, Object> args,
			GamaDate date) {

		this.scope = scope.copy("Later");
		this.species = species;

		// Get arguments
		arguments = action.createCompiledArgs().resolveAgainst(scope);

		// Convert arguments and insert in the current scope all values
		IList<String> keys = args.getKeys();
		for (String key : keys) {
			IType<?> type = this.scope.getType(key);
			final Object val = type.cast(this.scope, args.get(key), null, true);
			this.scope.addVarWithValue(key, val);
		}

		// Set action by name
		setAction(action.getName());
		setDate(date);
	}

	// ############################################
	// Methods

	/**
	 * Set date
	 */
	private void setDate(GamaDate date) throws GamaRuntimeException {
		if (date != null) {
			this.date = date.copy(scope);
		} else {
			this.date = null;
		}
	}

	/**
	 * Set Action
	 */
	private void setAction(String actionName) throws GamaRuntimeException {
		// Get target species
		final ISpecies species = getSpecies();
		if (species == null) {
			throw GamaRuntimeException.error("Impossible to find a species to execute " + actionName, scope);
		}

		// Get action
		action = species.getAction(actionName);
		if (action == null) {
			throw GamaRuntimeException.error("Impossible to find action " + action.getName() + " in " + species, scope);
		}
	}

	/**
	 * Get arguments
	 */
	private Arguments getRuntimeArgs() throws GamaRuntimeException {
		if (arguments == null) {
			return null;
		}
		return arguments.resolveAgainst(scope);
	}

	/**
	 * Get Species
	 */
	private ISpecies getSpecies() throws GamaRuntimeException {
		return species != null ? scope.getModel().getSpecies(species) : scope.getAgent().getSpecies();
	}

	/**
	 * Get date
	 */
	public GamaDate getDate() {
		return date;
	}

	/**
	 * Is greater than the event e
	 */
	public boolean isGreaterThan(Event e) {
		return date.isGreaterThan(e.getDate(), false);
	}

	/**
	 * True if the time of the next event is reached
	 */
	public boolean isTimeReached() {
		return scope.getClock().getCurrentDate().isGreaterThan(date, false);
	}

	/**
	 * Execute the action
	 */
	public Object execute() {
		return scope.execute(action, getRuntimeArgs()).getValue();
	}

	/**
	 * To string
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(action.getName());
		sb.append(" of ");
		sb.append(getSpecies());
		sb.append(" with ");
		// Bug in GAMA core -> Arguments toString() does not return the toString of each
		// facet but the hash of the list
		sb.append(arguments.toString());
		sb.append(" at ");
		sb.append(date);

		return sb.toString();
	}
}
