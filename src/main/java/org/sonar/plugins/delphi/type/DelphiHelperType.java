package org.sonar.plugins.delphi.type;

import static org.sonar.plugins.delphi.type.StructKind.CLASS_HELPER;
import static org.sonar.plugins.delphi.type.StructKind.RECORD_HELPER;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.sonar.plugins.delphi.antlr.ast.node.ClassHelperTypeNode;
import org.sonar.plugins.delphi.antlr.ast.node.HelperTypeNode;
import org.sonar.plugins.delphi.symbol.scope.DelphiScope;
import org.sonar.plugins.delphi.type.Type.HelperType;

public class DelphiHelperType extends DelphiStructType implements HelperType {
  private final Type extendedType;

  private DelphiHelperType(
      String image, DelphiScope scope, Set<Type> parents, Type extendedType, StructKind kind) {
    super(image, scope, parents, kind);
    this.extendedType = extendedType;
  }

  public static HelperType from(HelperTypeNode node) {
    StructKind kind = (node instanceof ClassHelperTypeNode) ? CLASS_HELPER : RECORD_HELPER;

    return new DelphiHelperType(
        node.jjtGetParent().getImage(),
        node.getScope(),
        node.getParentTypes(),
        node.getFor().getType(),
        kind);
  }

  @Override
  @NotNull
  public Type extendedType() {
    return extendedType;
  }

  @Override
  public boolean isForwardType() {
    return false;
  }

  @Override
  public boolean isHelper() {
    return true;
  }
}
