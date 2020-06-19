package compiler;

import java.util.List;

public interface ASTNode {
    ASTNode getParent();
    List<ASTNode> getChildren();
    ASTNode getChild(int index);
    void addChild(ASTNode child);
    String getText();
    ASTNodeType getType();

}
