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
     * This interface represents a stack of nodes used by the walker class.
     * 
     * @author kotelnikov
     * @param <S>
     */
    public interface IWalkerStack<S> {

        /**
         * Returns <code>true</code> if this stack is empty.
         * 
         * @return <code>true</code> if this stack is empty
         */
        boolean isEmpty();

        /**
         * Returns the topmost object of the stack or <code>null</code> if this
         * stack is empty.
         * 
         * @return the object at the top of this stack.
         */
        S peek();

        /**
         * Removes and returns the object from the top of the stack. It returns
         * <code>null</code> if this stack is empty.
         * 
         * @return the removed topmost object of the stack
         */
        S pop();

        /**
         * Push the given object on the top of the stack.
         * 
         * @param object the object to add on the top of the stack
         */
        void push(S object);

    }

    /**
     * A simple implementation of the {@link IWalkerStack} interface.
     * 
     * @author kotelnikov
     * @param <S>
     */
    public static class WalkerStack<S> extends ArrayList<S>
        implements
        IWalkerStack<S> {
        private static final long serialVersionUID = 643178065970179980L;

        @Override
        public S peek() {
            return !isEmpty() ? get(size() - 1) : null;
        }

        @Override
        public S pop() {
            return !isEmpty() ? remove(size() - 1) : null;
        }

        @Override
        public void push(S object) {
            add(object);
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
        fStack = newStack();
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
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Walker<?>)) {
            return false;
        }
        Walker<S> o = (Walker<S>) obj;
        return fStack.equals(o.fStack);
    }

    /**
     * Returns the current active node; an active node is a node for which the
     * {@link IWalkerListener#onEnd(Object, Object)} was already called.
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
     * @return a new instance of the {@link IWalkerStack} interface
     */
    protected IWalkerStack<S> newStack() {
        return new WalkerStack<S>();
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
     * {@link IWalkerListener#onEnd(Object, Object)} method is called.
     * 
     * @param node the next node in the graph (or <code>null</code>)
     * @return <code>true</code> if the stack update was performed successfully
     */
    public boolean update(S node) {
        if (node == null && fStack.isEmpty()) {
            return false;
        }
        IWalkerListener<S> listener = fListener;
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
