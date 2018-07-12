package org.sonar.plugins.delphi.pmd.rules;

import net.sourceforge.pmd.RuleContext;
import org.sonar.plugins.delphi.antlr.DelphiLexer;
import org.sonar.plugins.delphi.antlr.ast.DelphiPMDNode;

public class ClassNamePrefixRule extends DelphiRule {

    @Override
    public  void visit(DelphiPMDNode node, RuleContext ctx){

        if (node.getType() == DelphiLexer.TkClass ){
            String className = node.getParent().getText();

            if(!className.startsWith("T")){
                addViolation(ctx, node);
            }
        } else if(node.getType() == DelphiLexer.TkNewType){
            String enumName = node.getChild(0).getText();

            System.out.print("NAME: " + enumName + "\n");

            if (!enumName.startsWith("T")){
                addViolation(ctx, node);
            }
        }
    }
}
