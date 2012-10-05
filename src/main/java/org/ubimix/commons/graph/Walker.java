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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This is a helper class used to implement graph traversal algorithms. Objects
 * of this type are used to translate simple calls of the {@link #begin(Object)}
 * /{@link #end()} methods in calls to {@link IWalkerListener} instances. This
 * object contains an internal stack which keeps all graph nodes "activated"
 * with the {@link #begin(Object)} method and removes these objects from the
 * stack when the {@link #end()} method is called. This very basic functionality
 * is used by other (high-level) classes to implement graph iterators and FSMs.
 * 
 * @author kotelnikov
 * @param <S> the type of graph nodes
 */
public class Walker<S> {

    /**
     * This interface represents stack of graph nodes.
     * 
     * @author kotelnikov
     * @param <S> the type of graph nodes
     */
    public interface IWalkerStack<S> {

        /**
         * Copies all nodes from this stack into the given collection.
         * 
         * @param collection the collection where all nodes from this stack
         *        should be copied to
         */
        void copy(Collection<? super S> collection);

        /**
         * Returns an iterator over all nodes in this stack
         * 
         * @param direct if this flag is <code>true</code> then this method
         *        iterates over all nodes in the stack in the same order as they
         *        are stored in this stack; otherwise the order of nodes is
         *        inverted
         * @return an iterator over all nodes in the stack
         */
        Iterator<S> getIterator(boolean direct);

        /**
         * Returns <code>true</code> if this stsack is empty.
         * 
         * @return <code>true</code> if this stack is empty
         */
        boolean isEmpty();

        /**
         * Returns the topmost node in this stack.
         * 
         * @return the topmost node in this stack
         */
        S peek();

        /**
         * Removes the topmost node from this stack and returns it.
         * 
         * @return the removed topmost node from this stack.
         */
        S pop();

        /**
         * Adds the given node on the top of this stack.
         * 
         * @param node the node to push in the stack.
         */
        void push(S node);

    }

    /**
     * An array-based implementation of the {@link IWalkerStack} interface.
     * 
     * @author kotelnikov
     * @param <S> the type of the node
     */
    public static class WalkerStack<S> implements IWalkerStack<S> {

        /**
         * This list keeps all nodes of this stack.
         */
        private List<S> fStack = new ArrayList<S>();

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#copy(java.util.List)
         */
        public void copy(Collection<? super S> list) {
            list.addAll(fStack);
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (!(obj instanceof Walker.WalkerStack<?>))
                return false;
            Walker.WalkerStack<?> o = (Walker.WalkerStack<?>) obj;
            return fStack.equals(o.fStack);
        }

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#getIterator(boolean)
         */
        public Iterator<S> getIterator(boolean direct) {
            if (direct) {
                return fStack.iterator();
            } else {
                final int size = fStack.size();
                return new Iterator<S>() {
                    int pos = size;

                    public boolean hasNext() {
                        return pos > 0;
                    }

                    public S next() {
                        if (pos <= 0)
                            return null;
                        pos--;
                        return fStack.get(pos);
                    }

                    public void remove() {
                    }

                };
            }
        }

        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return fStack.hashCode();
        }

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#isEmpty()
         */
        public boolean isEmpty() {
            return fStack.isEmpty();
        }

        /**
         * @see java.lang.Iterable#iterator()
         */
        public Iterator<S> iterator() {
            return fStack.iterator();
        }

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#peek()
         */
        public S peek() {
            return !fStack.isEmpty() ? fStack.get(fStack.size() - 1) : null;
        }

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#pop()
         */
        public S pop() {
            return !fStack.isEmpty() ? fStack.remove(fStack.size() - 1) : null;
        }

        /**
         * @see org.ubimix.commons.graph.Walker.IWalkerStack#push(java.lang.Object)
         */
        public void push(S state) {
            fStack.add(state);
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return fStack.toString();
        }
    }

    /**
     * This listener is used to notify when the walker enters in a node or goes
     * out of a node.
     */
    private IWalkerListener<S> fListener;

    /**
     * The previous node.
     */
    private S fPrev;

    /**
     * The stack of nodes.
     */
    private IWalkerStack<S> fStack;

    /**
     * The default constructor used to set the listener.
     * 
     * @param listener the listener to set
     */
    public Walker(IWalkerListener<S> listener) {
        this(new WalkerStack<S>(), listener);
    }

    /**
     * This constructor is used to explicitly define the stack of nodes and set
     * a node listener.
     * 
     * @param stack the stack to set
     * @param listener the listener to set
     */
    public Walker(IWalkerStack<S> stack, IWalkerListener<S> listener) {
        super();
        fStack = stack;
        fListener = listener;
    }

    /**
     * Beginning of of a new node in the tree structure. This method calls the
     * {@link #update(Object)} with the given node as a parameter.
     * 
     * @param node the node to start.
     * @return the result of the {@link #update(Object)} operation
     * @see #update(Object)
     */
    public boolean begin(S node) {
        return update(node);
    }

    /**
     * Copies all nodes from the internal stack in the given collection.
     * 
     * @param collection the target collection where all nodes from the internal
     *        stack should be copied to
     */
    public void copyStack(Collection<? super S> collection) {
        fStack.copy(collection);
    }

    /**
     * Initializes a new node and finishes it immediately. This method is used
     * to notify about empty (leaf) nodes.
     * 
     * @param node an empty node
     * @return the result of the {@link #update(Object)} operation
     */
    public boolean empty(S node) {
        update(node);
        return update(null);
    }

    /**
     * Finishes the current node in the tree structure (pops it up from the
     * stack). This method just calls the {@link #update(Object)} method with an
     * empty (<code>null</code>) parameter.
     * 
     * @param node the node to start.
     * @return the result of the {@link #update(Object)} operation
     * @see #update(Object)
     */
    public boolean end() {
        return update(null);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Walker<?>))
            return false;
        Walker<S> process = (Walker<S>) obj;
        return fStack.equals(process.fStack);
    }

    /**
     * Returns the current active node; an active node is a node for which the
     * {@link IFsmProcessListener#beginState(List<S>, Walker<?>)} was already
     * called.
     * 
     * @return the current active state
     */
    public S getCurrent() {
        return fStack.peek();
    }

    /**
     * Returns the internal listener which is notified about new nodes pushed in
     * the stack of removed from the stack.
     * 
     * @return the internal listener
     */
    public IWalkerListener<S> getListener() {
        return fListener;
    }

    /**
     * Returns the previous node
     * 
     * @return the previous node
     */
    public S getPrevious() {
        return fPrev;
    }

    /**
     * Returns the underlying stack associated with this walker.
     * 
     * @return the stack of this walker
     */
    public IWalkerStack<S> getStack() {
        return fStack;
    }

    /**
     * Returns an iterator over all nodes in the stack.
     * 
     * @return an iterator over all nodes in the stack
     */
    public Iterator<S> getStackIterator(boolean direct) {
        return fStack.getIterator(direct);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return fStack.hashCode();
    }

    /**
     * Returns <code>true</code> if the this walker activated a new node on the
     * previous step.
     * 
     * @return <code>true</code> if the this walker activated a new node on the
     *         previous step.
     */
    public boolean isEntered() {
        return fPrev == null && !fStack.isEmpty();
    }

    /**
     * Returns <code>true</code> if this walker leaved a graph node in the
     * previous step
     * 
     * @return <code>true</code> if this walker leaved a graph node in the
     *         previous step
     */
    public boolean isExit() {
        return fPrev != null;
    }

    /**
     * Returns <code>true</code> if the iteration process is finished (ie if the
     * internal stack is empty and the previous node is <code>null</code>).
     * 
     * @return <code>true</code> if the iteration process is finished
     */
    public boolean isFinished() {
        return fStack.isEmpty();
    }

    /**
     * Returns <code>true</code> if the stack is empty.
     * 
     * @return <code>true</code> if the stack is empty.
     */
    public boolean isStackEmpty() {
        return fStack.isEmpty();
    }

    /**
     * Sets a new listener used to notify about graph nodes iterated by this
     * walker.
     * 
     * @param listener the listener to set
     */
    public void setListener(IWalkerListener<S> listener) {
        fListener = listener;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return fStack.toString();
    }

    /**
     * This method updates the internal state of the walker and notifies the
     * registered listener. If the given node is not empty (it is not
     * <code>null</code>) then this method adds it on the stack and calls the
     * {@link IWalkerListener#onBegin(Object, Object)} method. Otherwise the
     * topmost node on the stack is removed and the
     * {@link IWalkerListener#onEnd(Object, Object)} method is called. This
     * method also notifies about "transitions" between nodes using the
     * {@link IWalkerListener#onTransition(Object, Object, Object)} method.
     * 
     * @param node the next node in the graph (or <code>null</code>)
     * @return <code>true</code> if the stack update was performed successfully
     */
    public boolean update(S node) {
        if (node == null && fStack.isEmpty()) {
            return false;
        }
        IWalkerListener<S> listener = fListener;
        if (!fStack.isEmpty()) {
            S parent = fStack.peek();
            listener.onTransition(parent, fPrev, node);
        }

        if (node != null) {
            fPrev = null;
            S parent = fStack.peek();
            listener.onBegin(parent, node);
            fStack.push(node);
        } else {
            fPrev = fStack.pop();
            S parent = fStack.peek();
            listener.onEnd(parent, fPrev);
        }
        return true;
    }
}