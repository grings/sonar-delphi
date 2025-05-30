/*
 * Sonar Delphi Plugin
 * Copyright (C) 2019 Integrated Application Development
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package au.com.integradev.delphi.antlr.ast.node;

import au.com.integradev.delphi.antlr.ast.visitors.DelphiParserVisitor;
import au.com.integradev.delphi.type.generic.TypeParameterTypeImpl;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.runtime.Token;
import org.sonar.plugins.communitydelphi.api.ast.ConstraintNode;
import org.sonar.plugins.communitydelphi.api.ast.GenericDefinitionNode.TypeParameter;
import org.sonar.plugins.communitydelphi.api.ast.NameDeclarationNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeConstraintNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeParameterNode;
import org.sonar.plugins.communitydelphi.api.ast.TypeReferenceNode;
import org.sonar.plugins.communitydelphi.api.type.Constraint;
import org.sonar.plugins.communitydelphi.api.type.Type.TypeParameterType;

public final class TypeParameterNodeImpl extends DelphiNodeImpl implements TypeParameterNode {
  public TypeParameterNodeImpl(Token token) {
    super(token);
  }

  public TypeParameterNodeImpl(int tokenType) {
    super(tokenType);
  }

  @Override
  public <T> T accept(DelphiParserVisitor<T> visitor, T data) {
    return visitor.visit(this, data);
  }

  @Override
  public List<NameDeclarationNode> getTypeParameterNameNodes() {
    return findChildrenOfType(NameDeclarationNode.class);
  }

  @SuppressWarnings("removal")
  @Override
  public List<TypeReferenceNode> getTypeConstraintNodes() {
    return getConstraintNodes().stream()
        .filter(TypeConstraintNode.class::isInstance)
        .map(TypeConstraintNode.class::cast)
        .map(TypeConstraintNode::getTypeNode)
        .collect(Collectors.toList());
  }

  @Override
  public List<ConstraintNode> getConstraintNodes() {
    return findChildrenOfType(ConstraintNode.class);
  }

  @Override
  public List<TypeParameter> getTypeParameters() {
    List<Constraint> constraints =
        getConstraintNodes().stream()
            .map(ConstraintNode::getConstraint)
            .collect(Collectors.toUnmodifiableList());

    return getTypeParameterNameNodes().stream()
        .map(
            name ->
                new TypeParameterImpl(
                    name, TypeParameterTypeImpl.create(name.getImage(), constraints)))
        .collect(Collectors.toList());
  }

  private static final class TypeParameterImpl implements TypeParameter {
    private final NameDeclarationNode location;
    private final TypeParameterType type;

    public TypeParameterImpl(NameDeclarationNode location, TypeParameterType type) {
      this.location = location;
      this.type = type;
    }

    @Override
    public NameDeclarationNode getLocation() {
      return location;
    }

    @Override
    public TypeParameterType getType() {
      return type;
    }
  }
}
