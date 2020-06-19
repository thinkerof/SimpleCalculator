package compiler_simpleCalculator;

import compiler.ASTNode;
import compiler.ASTNodeType;

import java.util.ArrayList;
import java.util.List;

public class SimpleASTNode implements ASTNode {
    ASTNode parent;
    List<ASTNode> children=new ArrayList<>();
    SimpleASTNodeType type;
    String value;

    public SimpleASTNode(SimpleASTNodeType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public ASTNode getParent() {
        return parent;
    }

    @Override
    public List<ASTNode> getChildren() {
        return children;
    }
    @Override
    public ASTNode getChild(int index) {
        return index>=children.size()?null:children.get(index);
    }

    @Override
    public void addChild(ASTNode child) {
        this.children.add(child);
    }

    @Override
    public String getText() {
        return value;
    }

    @Override
    public SimpleASTNodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type+":"+value;
        //return toString(this,"  ",0);
    }
}
