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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
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
import msi.gama.util.IContainer;

import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

import irit.gama.common.interfaces.IKeywordIrit;
import irit.gama.precompiler.IConceptIrit;
import irit.gama.precompiler.ITypeIrit;
import irit.gama.util.GamaDeque;
import irit.gama.util.GamaQueue;
import irit.gama.util.IDequeOperator;

@symbol(
		name = IKeywordIrit.PUSH, 
		kind = ISymbolKind.SINGLE_STATEMENT, 
		with_sequence = false,
		concept = { IConceptIrit.DEQUE, IConceptIrit.STACK, IConceptIrit.QUEUE })
@inside(
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER },
		symbols = IKeyword.CHART)
@doc (
		value = "Allows to add, i.e. to insert, a new element in a deque",
		usages = { @usage (value = "The new element can be added either at the end of the deque",
				examples = { 
						@example (
								value = "push expr to: stack;		// Add at the end",
								isExecutable = false),
						@example (
								value = "push expr to: queue;		// Add at the end",
								isExecutable = false) })})
@facets(
		value = { 
			 @facet (
				name = IKeyword.ITEM,
				type = IType.NONE,
				optional = false,
				doc = { @doc ("any expression to add in the deque") }),
			 @facet (
				name = IKeyword.TO,
				type = { ITypeIrit.DEQUE, ITypeIrit.STACK, ITypeIrit.QUEUE},
				optional = false,
				doc = { @doc ("an expression that evaluates to deque") }), }, 
		omissible = IKeyword.ITEM)
@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
public class PushStatement extends AbstractStatement {

	final IExpression itemExp;
	final IExpression toExp;
	
	public PushStatement(IDescription desc) {
		super(desc);
		
		// Get facets
		itemExp = getFacet(IKeyword.ITEM);
		toExp = getFacet(IKeyword.TO);

		// Save data from facets
		String toName = (toExp != null) ? toExp.literalValue() : null;

		// Set name
		setName("push to " + toName);
	}
	
	private GamaDeque identifyContainer(final IScope scope, final IExpression toExp) throws GamaRuntimeException {
		final Object cont = toExp.value(scope);
		if(cont instanceof GamaDeque) {
			return (GamaDeque) cont;
		}
		return null;
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		
		GamaDeque to = identifyContainer(scope, toExp);
		Object data = itemExp.value(scope);

		if(to != null && data != null) {				
			to.addLast(data);
		}
		
		return data;
	}

}
