/*******************************************************************************************************
 *
 * msi.gaml.types.GamaListType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package irit.gaml.types;

import java.awt.Color;
import java.util.Collection;

import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.IContainer;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.GamaContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import irit.gama.common.interfaces.IKeywordIrit;
import irit.gama.precompiler.IConceptIrit;
import irit.gama.precompiler.ITypeIrit;
import irit.gama.util.GamaDeque;

@SuppressWarnings("rawtypes")
@type(
		name = IKeywordIrit.DEQUE, 
		id = ITypeIrit.DEQUE, 
		wraps = { GamaDeque.class }, 
		kind = ISymbolKind.Variable.CONTAINER, 
		doc = {@doc("Deque")},
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConceptIrit.DEQUE })
public class GamaDequeType extends GamaContainerType<GamaDeque> {

	/**
	 * TEMPORY CONSTRUCTOR
	 * TODO REMOVE THIS
	 */
	public GamaDequeType() {
		id = ITypeIrit.DEQUE;
		name = IKeywordIrit.DEQUE;
		parent = null;
		parented = false;
		plugin = "irit.gama.switch";
		support = GamaDeque.class;
		varKind = ISymbolKind.Variable.CONTAINER;
	}
	
	/**
	 * Cast data into GamaFifo
	 */
	@Override
	public GamaDeque cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentsType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, contentsType, copy);
	}

	/**
	 * Static cast definition
	 */
	@SuppressWarnings("unchecked")
	public static GamaDeque staticCast(final IScope scope, final Object obj, final IType<?> ct, final boolean copy)
			throws GamaRuntimeException {
		final IType<?> contentsType = ct == null ? Types.NO_TYPE : ct;

		if (obj == null) {
			return new GamaDeque(contentsType);
		}

		if (obj instanceof GamaDate) {
			return new GamaDeque(contentsType, ((GamaDate) obj).listValue(scope, contentsType));
		}

		if (obj instanceof IContainer) {
			if (obj instanceof IPopulation) {
				return new GamaDeque(contentsType, ((IPopulation) obj).listValue(scope, contentsType, true));
			}
			return new GamaDeque(contentsType, ((IContainer) obj).listValue(scope, contentsType, true));
		}

		if (obj instanceof Collection) {
			return new GamaDeque(contentsType, (Collection) obj);
		}

		if (obj instanceof Color) {
			final Color c = (Color) obj;
			return new GamaDeque(contentsType, new Integer[] { c.getRed(), c.getGreen(), c.getBlue() });
		}

		if (obj instanceof GamaPoint) {
			final GamaPoint point = (GamaPoint) obj;
			return new GamaDeque(contentsType, new Double[] { point.x, point.y, point.z });
		}

		if (obj instanceof String) {
			return new GamaDeque(contentsType, StringUtils.tokenize((String) obj));
		}

		return new GamaDeque(contentsType, new Object[] { obj });
	}

	/**
	 * Get default value
	 */
	@Override
	public GamaDeque getDefault() {
		return null;	
	}
	
	/**
	 * Get key type (integer -> index)
	 */
	@Override
	public IType<?> getKeyType() {
		return Types.get(INT);
	}

	/**
	 * Get content type
	 */
	@Override
	public IType<?> contentsTypeIfCasting(final IExpression expr) {
		switch (expr.getGamlType().id()) {
		case COLOR:
		case DATE:
			return Types.get(INT);
		case POINT:
			return Types.get(FLOAT);
		}
		return super.contentsTypeIfCasting(expr);
	}

	/**
	 * True (the type can const cast)
	 */
	@Override
	public boolean canCastToConst() {
		return true;
	}
}
