package r.analysis.codegen;

import com.oracle.truffle.api.frame.Frame;
import org.junit.Test;
import r.analysis.codegen.annotations.*;
import r.analysis.codegen.annotations.behavior.*;
import r.analysis.visitors.*;
import r.nodes.truffle.RNode;

import static junit.framework.Assert.*;



@LocalVariableWrite
class Node1 extends RNode {
    @Override public Object execute(Frame frame) {
        return null;
    }
}

class Node2 extends Node1 {

}


@LocalVariableWrite(check = "checkMe")
@BuiltinBindingChange
class Node3 extends RNode {
    @Override public Object execute(Frame frame) {
        return null;
    }

    public boolean x = true;

    private boolean checkMe() {
        return x;
    }

}
@TopLevelVariableWrite(check = "checkMe")
class Node4 extends Node3 {

    public boolean y = true;

    private boolean checkMe() {
        return y;
    }
}

public class FastrLoaderBehaviorCheckTest {

    @Test
    public void simpleBehaviorWorks() {
        Node1 node = new Node1();
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
    }

    @Test
    public void inheritedBehaviorWorks() {
        Node2 node = new Node2();
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
    }

    @Test
    public void checkedBehaviorWorks() {
        Node3 node = new Node3();
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
        node.x = false;
        assertFalse(node.behaviorCheck(LocalVariableWrite.class));
    }

    @Test
    public void simpleAndCheckedBehaviorWorks() {
        Node3 node = new Node3();
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
        assertTrue(node.behaviorCheck(BuiltinBindingChange.class));
        node.x = false;
        assertFalse(node.behaviorCheck(LocalVariableWrite.class));
        assertTrue(node.behaviorCheck(BuiltinBindingChange.class));
    }

    @Test
    public void inheritedCheckedBehaviorWorks() {
        Node4 node = new Node4();
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
        assertTrue(node.behaviorCheck(TopLevelVariableWrite.class));
        node.y = false;
        assertFalse(node.behaviorCheck(TopLevelVariableWrite.class));
        assertTrue(node.behaviorCheck(LocalVariableWrite.class));
    }

}
