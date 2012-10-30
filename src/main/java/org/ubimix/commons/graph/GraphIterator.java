/**
 * 
 */
package org.ubimix.commons.graph;

import java.util.Iterator;

/**
 * This class is used to iterate over graph structures. It returns all graph
 * nodes one-by-one until the whole structure is traversed. Iteration "modes"
 * are used to define when this iterator returns control to the caller. This
 * iterator could returns the control when it enters in a new graph node, when
 * it leaves a node, when it is in a "leaf" node (which has no children) or when
 * it goes to the next node (when it is already leaved the previous node, but
 * not entered in a new node). Any combinations of these modes are possible. To
 * define the iteration mode the OR combinations of the following internal masks
 * should be used: {@link #IN}, {@link #OUT}, {@link #LEAF}, {@link #NEXT}. The
 * {@link #ALL} mask could be used to return the control after each step (every
 * time when the iterator enters in a node or leaves it). The iteration modes
 * could be defined using the constructor of this class or using the
 * {@link #setMode(int)} method.
 * <p>
 * Below you can find examples of various iteration modes.
 * </p>
 * 
 * <pre>
 * Imagine the following graph structure:
 * - X
 *  - a
 *   - a1
 *   - a2
 *  - b
 *   - b1
 *   - b2
 *   
 * Below you can find sequences of nodes returned by this iterator for each mode.
 * 
 * "IN": X,a,b
 * Note: The iterator returns the control only for non-empty nodes after
 * entering in each node.
 *      
 * "OUT": a,b,X
 * Note: The iterator returns the control after leaving a non-empty node.
 * 
 * "LEAF": a1,a2,b1,b2
 * Note: The iterator returns the control after entering in a leaf node
 * (which has no children)
 * 
 * "NEXT": a,X,b
 * Note: This mode returns the control when the iterator goes from one node 
 * into an another. In this tree this kind of transition happens 3 times:
 *  1) a1 => a2
 *  2) a => b
 *  3) b1 => b2
 * Every time the iterator returns the topmost node in the stack. 
 * So in the first transition (a1 => a2) the topmost node on the stack is the 
 * "a" node. For the second transition (a => b) the topmost node is the "X" node.
 * And for the third transition (b1 => b2) the returned value is the "b".
 * 
 * Combined modes:
 * 
 * "IN|LEAF": X,a,a1,a2,b,b1,b2
 * Note: This is the default iteration mode. The iterator returns the control
 * when it enters in a node (empty or not).
 * 
 * "OUT|LEAF": a1,a2,a,b1,b2,b,X
 * Note: This is the "deep-first" iteration.
 * 
 * "Negative" modes.
 * "~LEAF": "X,a,a,a,X,b,b,b,X"
 * Note: The iterator returns the control for all transitions but not when it 
 * is in a leaf node.
 * 
 * </pre>
 * 
 * @author kotelnikov
 * @param <T> the type of the iterated nodes
 */
public abstract class GraphIterator<T> implements Iterator<T> {

    /**
     * The {@link #shiftItem()} method returns the control every time when it
     * enters in or goes out of the node.
     */
    public final static int ALL = 1 | 2 | 4 | 8; // IN | LEAF | OUT | NEXT

    /**
     * The {@link #shiftItem()} method returns the control every time when it
     * enters in a new node.
     */
    public final static int IN = 1;

    /**
     * The {@link #shiftItem()} method returns the control every time when it
     * enters in a leaf node (which does not have any children).
     */
    public final static int LEAF = 2;

    /**
     * The "deep first" iteration mode. It is a combination of the {@link #OUT}
     * and {@link #LEAF} masks.
     */
    public final static int MODE_DEEP_FIRST;

    /**
     * The "default" iteration mode which returns nodes when the iterator enters
     * in a node. It is a combination of the {@link #IN} and {@link #LEAF}
     * masks.
     */
    public final static int MODE_DEFAULT;

    /**
     * The {@link #shiftItem()} method returns the control every time when it g
     * oes to the next node.
     */
    public final static int NEXT = 8;

    /**
     * The {@link #shiftItem()} method returns the control every time when it
     * goes out of a node.
     */
    public final static int OUT = 4;

    /**
     * Initialization of constant fields.
     */
    static {
        MODE_DEFAULT = IN | LEAF;
        MODE_DEEP_FIRST = OUT | LEAF;
    }

    /**
     * This flag shows if the next node was already loaded or not.
     */
    private boolean fDone;

    /**
     * The flag defining the iteration mode for this class. The iteration mode
     * defines when this iterator returns the control to the caller - when it
     * enters in a new node, exits from a node and so on. This field should be a
     * combination of the following flags: {@link #ALL}, {@link #IN},
     * {@link #LEAF}, {@link #NONE} or {@link #OUT}.
     */
    private int fMode;

    /**
     * The next node to load.
     */
    protected T fNextNode;

    /**
     * Current iteration status. It could be one of the following values:
     * {@link #IN}, {@link #OUT} or {@link #LEAF}.
     */
    private int fStatus;

    /**
     * A node walker containing the current stack of nodes
     */
    protected Walker<T> fWalker;

