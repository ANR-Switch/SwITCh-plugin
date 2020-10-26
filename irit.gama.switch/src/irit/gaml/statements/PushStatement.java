/*******************************************************************************************************
 *
 * msi.gaml.statements.AddStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

import irit.gama.common.interfaces.IKeywordIrit;
import irit.gama.precompiler.IConceptIrit;
import irit.gama.precompiler.ITypeIrit;

@symbol(
		name = IKeywordIrit.PUSH, 
		kind = ISymbolKind.SINGLE_STATEMENT, 
		with_sequence = false,
		concept = { IConceptIrit.DEQUE })
@inside(
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER },
		symbols = IKeyword.CHART)
@doc (
		value = "Allows to add, i.e. to insert, a new element in a deque. If head: true then insert at the first index, at the end otherwise",
		usages = { @usage (value = "The new element can be added either at the end of the container or at the front",
				examples = { 
						@example (
								value = "push expr to: deque;					// Add at the end",
								isExecutable = false),
						@example (
								value = "push expr head: true to: deque;		// Add at the first position",
								isExecutable = false) })})
@facets(
		value = { 
				 @facet (
						 name = IKeyword.TO,
						 type = { ITypeIrit.DEQUE},
						 optional = false,
						 doc = { @doc ("an expression that evaluates to deque") }),
				 @facet(
						name = IKeywordIrit.HEAD, 
						type = IType.BOOL, 
						optional = true,
						doc = { @doc ("if true insert at the first index") }), }, 
		omissible = IKeyword.NAME)
public class PushStatement extends AbstractStatement {

	public PushStatement(IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
