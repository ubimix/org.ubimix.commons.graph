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
import org.ubimix.commons.graph.TreeBuilder;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class TreeBuilderTest extends TestCase {

    private StringBuilder fBuf = new StringBuilder();

    /**
     * @param name
     */
    public TreeBuilderTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        PrintListener<String> listener = new PrintListener<String>(
            false,
            true,
            false) {
            @Override
            protected void print(String string) {
                fBuf.append(string);
            }
        };
        TreeBuilder<String> b = new TreeBuilder<String>(listener);
        test(b, "<a><b>", "a", "b");
        test(b, "</b><b>", "a", "b");
        test(b, "<c>", "a", "b", "c");
        test(b, "</c><c>", "a", "b", "c");
        test(b, "</c></b><b>", "a", "b");
        test(b, "<c><d><e><f><g>", "a", "b", "c", "d", "e", "f", "g");
        test(b, "</g></f></e></d></c></b></a>");
        test(b, "<a><b><c><d><e><f><g>", "a", "b", "c", "d", "e", "f", "g");
        test(b, "</g><g>", "a", "b", "c", "d", "e", "f", "g");
        test(b, "</g></f><1>", "a", "b", "c", "d", "e", "1");
        test(b, "</1></e></d></c></b></a>");
    }

    private void test(TreeBuilder<String> b, String control, String... path) {
        fBuf.delete(0, fBuf.length());
        b.align(path);
        assertEquals(control, fBuf.toString());
    }

}
