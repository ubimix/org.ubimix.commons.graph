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
 * **************************************************************************
 */
package org.ubimix.commons.graph;

import java.util.Iterator;
import java.util.Stack;

/**
 * @author kotelnikov
 */
public abstract class IteratorBasedGraphIterator<S> extends GraphIterator<S> {

    private Stack<Iterator<S>> fStack = new Stack<Iterator<S>>();

    /**
     * @see GraphIterator#GraphIterator(Object)
     */
    public IteratorBasedGraphIterator(S top) {
        super(top);
    }

    /**
     * @see GraphIterator#GraphIterator(Object, int)
     */
    public IteratorBasedGraphIterator(S top, int mode) {
        super(top, mode);
    }

    /**
     * @see GraphIterator#GraphIterator(Object, IWalkerListener, int)
     */
    public IteratorBasedGraphIterator(S top, IWalkerListener<S> listener) {
        super(top, listener);
    }

    /**
     * @see GraphIterator#GraphIterator(Object, IWalkerListener, int)
     */
    public IteratorBasedGraphIterator(
        S top,
        IWalkerListener<S> listener,
        int mode) {
        super(top, listener, mode);
    }

    /**
     * @see GraphIterator#GraphIterator(Walker)
     */
    public IteratorBasedGraphIterator(Walker<S> walker) {
        super(walker);
    }

    /**
     * @see GraphIterator#GraphIterator(Walker, int)
     */
    public IteratorBasedGraphIterator(Walker<S> walker, int mode) {
        super(walker, mode);
    }

    /**
     * This method could be re-defined in subclasses if it is required to
     * "close/destroy" iterators returned by the {@link #newIterator(Object)}
     * method.
     * 
     * @param iterator the iterator to destroy
     */
    protected void deleteIterator(Iterator<S> iterator) {
    }

    /**
     * Returns the next node returned by the topmost iterator.
     * 
     * @return the next node returned by the topmost iterator.
     */
    private S loadNext() {
        Iterator<S> iterator = !fStack.isEmpty() ? fStack.peek() : null;
        if (iterator == null) {
            return null;
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    /**
     * @see org.ubimix.commons.graph.GraphIterator#loadNextNode(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    protected S loadNextNode(S parent, S node) {
        if (node == null) {
            Iterator<S> iterator = parent != null ? newIterator(parent) : null;
            fStack.push(iterator);
        } else {
            Iterator<S> iterator = fStack.pop();
            deleteIterator(iterator);
        }
        S next = loadNext();
        return next;
    }

    /**
     * Returns an iterator over all direct children of the specified node.
     * 
     * @param node the node for which the corresponding children should be
     *        returned
     * @return an iterator over all direct child nodes of the specified node
     */
    protected abstract Iterator<S> newIterator(S node);

}
