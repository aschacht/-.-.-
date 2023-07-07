package Box.math.Syntax;

import java.util.List;
import java.util.ArrayList;
import Box.math.Token.MathToken;

public abstract class Term {
	public interface Visitor<R> {
	R visitIntegralTerm(Integral term, boolean derive,boolean antiDerive);
	R visitAssignmentTerm(Assignment term, boolean derive,boolean antiDerive);
	R visitBinaryTerm(Binary term, boolean derive,boolean antiDerive);
	R visitMonoTerm(Mono term, boolean derive,boolean antiDerive);
	R visitLnTerm(Ln term, boolean derive,boolean antiDerive);
	R visitLogTerm(Log term, boolean derive,boolean antiDerive);
	R visitFactorialTerm(Factorial term, boolean derive,boolean antiDerive);
	R visitUnaryTerm(Unary term, boolean derive,boolean antiDerive);
	R visitFunctionTerm(Function term, boolean derive,boolean antiDerive);
	R visitDerivitiveTerm(Derivitive term, boolean derive,boolean antiDerive);
	R visitToDeriveTerm(ToDerive term, boolean derive,boolean antiDerive);
	R visitVariableTerm(Variable term, boolean derive,boolean antiDerive);
	R visitLiteralTerm(Literal term, boolean derive,boolean antiDerive);
	R visitETerm(E term, boolean derive,boolean antiDerive);
	R visitPITerm(PI term, boolean derive,boolean antiDerive);
	R visitNotDefinedTerm(NotDefined term, boolean derive,boolean antiDerive);
	}
public static class Integral extends Term {
	 public Integral(Term from , Term to , MathToken toIntegrateOver , Term function) {
	this.from = from;
	this.to = to;
	this.toIntegrateOver = toIntegrateOver;
	this.function = function;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitIntegralTerm(this,derive,antiDerive);
	}

	public final Term from;
	public final Term to;
	public final MathToken toIntegrateOver;
	public final Term function;
	}
public static class Assignment extends Term {
	 public Assignment(Term name , Term value) {
	this.name = name;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitAssignmentTerm(this,derive,antiDerive);
	}

	public final Term name;
	public final Term value;
	}
public static class Binary extends Term {
	 public Binary(Term left , MathToken operator , Term right) {
	this.left = left;
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitBinaryTerm(this,derive,antiDerive);
	}

	public final Term left;
	public final MathToken operator;
	public final Term right;
	}
public static class Mono extends Term {
	 public Mono(Term value , MathToken operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitMonoTerm(this,derive,antiDerive);
	}

	public final Term value;
	public final MathToken operator;
	}
public static class Ln extends Term {
	 public Ln(MathToken operator , Term value) {
	this.operator = operator;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitLnTerm(this,derive,antiDerive);
	}

	public final MathToken operator;
	public final Term value;
	}
public static class Log extends Term {
	 public Log(MathToken operator , Term valueBase , Term value) {
	this.operator = operator;
	this.valueBase = valueBase;
	this.value = value;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitLogTerm(this,derive,antiDerive);
	}

	public final MathToken operator;
	public final Term valueBase;
	public final Term value;
	}
public static class Factorial extends Term {
	 public Factorial(Term value , MathToken operator) {
	this.value = value;
	this.operator = operator;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitFactorialTerm(this,derive,antiDerive);
	}

	public final Term value;
	public final MathToken operator;
	}
public static class Unary extends Term {
	 public Unary(MathToken operator , Term right) {
	this.operator = operator;
	this.right = right;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitUnaryTerm(this,derive,antiDerive);
	}

	public final MathToken operator;
	public final Term right;
	}
public static class Function extends Term {
	 public Function(MathToken name , Term functionBody) {
	this.name = name;
	this.functionBody = functionBody;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitFunctionTerm(this,derive,antiDerive);
	}

	public final MathToken name;
	public final Term functionBody;
	}
public static class Derivitive extends Term {
	 public Derivitive(Term function) {
	this.function = function;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitDerivitiveTerm(this,derive,antiDerive);
	}

	public final Term function;
	}
public static class ToDerive extends Term {
	 public ToDerive(Term left , MathToken derive) {
	this.left = left;
	this.derive = derive;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitToDeriveTerm(this,derive,antiDerive);
	}

	public final Term left;
	public final MathToken derive;
	}
public static class Variable extends Term {
	 public Variable(MathToken name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitVariableTerm(this,derive,antiDerive);
	}

	public final MathToken name;
	}
public static class Literal extends Term {
	 public Literal(Object literal) {
	this.literal = literal;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitLiteralTerm(this,derive,antiDerive);
	}

	public final Object literal;
	}
public static class E extends Term {
	 public E() {
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitETerm(this,derive,antiDerive);
	}

	}
public static class PI extends Term {
	 public PI() {
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitPITerm(this,derive,antiDerive);
	}

	}
public static class NotDefined extends Term {
	 public NotDefined(MathToken name) {
	this.name = name;
	}

	@Override
	public <R> R accept(Visitor<R> visitor, boolean derive,boolean antiDerive) {
	 	return visitor.visitNotDefinedTerm(this,derive,antiDerive);
	}

	public final MathToken name;
	}

 public abstract <R> R accept(Visitor<R> visitor,boolean derive,boolean antiDerive);
}
