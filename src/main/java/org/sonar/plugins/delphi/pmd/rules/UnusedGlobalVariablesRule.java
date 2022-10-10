/*
 * Sonar Delphi Plugin
 * Copyright (C) 2019-2022 Integrated Application Development
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
package org.sonar.plugins.delphi.pmd.rules;

import static org.sonar.plugins.delphi.utils.VariableUtils.isGeneratedFormVariable;

import net.sourceforge.pmd.RuleContext;
import org.sonar.plugins.delphi.antlr.ast.node.VarDeclarationNode;
import org.sonar.plugins.delphi.symbol.scope.FileScope;

public class UnusedGlobalVariablesRule extends AbstractDelphiRule {
  @Override
  public RuleContext visit(VarDeclarationNode varDeclaration, RuleContext data) {
    if (varDeclaration.getScope() instanceof FileScope
        && !isGeneratedFormVariable(varDeclaration)) {
      varDeclaration.getNameDeclarationList().getDeclarations().stream()
          .filter(node -> node.getUsages().isEmpty())
          .forEach(node -> addViolation(data, node));
    }
    return data;
  }
}