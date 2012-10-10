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
 * This class is used to generate XML-like traces for all nodes visited by
 * {@link Walker}. So it can be used by all classes based on the {@link Walker}
 * (like {@link GraphIterator} or {@link TreeBuilder}.
 * 
 * @author kotelnikov
 * @param <S> the type of visited graph nodes
 */
public class PrintListener<S> implements IWalkerListener<S> {

    protected int fDepth;

    protected boolean fIdent;

    protected boolean fPrintNodes;

    protected boolean fPrintTransitions;

    public PrintListener() {
        this(true, true, false);
    }

    public PrintListener(
        boolean ident,
        boolean printNodes,
        boolean printTransitions) {
        fIdent = ident;
        fPrintNodes = printNodes;
        fPrintTransitions = printTransitions;
    }

    protected String getName(S node) {
        return node != null ? node.toString() : null;
    }

    @Override
    public void onBegin(S parent, S node) {
        if (fPrintNodes) {
            String name = getName(node);
            println("<" + name + ">");
        }
        fDepth++;
    }

    @Override
    public void onEnd(S parent, S node) {
        fDepth--;
        if (fPrintNodes) {
            String name = getName(node);
            println("</" + name + ">");
        }
    }

    protected void print(String string) {
        System.out.print(string);
    }

    public void println(String string) {
        if (fIdent) {
            for (int i = 0; i < fDepth; i++) {
                print("  ");
            }
        }
        print(string);
        if (fIdent) {
            print("\n");
        }
    }

}