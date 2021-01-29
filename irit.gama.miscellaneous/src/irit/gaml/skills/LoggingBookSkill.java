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

import java.io.FileWriter;
import java.io.IOException;

import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.json.impl.JSONObject;

import irit.gama.common.interfaces.IKeywordIrit;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

/**
 * Logger skill, add the capability to log data and write it into files
 * 
 * @author Jean-François Erdelyi
 */
@vars({ @variable(name = IKeywordIrit.LOG_DATA, type = IType.NONE, doc = { @doc("Logbook agent") }), })
@skill(name = IKeywordIrit.LOGGING_BOOK, concept = { IKeywordIrit.LOGGING, IConcept.SKILL }, internal = true)
public class LoggingBookSkill extends Skill {
	// ############################################
	// Getter and setter of skill

	@getter(IKeywordIrit.LOG_DATA)
	public Object getLogData(final IAgent agent) {
		if (agent == null) {
			return null;
		}
		return (JSONObject) agent.getAttribute(IKeywordIrit.LOG_DATA);
	}

	// ############################################
	// Actions

	@action(name = "write", args = {
			@arg(name = IKeywordIrit.FILE_NAME, type = IType.STRING, optional = false, doc = @doc("File name")),
			@arg(name = IKeywordIrit.FLUSH, type = IType.BOOL, optional = true, doc = @doc("Flush data if true")) }, doc = @doc(examples = {
					@example("do write file_name: \"log.txt\" clear: true;") }, value = "Write data in file."))
	public Object write(final IScope scope) throws GamaRuntimeException {
		// Get data from the scope
		String fileName = (String) scope.getArg(IKeywordIrit.FILE_NAME, IType.STRING);
		Boolean flush = (Boolean) scope.getArg(IKeywordIrit.FLUSH, IType.BOOL);
		FileWriter fw = null;
		IAgent agent = scope.getAgent();
		JSONObject jsonData = (JSONObject) agent.getAttribute(IKeywordIrit.LOG_DATA);
		
		// Not ok
		if (jsonData == null) {
			return false;
		}

		// Write data
		String path = FileUtils.constructAbsoluteFilePath(scope, fileName, false);
		try {
			fw = new FileWriter(path);
			fw.write(jsonData.toJSONString());
		} catch (IOException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		} finally {
			try {
				if (fw != null) {
					fw.flush();
					fw.close();
				}
			} catch (IOException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}

		// If "flush" is true then clear data
		if (flush) {
			jsonData.clear();
		}

		// Ok
		return true;
	}

	@action(name = "flush", doc = @doc(examples = { @example("do flush;") }, value = "Flush data."))
	public Object flush(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgent();
		JSONObject jsonData = (JSONObject) agent.getAttribute(IKeywordIrit.LOG_DATA);

		// Clear data
		jsonData.clear();

		// Always true
		return true;
	}

	// ############################################
	// Internal behavior

	@SuppressWarnings("unchecked")
	public Object log(final IAgent logbook, final String dataName, final JSONObject data) throws GamaRuntimeException {
		JSONObject jsonData = (JSONObject) logbook.getAttribute(IKeywordIrit.LOG_DATA);
		if (jsonData == null) {
			jsonData = new JSONObject();
			logbook.setAttribute(IKeywordIrit.LOG_DATA, jsonData);
		}

		// Get map
		JSONArray values = (JSONArray) jsonData.get(dataName);

		// If map is null then create a new one
		if (values == null) {
			values = new JSONArray();
			jsonData.put(dataName, values);
		}

		return values.add(data);
	}

}
