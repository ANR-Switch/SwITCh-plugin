/*******************************************************************************************************
 *
 * EventManagerArchitecture.java, in plugin msi.irit.switch, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package irit.gaml.architecure.event_manager;

import irit.gama.common.interfaces.IKeywordIrit;
import irit.gama.util.event_manager.EventManager;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaMap;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.types.IType;

/**
 * Event manager architecture
 * 
 * @author Jean-François Erdelyi
 */
@vars({ @variable(name = IKeyword.SIZE, type = IType.INT, doc = @doc("Return the size of the all queues")),
		@variable(name = IKeywordIrit.SIZE_BY_SPECIES, type = IType.MAP, doc = @doc("Return the size of the all queues (sorted by name)")),
		@variable(name = IKeywordIrit.ALLOW_PAST, type = IType.BOOL, init = "true", doc = @doc("If true allow date in the past, you can set variable delta_date")) })
@skill(name = IKeywordIrit.EVENT_MANAGER, concept = { IConcept.BEHAVIOR,
		IConcept.ARCHITECTURE }, doc = @doc("Event manager behavior"))
public class EventManagerArchitecture extends ReflexArchitecture {

	// ############################################
	// Getter and setter of architecture

	/**
	 * Get size
	 */
	@getter(IKeyword.SIZE)
	public int getQueueSize(final IAgent agent) {
		return getCurrentManagerIfExists(agent).size();
	}

	/**
	 * Get size by species
	 */
	@getter(IKeywordIrit.SIZE_BY_SPECIES)
	public GamaMap<String, Integer> getQueueSizeBySpecies(final IAgent agent) {
		return getCurrentManagerIfExists(agent).sizeBySpecies();
	}

	/**
	 * Get size
	 */
	@getter(IKeywordIrit.ALLOW_PAST)
	public Boolean getAllowPast(final IAgent agent) {
		return getCurrentManagerIfExists(agent).isPastAllowed();
	}

	/**
	 * Get size by species
	 */
	@setter(IKeywordIrit.ALLOW_PAST)
	public void setAllowPast(final IAgent agent, final Boolean value) {
		agent.setAttribute(IKeywordIrit.ALLOW_PAST, value);
		// If the manager is set then set the object value
		EventManager manager = getCurrentManager(agent);
		if (manager != null) {
			manager.setPastAllowed(value);
		}
	}

	// ############################################
	// Methods

	/**
	 * Execution (each step)
	 */
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);
		return executeCurrentManager(scope);
	}

	/**
	 * Initilization
	 */
	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final EventManager manager = new EventManager();

		Boolean allowPast = (Boolean) agent.getAttribute(IKeywordIrit.ALLOW_PAST);
		manager.setPastAllowed(allowPast);

		agent.setAttribute(IKeywordIrit.EVENT_MANAGER, manager);
		return true;
	}

	/**
	 * Execute current manager
	 */
	protected Object executeCurrentManager(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if (scope.interrupted() || agent == null) {
			return null;
		}

		return getCurrentManagerIfExists(agent).execute(scope);
	}

	/**
	 * Get current manager by agent
	 */
	protected EventManager getCurrentManager(final IAgent agent) throws GamaRuntimeException {
		return (EventManager) agent.getAttribute(IKeywordIrit.EVENT_MANAGER);
	}

	/**
	 * Get current manager by agent. throw exception if doest not exists
	 */
	protected EventManager getCurrentManagerIfExists(final IAgent agent) throws GamaRuntimeException {
		EventManager manager = getCurrentManager(agent);
		if (manager == null) {
			throw GamaRuntimeException.error("No event manager agent was detected", agent.getScope());
		}
		return manager;
	}

	/**
	 * Internal register (used by "scheduling" skill)
	 */
	public Object register(final IScope scope, final String species, final ActionDescription action,
			final GamaMap<String, Object> args, final GamaDate date) throws GamaRuntimeException {

		IAgent agent = (IAgent) getCurrentAgent(scope).getAttribute(IKeywordIrit.EVENT_MANAGER);
		if (scope.interrupted() || agent == null) {
			return null;
		}

		return getCurrentManagerIfExists(agent).register(scope, species, action, args, date);
	}
}
