package org.sonar.plugins.delphi.antlr.ast.node;

import org.antlr.runtime.Token;

public abstract class FileHeaderNode extends DelphiNode {
  FileHeaderNode(Token token) {
    super(token);
  }

  public QualifiedNameDeclarationNode getNameNode() {
    return (QualifiedNameDeclarationNode) jjtGetChild(0);
  }

  public String getName() {
    return getNameNode().fullyQualifiedName();
  }
}