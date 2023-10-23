/*
 * Sonar Delphi Plugin
 * Copyright (C) 2015 Fabricio Colombo
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

import au.com.integradev.delphi.builders.DelphiTestFile;
import au.com.integradev.delphi.builders.DelphiTestUnitBuilder;
import au.com.integradev.delphi.checks.verifier.CheckVerifier;
import org.junit.jupiter.api.Test;
import org.sonar.plugins.communitydelphi.api.check.DelphiCheck;

class UnitNameCheckTest {

  private static final String RESOURCE_PATH = "/au/com/integradev/delphi/checks/UnitNameCheck";
  private static final String INVALID_PREFIX = RESOURCE_PATH + "/InvalidPrefix.pas";

  private static DelphiCheck createCheck() {
    UnitNameCheck check = new UnitNameCheck();
    check.prefixes = "Prefix";
    return check;
  }

  @Test
  void testCompliantNameShouldNotAddIssue() {
    CheckVerifier.newVerifier()
        .withCheck(createCheck())
        .onFile(new DelphiTestUnitBuilder().unitName("PrefixTestUnits"))
        .verifyNoIssues();
  }

  @Test
  void testCompliantNameWithNamespaceShouldNotAddIssue() {
    CheckVerifier.newVerifier()
        .withCheck(createCheck())
        .onFile(new DelphiTestUnitBuilder().unitName("Namespace.PrefixTestUnits"))
        .verifyNoIssues();
  }

  @Test
  void testInvalidPrefixShouldAddIssue() {
    CheckVerifier.newVerifier()
        .withCheck(createCheck())
        .onFile(DelphiTestFile.fromResource(INVALID_PREFIX))
        .verifyIssues();
  }
}
