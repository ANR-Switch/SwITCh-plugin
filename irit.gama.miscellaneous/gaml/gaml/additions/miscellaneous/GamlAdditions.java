package gaml.additions.miscellaneous;

import msi.gaml.extensions.multi_criteria.*;
import msi.gama.outputs.layers.charts.*;
import msi.gama.outputs.layers.*;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gama.kernel.root.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gaml.architecture.user.*;
import msi.gaml.architecture.reflex.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.*;
import msi.gama.metamodel.shape.*;
import msi.gaml.expressions.*;
import msi.gama.metamodel.topology.*;
import msi.gaml.statements.test.*;
import msi.gama.metamodel.population.*;
import msi.gama.kernel.simulation.*;
import msi.gama.kernel.model.*;
import java.util.*;
import msi.gaml.statements.draw.*;
import  msi.gama.metamodel.shape.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.*;
import java.lang.*;
import msi.gama.metamodel.agent.*;
import msi.gaml.types.*;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.descriptions.*;
import msi.gama.util.tree.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.util.path.*;
import msi.gama.util.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;
import msi.gama.kernel.experiment.*;
import msi.gaml.operators.*;
import msi.gama.common.interfaces.*;
import msi.gama.extensions.messaging.*;
import msi.gama.metamodel.population.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Properties;
import msi.gaml.operators.System;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })

public class GamlAdditions extends msi.gaml.compilation.AbstractGamlAdditions {
	public void initialize() throws SecurityException, NoSuchMethodException {
	initializeType();
	initializeSymbol();
	initializeVars();
	initializeOperator();
	initializeAction();
	initializeSkill();
}public void initializeType()  {
_type("queue",new irit.gaml.types.GamaQueueType(),667,102,irit.gama.util.GamaQueue.class);
_type("stack",new irit.gaml.types.GamaStackType(),668,102,irit.gama.util.GamaStack.class);
}public void initializeSymbol()  {
_symbol(S("push"),irit.gaml.statements.PushStatement.class,2,F,F,T,F,F,F,S("chart"),I(3,11,6),P(_facet("item",I(0),0,0,AS,F,F,F),_facet("to",I(668,667),0,0,AS,F,F,F)),"item",(x)->new irit.gaml.statements.PushStatement(x));
}public void initializeVars()  {
_var(irit.gaml.architecure.event_manager.EventManagerArchitecture.class,desc(1,S("type","1","name","size")),(s,a,t,v)->t==null? 0:((irit.gaml.architecure.event_manager.EventManagerArchitecture)t).getQueueSize(a),null,null);
_var(irit.gaml.skills.LoggingBookSkill.class,desc(0,S("type","0","name","log_data")),(s,a,t,v)->t==null? null:((irit.gaml.skills.LoggingBookSkill)t).getLogData(a),null,null);
_var(irit.gaml.skills.LoggingSkill.class,desc(11,S("type","11","name","logbook")),(s,a,t,v)->t==null? null:((irit.gaml.skills.LoggingSkill)t).getEventManager(a),null,(s,a,t,v)->{if (t != null) ((irit.gaml.skills.LoggingSkill) t).setEventManager(a, (IAgent) v); return null; });
_var(irit.gaml.skills.SchedulingSkill.class,desc(11,S("type","11","name","event_manager")),(s,a,t,v)->t==null? null:((irit.gaml.skills.SchedulingSkill)t).getEventManager(a),null,(s,a,t,v)->{if (t != null) ((irit.gaml.skills.SchedulingSkill) t).setEventManager(a, (IAgent) v); return null; });
_var(irit.gaml.skills.SchedulingSkill.class,desc(23,S("type","23","name","event_date")),(s,a,t,v)->t==null? null:((irit.gaml.skills.SchedulingSkill)t).getAt(a),null,null);
_var(irit.gaml.skills.SchedulingSkill.class,desc(11,S("type","11","name","refer_to")),(s,a,t,v)->t==null? null:((irit.gaml.skills.SchedulingSkill)t).getReferTo(a),null,null);
}public void initializeOperator() throws SecurityException, NoSuchMethodException {
_unary(S("pop"),irit.gama.util.deque.IDequeOperator.class.getMethod("pop",SC),C(irit.gama.util.deque.IDequeOperator.class),AI,O,T,-299,-13,-13,-13,(s,o)->((irit.gama.util.deque.IDequeOperator)o).pop(s));
}public void initializeAction() throws SecurityException, NoSuchMethodException {
_action((s,a,t,v)->((irit.gaml.skills.LoggingBookSkill) t).flush(s),desc(PRIM,new Children(),NAME,"flush",TYPE,Ti(O),VIRTUAL,FALSE),irit.gaml.skills.LoggingBookSkill.class.getMethod("flush",SC));
_action((s,a,t,v)->((irit.gaml.skills.LoggingBookSkill) t).write(s),desc(PRIM,new Children(desc(ARG,NAME,"file_name",TYPE,"4","optional",FALSE),desc(ARG,NAME,"flush",TYPE,"3","optional",TRUE)),NAME,"write",TYPE,Ti(O),VIRTUAL,FALSE),irit.gaml.skills.LoggingBookSkill.class.getMethod("write",SC));
_action((s,a,t,v)->((irit.gaml.skills.LoggingSkill) t).logPlot2d(s),desc(PRIM,new Children(desc(ARG,NAME,"agent_name",TYPE,"4","optional",FALSE),desc(ARG,NAME,"date",TYPE,"23","optional",FALSE),desc(ARG,NAME,"data_name",TYPE,"4","optional",FALSE),desc(ARG,NAME,"x",TYPE,"4","optional",FALSE),desc(ARG,NAME,"y",TYPE,"4","optional",FALSE)),NAME,"log_plot_2d",TYPE,Ti(O),VIRTUAL,FALSE),irit.gaml.skills.LoggingSkill.class.getMethod("logPlot2d",SC));
_action((s,a,t,v)->((irit.gaml.skills.SchedulingSkill) t).register(s),desc(PRIM,new Children(desc(ARG,NAME,"the_action",TYPE,"-201","optional",FALSE),desc(ARG,NAME,"with_arguments",TYPE,"10","optional",TRUE),desc(ARG,NAME,"at",TYPE,"23","optional",TRUE),desc(ARG,NAME,"refer_to",TYPE,"11","optional",TRUE)),NAME,"later",TYPE,Ti(O),VIRTUAL,FALSE),irit.gaml.skills.SchedulingSkill.class.getMethod("register",SC));
_action((s,a,t,v)->((irit.gaml.skills.SchedulingSkill) t).clear(s),desc(PRIM,new Children(),NAME,"clear_events",TYPE,Ti(O),VIRTUAL,FALSE),irit.gaml.skills.SchedulingSkill.class.getMethod("clear",SC));
}public void initializeSkill()  {
_skill("event_manager",irit.gaml.architecure.event_manager.EventManagerArchitecture.class,AS);
_skill("logging_book",irit.gaml.skills.LoggingBookSkill.class,AS);
_skill("logging",irit.gaml.skills.LoggingSkill.class,AS);
_skill("scheduling",irit.gaml.skills.SchedulingSkill.class,AS);
}
}