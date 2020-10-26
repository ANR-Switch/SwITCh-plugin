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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import irit.gaml.types.TypesIrit;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@SuppressWarnings({ "rawtypes", "unchecked" })
/**
 * The Class GamaDeque.
 */
public class GamaDeque<T> extends ArrayDeque<T> implements IDeque<T> {

	// ############################################
	// Attributs

	/**
	 * The serializable class does not declare a static final serialVersionUID field
	 * of type long
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * GAMA type
	 */
	private IContainerType<?> type;

	// ############################################
	// Constructors

	/**
	 * Constructor
	 */
	public GamaDeque(IType<?> contentsType) {
		super();
		type = TypesIrit.FIFO.of(contentsType);
	}

	/**
	 * Constructor with data
	 */
	public GamaDeque(IType<?> contentsType, T[] values) {
		super();
		for (T v : values) {
			add(v);
		}
		type = TypesIrit.FIFO.of(contentsType);
	}

	/**
	 * Constructor with data
	 */
	public GamaDeque(IType<?> contentsType, Collection<T> values) {
		super();
		for (T v : values) {
			add(v);
		}
		type = TypesIrit.FIFO.of(contentsType);
	}

	/**
	 * Copy constructor
	 */
	public GamaDeque(GamaDeque<T> gq) {
		super(gq.clone());
		type = gq.getGamlType();
	}

	// ############################################
	// Override: methods

	/**
	 * To string
	 */
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return serialize(false);
	}

	/**
	 * Serialization (like a list)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder(size() * 10);
		Object[] values = toArray();

		sb.append('[');
		for (int i = 0; i < size(); i++) {
			if (i != 0) {
				sb.append(',');
			}
			sb.append(StringUtils.toGaml(values[i], includingBuiltIn));
		}
		sb.append(']');

		return sb.toString();
	}

	/**
	 * Clone data
	 */
	@Override
	public GamaDeque<T> copy(IScope scope) throws GamaRuntimeException {
		return new GamaDeque<T>(this);
	}

	/**
	 * Get GAMA type
	 */
	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	/**
	 * Return true if the value exists in the collection
	 */
	@Override
	public boolean contains(IScope scope, Object o) throws GamaRuntimeException {
		return contains(o);
	}

	/**
	 * Check if the "key" exists (index here)
	 */
	@Override
	public boolean containsKey(IScope scope, Object o) throws GamaRuntimeException {
		if (o instanceof Integer) {
			final Integer i = (Integer) o;
			return i >= 0 && i < this.size();
		}
		return false;
	}

	/**
	 * Get first value
	 */
	@Override
	public T firstValue(IScope scope) throws GamaRuntimeException {
		return getFirst();
	}

	/**
	 * Get last value
	 */
	@Override
	public T lastValue(IScope scope) throws GamaRuntimeException {
		return getLast();
	}

	/**
	 * Get random value
	 */
	@Override
	public T anyValue(IScope scope) {
		final int i = scope.getRandom().between(0, 1);
		return i == 0 ? getFirst() : getLast();
	}

	/**
	 * Get the number of values
	 */
	@Override
	public int length(IScope scope) {
		return size();
	}

	/**
	 * True if the collection is empty
	 */
	@Override
	public boolean isEmpty(IScope scope) {
		return isEmpty();
	}

	// ############################################
	// Override: methods about values

	/**
	 * Get the list of values (is "equivalent" to toArray())
	 */
	@Override
	public IList listValue(IScope scope, IType<?> contentType, boolean copy) {
		return GamaListFactory.wrap(contentType, contentType.cast(scope, toArray(), null, copy));
	}

	/**
	 * Return iterable collection
	 */
	@Override
	public Iterable<? extends T> iterable(IScope scope) {
		return listValue(scope, Types.NO_TYPE, false);
	}

	/**
	 * Reverse the collection: useless ?
	 */
	@Override
	public IContainer<?, ?> reverse(IScope scope) throws GamaRuntimeException {
		ArrayList<T> list = (ArrayList<T>) Arrays.asList(toArray());
		Collections.reverse(list);
		return new GamaDeque(type, list.toArray());
	}

	/**
	 * Return a map from values: deque does not allow this
	 */
	@Override
	public <D, C> IMap<C, D> mapValue(IScope scope, IType<C> keyType, IType<D> contentType, boolean copy) {
		final IMap result = GamaMapFactory.create(keyType, contentType);
		result.setAllValues(scope, toArray());
		return result;
	}

	/**
	 * Return matrix from values
	 */
	@Override
	public IMatrix<?> matrixValue(IScope scope, IType<?> contentType, boolean copy) {
		return matrixValue(scope, contentType, null, copy);
	}

	/**
	 * Return matrix from values with prefered size
	 */
	@Override
	public IMatrix<?> matrixValue(IScope scope, IType<?> contentType, ILocation size, boolean copy) {
		return GamaMatrixType.from(scope, listValue(scope, contentType, copy), contentType, size);
	}

	// ############################################
	// Override: methods deque statements

	// ############################################
	// Override: methods deque

	@Override
	public T popFirst(IScope scope) {
		return pollFirst();
	}

	@Override
	public T popLast(IScope scope) {
		return pollLast();
	}

}
