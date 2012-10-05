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
     * The path used as a source of nodes
     */
    private T[] fPath;

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
    @SuppressWarnings("unchecked")
    public TreeBuilder(Walker<T> walker) {
        super();
        fPath = toArray();
        fWalker = walker;
    }

    /**
     * The main method of this class; it re-builds the tree structure by
     * individual paths.
     * 
     * @param path the next path used to re-build the tree structure
     */
    public void align(T... path) {
        int len = Math.min(path.length, fPath.length);
        int i;
        for (i = 0; i < len; i++) {
            T a = fPath[i];
            T b = path[i];
            if (!equals(a, b))
                break;
        }
        if (i == path.length)
            i--;
        for (int j = fPath.length - 1; j >= i; j--) {
            fWalker.end();
        }
        for (; i >= 0 && i < path.length; i++) {
            T node = path[i];
            fWalker.begin(node);
        }
        fPath = path;
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

    /**
     * Returns the given parameters in the form of an array
     * 
     * @param param the parameters to transform into an array
     * @return the given parameters in the form of an array
     */
    private T[] toArray(T... param) {
        return param;
    }

}