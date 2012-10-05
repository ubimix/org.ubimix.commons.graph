/* ************************************************************************** *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * This file is licensed to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ************************************************************************** */
package org.ubimix.commons.graph;

import org.ubimix.commons.graph.GraphIterator;
import org.ubimix.commons.graph.PrintListener;
import org.ubimix.commons.graph.Walker;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class GraphIteratorTest extends TestCase {

    private static class Node {
        private Node fFirstChild;

        private final String fName;

        private Node fNextSibling;

        public Node(String name) {
            fName = name;
        }

        public Node addChild(Node child) {
            if (fFirstChild == null) {
                fFirstChild = child;
            } else {
                Node lastChild = fFirstChild;
                if (lastChild.fNextSibling != null) {
                    lastChild = lastChild.fNextSibling;
                }
                lastChild.fNextSibling = child;
            }
            return child;
        }

        public Node addChild(String name) {
            return addChild(new Node(name));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof Node))
                return false;
            Node o = (Node) obj;
            return fName.equals(o.fName);
        }

        public Node getFirstChild() {
            return fFirstChild;
        }

        public Node getNextSibling() {
            return fNextSibling;
        }

        @Override
        public int hashCode() {
            return fName.hashCode();
        }

        @Override
        public String toString() {
            return fName;
        }
    }

    /**
     * @param name
     */
    public GraphIteratorTest(String name) {
        super(name);
    }

    public void test() {
        Node top = new Node("X");
        Node a = top.addChild("a");
        a.addChild("a1");
        a.addChild("a2");
        Node b = top.addChild("b");
        b.addChild("b1");
        b.addChild("b2");

        // Basic iteration modes:
        test(top, GraphIterator.IN, "X,a,b");
        test(top, GraphIterator.OUT, "a,b,X");
        test(top, GraphIterator.LEAF, "a1,a2,b1,b2");
        // NEXT mode returns the control when the iterator goes from one node
        // into an another. In this tree this kind of transitions happen 3
        // times:
        // 1) a1 => a2
        // 2) a => b
        // 3) b1 => b2
        // Every time the iterator returns the topmost node in the stack.
        // So in the first transition (a1 => a2) the topmost node on the stack
        // is the "a" node. For the second transition (a => b) the topmost node
        // is the "X" node. And for the third transition (b1 => b2) the returned
        // value is the "b".
        test(top, GraphIterator.NEXT, "a,X,b");

        // Combined modes:

        // The default iteration mode - IN and LEAF. The iterator returns the
        // control when it enters in a node (empty or not).
        test(top, GraphIterator.IN | GraphIterator.LEAF, "X,a,a1,a2,b,b1,b2");
        // The "deep-first" iteration.
        test(top, GraphIterator.OUT | GraphIterator.LEAF, "a1,a2,a,b1,b2,b,X");

        // "Negative" modes. Returns the control for all transitions but not
        // when it is in a leaf node.
        test(top, ~GraphIterator.LEAF, "X,a,a,a,X,b,b,b,X");

    }

    private void test(Node top, int mode, String control) {
        final StringBuilder buf = new StringBuilder();
        GraphIterator<Node> iterator = new GraphIterator<Node>(top, mode) {
            @Override
            protected Node loadNextNode(Node parent, Node node) {
                if (parent == null)
                    return null;
                if (node == null)
                    return parent.getFirstChild();
                return node.getNextSibling();
            }
        };
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (buf.length() > 0)
                buf.append(",");
            buf.append(node);
        }
        System.out.println(buf);
        assertEquals(control, buf.toString());
    }

    /**
     * This method tests an iteration over a generated structure with two nodes
     * ("1" and "2") having two children each (node "1" has 2 children "11" and
     * "12"; the node "2" has also 2 children: "21" and "22").
     */
    public void testGraphIterator() {
        testTraces(
            GraphIterator.IN | GraphIterator.LEAF,
            "<1>[1]<11>[11]</11><12>[12]</12></1>");

        testTraces(GraphIterator.NEXT, "<1><11></11>[1]<12></12></1>");

        // Returns control *before* subnodes. Leaf nodes are not included.
        testTraces(GraphIterator.IN, "<1>[1]<11></11><12></12></1>");
        // Returns control *before* subnodes or in leaf nodes.
        testTraces(
            GraphIterator.IN | GraphIterator.LEAF,
            "<1>[1]<11>[11]</11><12>[12]</12></1>");

        // Returns control *after* all subnodes. Leaf nodes are not included.
        testTraces(GraphIterator.OUT, "<1><11></11><12></12>[1]</1>");
        // Returns control *after* all subnodes or in leaf nodes.
        testTraces(
            GraphIterator.OUT | GraphIterator.LEAF,
            "<1><11>[11]</11><12>[12]</12>[1]</1>");

        // Returns control only in leaf nodes.
        testTraces(GraphIterator.LEAF, "<1><11>[11]</11><12>[12]</12></1>");

        // Returns control every time when walker enters or goes out of a node.
        testTraces(
            GraphIterator.ALL,
            "<1>[1]<11>[11]</11>[1]<12>[12]</12>[1]</1>");

        // Never returns control. Just visits all nodes in one step. (not very
        // useful option).
        testTraces(0, "<1><11></11><12></12></1>");

        // Returns control when it enters or goes out of a state, but not for
        // leaf nodes.
        testTraces(~GraphIterator.LEAF, "<1>[1]<11></11>[1]<12></12>[1]</1>");
    }

    /**
     * This method generates a tree with two nodes and with two sub-nodes for
     * each node and iterates over this structure. The internal listener
     * generates traces for nods (when it enters and leaves a node). The
     * iterator itself writes also a trace. The resulting trace is compared to
     * the given control string.
     * 
     * @param mode the iteration mode
     * @param control the control string
     */
    private void testTraces(int mode, String control) {
        final StringBuilder buf = new StringBuilder();
        PrintListener<String> listener = new PrintListener<String>(
            false,
            true,
            false) {
            @Override
            protected void print(String string) {
                buf.append(string);
            }
        };
        Walker<String> walker = new Walker<String>(listener);
        GraphIterator<String> shifter = new GraphIterator<String>(walker, mode) {

            @Override
            protected String loadNextNode(String parent, String node) {
                if (parent.length() >= 2)
                    return null;
                if (node == null) {
                    node = parent + "1";
                    return node;
                }
                int len = node.length();
                char ch = node.charAt(len - 1);
                node = node.substring(0, len - 1);
                return ch < '2' ? node + ++ch : null;
            }

        };
        shifter.begin("1");
        while (shifter.hasNext()) {
            String s = shifter.next();
            listener.println("[" + s + "]");
        }
        System.out.println(buf);
        assertEquals(control, buf.toString());
    }
}
