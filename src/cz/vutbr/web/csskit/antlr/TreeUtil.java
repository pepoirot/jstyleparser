package cz.vutbr.web.csskit.antlr;

import org.antlr.runtime.tree.CommonTree;

public class TreeUtil {

	/**
     * Constructs pretty indented "lisp" representation of tree
     * which was created by parser
     */
    public static String toStringTree(CommonTree tree) {
        StringBuilder sb = new StringBuilder();
        nest(tree, sb, 0);
        return sb.toString();       
    }
    
    private static void nest(CommonTree tree, StringBuilder sb, int nest) {
        if(tree.getChildCount()==0) {
            addTree(sb, tree, nest);
            return;
        }
            
        if(!tree.isNil()) {
            addTree(sb, tree, nest);
        }
       
        for(int i=0; i < tree.getChildCount(); i++) {
            CommonTree n = (CommonTree) tree.getChild(i);
            nest(n, sb, nest+1);
        }
        if(!tree.isNil()) {
            sb.append(")");
        }
    
    }
    
    private static StringBuilder addTree(StringBuilder sb, CommonTree tree, int nest) {
        sb.append("\n");
        for(int i=0; i< nest; i++) {
            sb.append("  ");
        }
        
        if(!tree.isNil())
          sb.append("(");
        
        sb.append(tree.toString()).append(" |")
          .append(tree.getType()).append("| ");
        
        return sb;
    }
}
