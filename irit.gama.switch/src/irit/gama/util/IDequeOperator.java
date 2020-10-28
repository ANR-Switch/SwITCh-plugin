package irit.gama.util;

import irit.gama.precompiler.IConceptIrit;
import irit.gama.precompiler.IOperatorCategoryIrit;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Deque operators
 */
public interface IDequeOperator<T> {
	/**
	 * Pop operator must be redefined in queue and stack classes
	 */
	@operator(
			value = "pop", 
			can_be_const = true, 
			category = { IOperatorCategoryIrit.DEQUE, IOperatorCategoryIrit.QUEUE, IOperatorCategoryIrit.STACK }, 
			type = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
			concept = { IConceptIrit.DEQUE, IConceptIrit.QUEUE, IConceptIrit.STACK })
	@doc(value = "retrieves and removes the first available element of this container, or returns null if this container is empty", masterDoc = true, comment = "the pop operator behavior depends on the nature of the operand", usages = {
			@usage(value = "pop return and remove the first object of the container", examples = {
					@example(value = "pop(stack([1, 2]))", equals = "2"),
					@example(value = "pop(queue([1, 2]))", equals = "1")}) })
	T pop(IScope scope) throws GamaRuntimeException;
}
