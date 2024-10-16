package com.craftinginterpreters.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function stmtDeclaration;
  private final Expr.Function exprDeclaration;
  private final Environment closure;

  LoxFunction(Stmt.Function declaration, Environment closure) {
    this.stmtDeclaration = declaration;
    this.exprDeclaration = null;
    this.closure = closure;
  }

  LoxFunction(Expr.Function declaration, Environment closure) {
    this.stmtDeclaration = null;
    this.exprDeclaration = declaration;
    this.closure = closure;
  }

  @Override
  public String toString() {
    if (stmtDeclaration != null) {
      return "<fn " + stmtDeclaration.name.lexeme + ">";
    } else {
      return "<fn>";
    }
  }

  @Override
  public int arity() {
    if (stmtDeclaration != null) {
      return stmtDeclaration.params.size();
    } else {
      return exprDeclaration.params.size();
    }
  }

  @Override
  public Object call(Interpreter interpreter,
      List<Object> arguments) {
    Environment environment = new Environment(closure);
    List<Token> params;
    List<Stmt> body;

    if (stmtDeclaration != null) {
      params = stmtDeclaration.params;
      body = stmtDeclaration.body;
    } else {
      params = exprDeclaration.params;
      body = exprDeclaration.body;
    }

    for (int i = 0; i < params.size(); i++) {
      environment.define(params.get(i).lexeme, arguments.get(i));
    }

    try {
      interpreter.executeBlock(body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }
    return null;
  }
}
