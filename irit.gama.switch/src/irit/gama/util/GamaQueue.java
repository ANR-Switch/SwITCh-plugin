/*******************************************************************************************************
 *
 * GamaFifo.java, in plugin irit.gama.switch, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gama.util;

import java.util.Collection;

import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

import irit.gaml.types.TypesIrit;

/**
 * The Class GamaQueue.
 */
public class GamaQueue<T> extends GamaDeque<T> implements IDequeOperator<T> {
	
	// ############################################
	// Attributs

	/**
	 * The serializable class does not declare a static final serialVersionUID field
	 * of type long
	 */
	private static final long serialVersionUID = 1L;

	// ############################################
	// Constructors

	/**
	 * Constructor
	 */
	public GamaQueue(IType<?> contentsType) {
		super(TypesIrit.QUEUE.of(contentsType));
	}

	/**
	 * Constructor with data
	 */
	public GamaQueue(IType<?> contentsType, T[] values) {
		super(TypesIrit.QUEUE.of(contentsType), values);
	}

	/**
	 * Constructor with data
	 */
	public GamaQueue(IType<?> contentsType, Collection<T> values) {
		super(TypesIrit.QUEUE.of(contentsType), values);
	}

	// ############################################
	// Override : IDequeOperator

	/**
	 * Pop data from queue (FIFO)
	 */
	@Override
	public T pop(IScope scope) {
		return pollFirst();
	}
}
