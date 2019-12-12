package org.sonar.plugins.delphi.symbol.declaration;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.sonar.plugins.delphi.antlr.ast.node.TypeDeclarationNode;
import org.sonar.plugins.delphi.symbol.Qualifiable;
import org.sonar.plugins.delphi.symbol.QualifiedName;
import org.sonar.plugins.delphi.symbol.SymbolicNode;
import org.sonar.plugins.delphi.type.Type;
import org.sonar.plugins.delphi.type.Type.StructType;
import org.sonar.plugins.delphi.type.Typed;

public final class TypeNameDeclaration extends DelphiNameDeclaration implements Typed, Qualifiable {
  private final QualifiedName qualifiedName;
  private final Type type;
  private boolean isScopedEnum;

  public TypeNameDeclaration(TypeDeclarationNode node) {
    this(
        new SymbolicNode(node.getTypeNameNode(), node.getScope()),
        node.getType(),
        node.getQualifiedName());
  }

  public TypeNameDeclaration(SymbolicNode node, Type type, QualifiedName qualifiedName) {
    super(node);
    this.type = type;
    this.qualifiedName = qualifiedName;
  }

  @Override
  @NotNull
  public Type getType() {
    return type;
  }

  @Override
  public QualifiedName getQualifiedName() {
    return qualifiedName;
  }

  public boolean isForwardDeclaration() {
    return type instanceof StructType && ((StructType) type).isForwardType();
  }

  public void setIsScopedEnum() {
    isScopedEnum = true;
  }

  public boolean isScopedEnum() {
    return isScopedEnum;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    TypeNameDeclaration that = (TypeNameDeclaration) other;
    return getImage().equalsIgnoreCase(that.getImage())
        && isForwardDeclaration() == that.isForwardDeclaration();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getImage().toLowerCase(), isForwardDeclaration());
  }

  @Override
  public int compareTo(@NotNull DelphiNameDeclaration other) {
    int result = super.compareTo(other);
    if (result == 0) {
      TypeNameDeclaration that = (TypeNameDeclaration) other;
      result = Boolean.compare(isForwardDeclaration(), that.isForwardDeclaration());
    }
    return result;
  }

  @Override
  public String toString() {
    return "type " + getType().getImage();
  }
}