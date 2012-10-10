/**
 * 
 */
package org.ubimix.commons.graph;

/**
 * Instances of this type are used to notify when the {@link Walker} enters in a
 * graph node or leaves it.
 * 
 * @author kotelnikov
 * @param <S> the type of nodes visited by a {@link Walker}
 */
public interface IWalkerListener<S> {

    /**
     * Initializes the specified node. This node is not inserted in the walker
     * stack.
     * 
     * @param parent the parent of the activated node
     * @param node the node to activate
     */
    void onBegin(S parent, S node);

    /**
     * This method is called to deactivate the specified node. When this method
     * is called the given node is already removed from the walker stack.
     * 
     * @param parent the parent of the deactivated node
     * @param node the node to deactivate.
     */
    void onEnd(S parent, S node);

}