    /**
     * Creates an iterator over the graph with the specified topmost graph node.
     * It uses the default iteration mode ({@link #IN}|{@link #LEAF}).
     * 
     * @param top the topmost graph node
     */
    public GraphIterator(T top) {
        this(top, MODE_DEFAULT);
    }

    /**
     * This constructor activates this iterator with the specified topmost graph
     * node and sets the given iteration mode.
     * 
     * @param top the topmost graph node used as a starting point for this
     *        iterator
     * @param mode the itertor mode
     */
    public GraphIterator(T top, int mode) {
        this(top, null, mode);
    }

    /**
     * This constructor initializes this iterator with the specified topmost
     * graph node and the given iterator listener. The listener is used to
     * notify when this iterator enters in a graph node or leaves it. This
     * constructor sets the default iteration mode (see {@link #MODE_DEFAULT}).
     * 
     * @param top the topmost graph node used as a starting point for this
     *        iterator
     * @param listener the listener notified about individual steps of this
     *        iterator
     */
    public GraphIterator(T top, IWalkerListener<T> listener) {
        this(top, listener, MODE_DEFAULT);
    }

    /**
     * This constructor initializes this iterator with the specified topmost
     * graph node and sets the iteration mode. The given listener is used to
     * notify when this iterator enters in a graph node or leaves it.
     * 
     * @param top the topmost graph node used as a starting point for this
     *        iterator
     * @param listener the listener notified about individual steps of this
     *        iterator
     * @param mode the iteration mode
     */
    public GraphIterator(T top, IWalkerListener<T> listener, int mode) {
        this(new Walker<T>(listener != null
            ? listener
            : new WalkerListener<T>()), mode);
        begin(top);
    }

    /**
     * Initializes the internal fields.
     * 
     * @param walker the walker to set
     */
    public GraphIterator(Walker<T> walker) {
        this(walker, MODE_DEFAULT);
    }

    /**
     * This constructor sets the given walker
     * 
     * @param walker the graph walker
     * @param mode the iteration mode; it could be one of the following values:
     *        {@link #IN}, {@link #OUT}, {@link #LEAF} or {@link #ALL}
     */
    public GraphIterator(Walker<T> walker, int mode) {
        fWalker = walker;
        setMode(mode);
    }

    /**
     * Begins the iteration process.
     * 
     * @param topNode the topmost node of the graph; starting from this node the
     *        graph iteration is started Exception,
     */
    public void begin(T topNode) {
        fNextNode = topNode;
    }

    /**
     * Returns the current iteration mode. It is an OR combination of the
     * following masks: {@link #ALL}, {@link #IN}, {@link #LEAF}, {@link #NONE}
     * or {@link #OUT}.
     * 
     * @return the mask defining the iteration mode
     */
    public int getMode() {
        return fMode;
    }

    /**
     * Returns the previous node used by the Walker in previous step.
     * 
     * @return the previous node handled by the Walker.
     */
    public T getPrevious() {
        return fWalker.getPrevious();
    }

    /**
     * Returns the current iteration status; it could be one of the following
     * values: {@link #IN}, {@link #OUT} or {@link #LEAF}.
     * 
     * @return the current iteration status
     */
    public int getStatus() {
        return fStatus;
    }

    /**
     * Returns the internal walker object
     * 
     * @return the internal walker object
     */
    public Walker<T> getWalker() {
        return fWalker;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        T obj = shift(true);
        return obj != null;
    }

    /**
     * Return the next sibling of the specified node
     * 
     * @param parent the parent node
     * @param node the previous node; it could be <code>null</code>
     * @return the next node for the specified parent and the previous node
     */
    protected abstract T loadNextNode(T parent, T node);

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public T next() {
        return shift(false);
    }

    /**
     * Loads and returns the next node to load. It updates the internal status
     * of the iterator.
     * 
     * @return the next node to load
     */
    protected T prepareNextNode() {
        T parent = fWalker.getCurrent();
        T previous = fWalker.getPrevious();
        T next = loadNextNode(parent, previous);
        fStatus = 0;
        if (previous == null) {
            if (next != null) {
                fStatus = IN;
            } else {
                fStatus = LEAF;
            }
        } else {
            if (next != null) {
                fStatus = NEXT;
            } else {
                fStatus = OUT;
            }
        }
        fNextNode = next;
        return next;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the mask defining the iteration mode. It should be an OR combination
     * of the following masks: {@link #ALL}, {@link #IN}, {@link #LEAF},
     * {@link #NONE} or {@link #OUT}.
     * 
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        fMode = mode;
    }

    /**
     * Loads the next node (if neccessery) and returns the current active node
     * or <code>null</code> if there is nothing to return.
     * 
     * @param b the value of the {@link #fDone} field to set
     * @return the next node in the tree or <code>null</code> if there is no
     *         more nodes to return.
     */
    protected T shift(boolean b) {
        if (!fDone) {
            while (fWalker.update(fNextNode)) {
                if (fWalker.getCurrent() == null) {
                    fNextNode = null;
                    break;
                }
                prepareNextNode();
                if ((fStatus & fMode) != 0) {
                    break;
                }
            }
        }
        fDone = b;
        return fWalker.getCurrent();
    }
}
