/*******************************************************************************************************
 *
 * IKeywordIrit.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package irit.gama.common.interfaces;

/**
 * Keywords of IRIT
 * 
 * @author Jean-François Erdelyi
 */
public interface IKeywordIrit {
	/**
	 * Queue and stack keywords
	 */
	public static final String QUEUE = "queue";
	public static final String STACK = "stack";

	public static final String PUSH = "push";

	/**
	 * Scheduling keywords
	 */
	public static final String EVENT_MANAGER = "event_manager";

	public static final String EVENT = "event";
	
	public static final String EVENT_DATE = "event_date";

	public static final String THE_ACTION = "the_action";
	public static final String WITH_ARGUMENTS = "with_arguments";
	public static final String AT = "at";

	public static final String SIZE_BY_SPECIES = "size_by_species";

	public static final String SCHEDULING = "scheduling";

	public static final String ALLOW_PAST = "allow_past";
}
