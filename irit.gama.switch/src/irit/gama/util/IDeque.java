package irit.gama.util;

import java.util.Queue;

import irit.gama.precompiler.IConceptIrit;
import irit.gama.precompiler.IOperatorCategoryIrit;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.IType;

public interface IDeque<ValueType> extends IContainer<Integer, ValueType>, Queue<ValueType> {
	/**
	 * Build value
	 */
	@SuppressWarnings("unchecked")
	default ValueType buildValue(final IScope scope, final Object object) {
		final IType<?> ct = getGamlType().getContentType();
		return (ValueType) ct.cast(scope, object, null, false);
	}

	/**
	 * Build values
	 */
	@SuppressWarnings("unchecked")
	default IDeque<ValueType> buildValues(final IScope scope, final IContainer<?, ?> objects) {
		return (IDeque<ValueType>) getGamlType().cast(scope, objects, null, false);
	}

	/**
	 * Build index
	 */
	default Integer buildIndex(final IScope scope, final Object object) {
		return GamaIntegerType.staticCast(scope, object, null, false);
	}

	@operator(
			value = "pop_first", 
			can_be_const = true, 
			category = { IOperatorCategoryIrit.DEQUE }, 
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			concept = {
			IConceptIrit.DEQUE })
	@doc(value = "retrieves and removes the first element of this deque, or returns null if this deque is empty", masterDoc = true, comment = "the pop_first operator behavior depends on the nature of the operand", usages = {
			@usage(value = "pop_first return and remove the first object of the deque", examples = {
					@example(value = "pop_first(deque([1, 2]))", equals = "1") }) })
	ValueType popFirst(IScope scope) throws GamaRuntimeException;

	@operator(
			value = "pop_last", 
			can_be_const = true, 
			category = { IOperatorCategoryIrit.DEQUE },
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			concept = {
			IConceptIrit.DEQUE })
	@doc(value = "retrieves and removes the last element of this deque, or returns null if this deque is empty", masterDoc = true, comment = "the pop_last operator behavior depends on the nature of the operand", usages = {
			@usage(value = "pop_last return and remove the last object of the deque", examples = {
					@example(value = "pop_last(deque([1, 2]))", equals = "2") }) })
	ValueType popLast(IScope scope) throws GamaRuntimeException;
}
