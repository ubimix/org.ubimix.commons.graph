/**
 * 
 */
package org.ubimix.commons.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite listeners are used to dispatch calls for multiple listeners.
 * Example of usage:
 * 
 * <pre>
 * CompositeWalkerListener&lt;String&gt; composite = new CompositeWalkerListener&lt;String&gt;();
 * composite(new MyFirstListener&lt;String&gt;());
 * composite(new MySecondListener&lt;String&gt;());
 * 
 * </pre>
 * 
 * @author kotelnikov
 */
public class CompositeWalkerListener<S> implements IWalkerListener<S> {

    private List<IWalkerListener<S>> fList = new ArrayList<IWalkerListener<S>>();

    /**
     * 
     */
    public CompositeWalkerListener() {
    }

    public void addListener(IWalkerListener<S> listener) {
        ArrayList<IWalkerListener<S>> list = new ArrayList<IWalkerListener<S>>(
            fList);
        list.add(listener);
        fList = list;
    }

    public void onBegin(S parent, S node) {
        for (IWalkerListener<S> listener : fList) {
            listener.onBegin(parent, node);
        }
    }

    public void onEnd(S parent, S node) {
        for (IWalkerListener<S> listener : fList) {
            listener.onEnd(parent, node);
        }
    }

    public void onTransition(S parent, S prev, S next) {
        for (IWalkerListener<S> listener : fList) {
            listener.onTransition(parent, prev, next);
        }
    }

    public void removeListener(IWalkerListener<S> listener) {
        ArrayList<IWalkerListener<S>> list = new ArrayList<IWalkerListener<S>>(
            fList);
        list.remove(listener);
        fList = list;
    }

}
