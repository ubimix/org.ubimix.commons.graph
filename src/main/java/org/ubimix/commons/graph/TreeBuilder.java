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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class is used to recreate a tree structure from individual paths.
 * Example of usage:
 * 
 * <pre>
 * // The example below shows how to re-build the tree structure from individual
 * // paths. 
 * // Paths:
 * // - a/b/c
 * // - a/x/y
 * // The corresponding tree:
 * // - a 
 * //   - b
 * //     - c
 * //   - x
 * //     - y 
 * 
 * // This listener will be notified when a new node is opened or closed. 
 * INodeWalkerListener listener = ... ;  
 * TreeBuilder builder = new TreeBuilder(listener);
 * 
 * // This method splits the given string by the "/" symbols and returns 
 * // the corresponding array of path segments. 
 * String[] path = getPath("a/b/c"); 
 * 
 * // Notifies about nodes of the graph corresponding to the path "a/b/c" 
 * builder.align(path);
 * 
 * path = getPath("a/x/y");
 * // Notifies about new nodes "x" and "y"
 * builder.align(path);
 * 
 * // Finalizes the tree building
 * path.align();
 * </pre>
 * 
 * @author kotelnikov
 */
public class TreeBuilder<T> {

    /**
     * The internal walker translating {@link Walker#begin(Object)}/
     * {@link Walker#end()} method calls into calls to the registered listeners.
     */
    private Walker<T> fWalker;

    /**
     * This constructor initializes the internal fields and registers the given
     * listener which will be used to notify about individual graph nodes
     * reconstructed from the
     * 
     * @param listener
     */
    public TreeBuilder(IWalkerListener<T> listener) {
        this(new Walker<T>(listener));
    }

    /**
     * The common constructor used to initialize internal fields
     * 
     * @throws E
     */
    public TreeBuilder(Walker<T> walker) {
        super();
        fWalker = walker;
    }

    /**
     * The main method of this class; it re-builds the tree structure by
     * individual paths.
     * 
     * @param path the next path used to re-build the tree structure
     */
    public void align(List<T> path) {
        List<T> stack = fWalker.getStack();
        int len = Math.min(path.size(), stack.size());
        int i;
        for (i = 0; i < len; i++) {
            T a = stack.get(i);
            T b = path.get(i);
            if (!equals(a, b)) {
                break;
            }
        }
        if (i == path.size()) {
            i--;
        }
        for (int j = stack.size() - 1; j >= i; j--) {
            fWalker.end();
        }
        for (; i >= 0 && i < path.size(); i++) {
            T node = path.get(i);
            fWalker.begin(node);
        }
    }

    /**
     * The main method of this class; it re-builds the tree structure by
     * individual paths.
     * 
     * @param path the next path used to re-build the tree structure
     */
    public void align(T... path) {
        align(Arrays.asList(path));
    }

    /**
     * This method is used to build tree-structures based on relative "weights"
     * of nodes. Heavy nodes are near to the root; lighter nodes are on the
     * leaf.
     * 
     * @param node the node to add to the tree
     * @param comparator comparator used to compare nodes in the stack with new
     *        nodes
     */
    public void align(T node, Comparator<T> comparator) {
        List<T> stack = fWalker.getStack();
        int len = stack.size();
        int i;
        for (i = 0; i < len; i++) {
            T a = stack.get(i);
            if (comparator.compare(node, a) >= 0) {
                break;
            }
        }
        for (int j = len - 1; j >= i; j--) {
            fWalker.end();
        }
        fWalker.begin(node);
    }

    /**
     * Finalizes the tree building and pops all existing elements.
     */
    public void close() {
        while (!fWalker.isFinished()) {
            fWalker.end();
        }
    }

    /**
     * This method is used to compare individual path items.
     * 
     * @param a the first item to compare
     * @param b the second path segment to compare
     * @return <code>true</code> if the given path segments are equals
     */
    protected boolean equals(T a, T b) {
        return a == null || b == null ? a == b : a.equals(b);
    }

    public Walker<T> getWalker() {
        return fWalker;
    }

}