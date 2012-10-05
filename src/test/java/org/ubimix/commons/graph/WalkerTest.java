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

import org.ubimix.commons.graph.PrintListener;
import org.ubimix.commons.graph.Walker;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class WalkerTest extends TestCase {

    private StringBuilder fBuf = new StringBuilder();

    /**
     * @param name
     */
    public WalkerTest(String name) {
        super(name);
    }

    private void begin(Walker<String> w, String node, String control) {
        testStep(w, node, control);
    }

    private void empty(
        Walker<String> w,
        String node,
        String controlIn,
        String controlOut) {
        testStep(w, node, controlIn);
        testStep(w, null, controlOut);
    }

    private void end(Walker<String> w, String control) {
        testStep(w, null, control);
    }

    public void test() throws Exception {
        PrintListener<String> listener = new PrintListener<String>(
            false,
            true,
            true) {
            @Override
            protected void print(String string) {
                fBuf.append(string);
            }
        };
        Walker<String> w = new Walker<String>(listener);
        begin(w, "a", "<a>");
        {
            empty(
                w,
                "a1",
                "<transition parent='a' from='null' to='a1' /><a1>",
                "<transition parent='a1' from='null' to='null' /></a1>");
            empty(
                w,
                "a2",
                "<transition parent='a' from='a1' to='a2' /><a2>",
                "<transition parent='a2' from='null' to='null' /></a2>");
            begin(w, "a3", "<transition parent='a' from='a2' to='a3' /><a3>");
            {
                empty(
                    w,
                    "a3.1",
                    "<transition parent='a3' from='null' to='a3.1' /><a3.1>",
                    "<transition parent='a3.1' from='null' to='null' /></a3.1>");
                empty(
                    w,
                    "a3.2",
                    "<transition parent='a3' from='a3.1' to='a3.2' /><a3.2>",
                    "<transition parent='a3.2' from='null' to='null' /></a3.2>");
                empty(
                    w,
                    "a3.3",
                    "<transition parent='a3' from='a3.2' to='a3.3' /><a3.3>",
                    "<transition parent='a3.3' from='null' to='null' /></a3.3>");
            }
            end(w, "<transition parent='a3' from='a3.3' to='null' /></a3>");
        }
        end(w, "<transition parent='a' from='a3' to='null' /></a>");
    }

    private void testStep(Walker<String> w, String node, String control) {
        fBuf.delete(0, fBuf.length());
        w.update(node);
        assertEquals(control, fBuf.toString());
    }

}
