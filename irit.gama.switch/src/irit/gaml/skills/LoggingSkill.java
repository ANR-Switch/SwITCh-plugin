/*******************************************************************************************************
*
* LoggerSkill.java, in plugin irit.gama.switch,
* is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
*
* (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
*
* Visit https://github.com/gama-platform/gama for license information and contacts.
* 
********************************************************************************************************/

package irit.gaml.skills;

import org.jfree.data.json.impl.JSONObject;

import irit.gama.common.interfaces.IKeywordIrit;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/**
 * Logger skill, add the capability to log data, need a logbook
 * 
 * @author Jean-François Erdelyi
 */
@vars({ @variable(name = IKeywordIrit.LOGBOOK, type = IType.AGENT, doc = { @doc("Logbook agent") }), })
@skill(name = IKeywordIrit.LOGGING, concept = { IKeywordIrit.LOGGING, IConcept.SKILL }, internal = true)
public class LoggingSkill extends Skill {

	// ############################################
	// Getter and setter of skill

	@setter(IKeywordIrit.LOGBOOK)
	public void setEventManager(final IAgent agent, final IAgent logbook) {
		if (agent == null || logbook == null) {
			return;
		}
		agent.setAttribute(IKeywordIrit.LOGBOOK, logbook);
	}

	@getter(IKeywordIrit.LOGBOOK)
	public IAgent getEventManager(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (IAgent) agent.getAttribute(IKeywordIrit.LOGBOOK);
	}

	// ############################################
	// Actions

	@SuppressWarnings("unchecked")
	@action(name = "log_plot_2d", args = {
			@arg(name = IKeywordIrit.AGENT_NAME, type = IType.STRING, optional = false, doc = @doc("The name of the agent")),
			@arg(name = IKeywordIrit.DATE, type = IType.DATE, optional = false, doc = @doc("Date")),
			@arg(name = IKeywordIrit.DATA_NAME, type = IType.STRING, optional = false, doc = @doc("Data name")),
			@arg(name = IKeywordIrit.X, type = IType.STRING, optional = false, doc = @doc("X data")),
			@arg(name = IKeywordIrit.Y, type = IType.STRING, optional = false, doc = @doc("Y data")) }, doc = @doc(examples = {
					@example("do log_plot_2d agent_name: name date: my_date data_name: \"Mean speed\" x: my_date y: 30.2") }, value = "Write new line in logbook."))
	public Object logPlot2d(final IScope scope) throws GamaRuntimeException {
		// Get data
		String agentName = (String) scope.getArg(IKeywordIrit.AGENT_NAME, IType.STRING);
		GamaDate date = (GamaDate) scope.getArg(IKeywordIrit.DATE, IType.DATE);
		String dataName = (String) scope.getArg(IKeywordIrit.DATA_NAME, IType.STRING);
		String x = (String) scope.getArg(IKeywordIrit.X, IType.STRING);
		String y = (String) scope.getArg(IKeywordIrit.Y, IType.STRING);

		// Put data
		JSONObject data = new JSONObject();
		data.put("agent", agentName);
		data.put("date", date.toISOString());
		data.put("x", x);
		data.put("y", y);

		// Get logbook
		IAgent logbook = (IAgent) scope.getAgent().getAttribute(IKeywordIrit.LOGBOOK);
		if (logbook == null) {
			throw GamaRuntimeException.error("The logbook must be defined if you have to use the action \"log\"",
					scope);
		}

		// Get skill
		LoggingBookSkill lbs = null;
		for (SkillDescription skill : logbook.getSpecies().getDescription().getSkills()) {
			if (skill.getName() == IKeywordIrit.LOGGING_BOOK) {
				lbs = (LoggingBookSkill) skill.getInstance();
				break;
			}

		}
		if (lbs == null) {
			throw GamaRuntimeException.error("The logbook must by an \"logging_book\" to execute the action \"log\"",
					scope);
		}

		// Write log
		return lbs.log(logbook, dataName, data);
	}
}
