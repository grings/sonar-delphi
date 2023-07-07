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
package au.com.integradev.delphi.checks;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.communitydelphi.api.ast.DelphiAst;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheck;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheckContext;
import org.sonar.plugins.communitydelphi.api.check.FilePosition;
import org.sonarsource.analyzer.commons.annotations.DeprecatedRuleKey;

@DeprecatedRuleKey(ruleKey = "TooLongLineRule", repositoryKey = "delph")
@Rule(key = "TooLongLine")
public class TooLongLineCheck extends DelphiCheck {
  private static final int DEFAULT_MAXIMUM_LINE_LENGTH = 120;

  @RuleProperty(
      key = "maximumLineLength",
      description = "The maximum authorized line length.",
      defaultValue = "" + DEFAULT_MAXIMUM_LINE_LENGTH)
  public int maximumLineLength = DEFAULT_MAXIMUM_LINE_LENGTH;

  @Override
  public DelphiCheckContext visit(DelphiAst ast, DelphiCheckContext context) {
    for (int i = 0; i < context.getFileLines().size(); ++i) {
      String line = context.getFileLines().get(i);
      int lineLength = getLineLength(line);

      if (lineLength > maximumLineLength) {
        int lineNumber = i + 1;
        context
            .newIssue()
            .onFilePosition(FilePosition.from(lineNumber, 0, lineNumber, lineLength))
            .withMessage(
                String.format(
                    "Split this %d characters long line (which is greater than %d authorized).",
                    lineLength, maximumLineLength))
            .report();
      }
    }
    return context;
  }

  private static int getLineLength(String line) {
    int length = line.length();

    // For the purposes of line length, we don't care about whitespace at the end of the line
    for (; length > 0; length--) {
      if (!Character.isWhitespace(line.charAt(length - 1))) {
        break;
      }
    }

    return length;
  }
}