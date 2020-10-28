/*******************************************************************************************************
 *
 * DoLaterStatement.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gaml.statements;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;

import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.DoStatement;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

import irit.gama.common.interfaces.IKeywordIrit;

/**
 * Do an action on a specific date
 */
@symbol(name = { IKeywordIrit.DO_LATER,
		IKeyword.INVOKE }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true, with_scope = false, concept = {
				IConcept.ACTION }, with_args = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@facets(value = {
		@facet(name = IKeyword.ACTION, type = IType.ID, optional = false, doc = @doc("the name of an action or a primitive")),
		@facet(name = IKeyword.WITH, type = IType.MAP, of = IType.NONE, index = IType.STRING, optional = true, doc = @doc("a map expression containing the parameters of the action")),
		@facet(name = IKeywordIrit.AT, type = IType.DATE, optional = true, doc = @doc("call date")) }, omissible = IKeyword.ACTION)
@doc(value = "Allows the agent to execute an action at specific date.", usages = {
		@usage(value = "The simple syntax (when the action does not expect any argument and the result is not to be kept) is:", examples = {
				@example(value = "do_later name_of_action_or_primitive at:my_date;", isExecutable = false) }),
		@usage(value = "In case the action expects one or more arguments to be passed:", examples = {
				@example(value = "do_later name_of_action_or_primitive with: (\"param1\"::value1) at:my_date;", isExecutable = false) }) })
public class DoLaterStatement extends DoStatement {
	// Abort condition
	private static final AtomicBoolean stopped = new AtomicBoolean(true);
	// The watchdog
	private static Thread watchDog;
	// Messages
	private static final PriorityQueue<Event> events = new PriorityQueue<Event>(new EventComparator());
	// Mutex
	private static final Semaphore mutex = new Semaphore(1);

	// Global data
	private String targetSpecies;
	private Arguments arguments;

	// Event comparator -> priority queue
	private static class EventComparator implements Comparator<Event> {
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

	// Inner event
	class Event {
		// The execution date
		private GamaDate date;
		// Simualatio scope
		private IScope scope;
		// Species
		private String targetSpecies;
		// Executer
		IStatement.WithArgs action;
		// Arguments
		Arguments arguments;

		// ############################################
		// Constructors

		public Event(GamaDate date, IScope scope, String targetSpecies, Arguments arguments, String name) {
			setDate(date);
			setScope(scope);
			setTargetSpecies(targetSpecies);
			setArguments(arguments);
			setAction(name);
		}

		// ############################################
		// Getters and setters

		public IStatement.WithArgs getExecuter() {
			return action;
		}

		public GamaDate getDate() {
			return date;
		}

		public IScope getScope() {
			return scope;
		}

		public String getTargetSpecies() {
			return targetSpecies;
		}

		public Arguments getArguments() {
			return arguments;
		}

		public void setTargetSpecies(String targetSpecies) {
			this.targetSpecies = targetSpecies;
		}

		public void setDate(GamaDate date) {
			this.date = date.copy(scope);
		}

		public void setScope(IScope scope) {
			this.scope = scope.copy("Later");
		}

		public void setArguments(Arguments arguments) {
			this.arguments = arguments;
		}

		// ############################################
		// Methods

		public void setAction(String actionName) {
			// Get target species
			final ISpecies species = getSpecies();
			if (species == null) {
				throw GamaRuntimeException.error("Impossible to find a species to execute " + actionName, scope);
			}

			// Get action
			action = species.getAction(actionName);
			if (action == null) {
				throw GamaRuntimeException.error("Impossible to find action", scope);
			}
		}

		public ISpecies getSpecies() {
			return targetSpecies != null ? scope.getModel().getSpecies(targetSpecies) : scope.getAgent().getSpecies();
		}

		public GamaDate getCurrentDate() {
			return scope.getClock().getCurrentDate();
		}

		public Arguments getRuntimeArgs() {
			if (arguments == null) {
				return null;
			}
			return arguments.resolveAgainst(scope);
		}

		public void execute() {
			scope.execute(action, getRuntimeArgs());
		}
	}

	/**
	 * Constructor
	 */
	public DoLaterStatement(final IDescription desc) {
		super(desc);

		// Get target species
		if (((StatementDescription) desc).isSuperInvocation()) {
			final SpeciesDescription s = desc.getSpeciesContext().getParent();
			targetSpecies = s.getName();
		} else {
			targetSpecies = null;
		}

		// Set name
		setName(getLiteral(IKeyword.ACTION));
	}

	/**
	 * From IStatement
	 */
	@Override
	public void setFormalArgs(final Arguments args) {
		arguments = args;
	}

	/**
	 * Internal execution
	 */
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		// Get data from facet at
		GamaDate date = (GamaDate) getFacet("at").value(scope);
		// Create a new event
		Event event = new Event(date, scope, targetSpecies, arguments, name);

		try {
			// Add event
			mutex.acquire();
			events.add(event);
			mutex.release();
		} catch (InterruptedException e) {
			return false;
		}

		// Start the watchdog if it is not already started
		if (stopped.get()) {
			startWatchDog();
		}

		// True if started
		return !stopped.get();
	}

	/**
	 * Start the watchdog thread
	 */
	private void startWatchDog() {
		stopped.set(false);

		// New runnable (watchdog thread content)
		Runnable r = new Runnable() {

			// Override run (watchdog thread content)
			@Override
			public void run() {

				// Until abort condition
				while (!stopped.get()) {
					try {
						// Check abort condition
						mutex.acquire();
						if (events.size() <= 0) {
							stopped.set(true);

							// Release and continue
							mutex.release();
							continue;
						}

						// Get current event
						Event event = events.peek();
						GamaDate currentDate = event.getCurrentDate();
						mutex.release();

						// Wait until the D date
						while (true) {
							mutex.acquire();

							// Check if the scope is interupted
							if (event.getScope().interrupted()) {
								stopped.set(true);

								// Release mutex and break
								mutex.release();
								break;
							}

							// Get current event (the first event can be changed)
							event = events.peek();
							currentDate = event.getCurrentDate();

							// Execute action if the thread is not stopped
							if (currentDate.isGreaterThan(event.getDate(), false)) {
								event.execute();
								events.remove(event);
								event = null;

								// Release mutex and break
								mutex.release();
								break;
							} else {

								// Release mutex and wait 1ms
								mutex.release();
								Thread.sleep(1);
							}
						}

					} catch (InterruptedException e) {
						// Interupt the watchdog
						Thread.currentThread().interrupt();
					}
				}
			}
		};

		// New watchdog thread
		watchDog = new Thread(r);
		// Start watchdog
		watchDog.start();
	}
}
