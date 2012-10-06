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

import java.util.Comparator;

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

    public void testPathBasedTreeBuilding() throws Exception {
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
        testPathBasedTreeBuilding(b, "<a><b>", "a", "b");
        testPathBasedTreeBuilding(b, "</b><b>", "a", "b");
        testPathBasedTreeBuilding(b, "<c>", "a", "b", "c");
        testPathBasedTreeBuilding(b, "</c><c>", "a", "b", "c");
        testPathBasedTreeBuilding(b, "</c></b><b>", "a", "b");
        testPathBasedTreeBuilding(
            b,
            "<c><d><e><f><g>",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g");
        testPathBasedTreeBuilding(b, "</g></f></e></d></c></b></a>");
        testPathBasedTreeBuilding(
            b,
            "<a><b><c><d><e><f><g>",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g");
        testPathBasedTreeBuilding(
            b,
            "</g><g>",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g");
        testPathBasedTreeBuilding(
            b,
            "</g></f><1>",
            "a",
            "b",
            "c",
            "d",
            "e",
            "1");
        testPathBasedTreeBuilding(b, "</1></e></d></c></b></a>");
    }

    private void testPathBasedTreeBuilding(
        TreeBuilder<String> b,
        String control,
        String... path) {
        fBuf.delete(0, fBuf.length());
        b.align(path);
        assertEquals(control, fBuf.toString());
    }

    public void testWeightBasedTreeBuilding() {
        testWeightBasedTreeBuilding("<h1></h1>", "h1");
        testWeightBasedTreeBuilding("<h1></h1><h1></h1>", "h1", "h1");
        testWeightBasedTreeBuilding("<h1><h2></h2></h1>", "h1", "h2");
        testWeightBasedTreeBuilding(
            "<h1><h2></h2><h2></h2></h1>",
            "h1",
            "h2",
            "h2");
        testWeightBasedTreeBuilding(
            "<h1><h2></h2><h2></h2></h1>",
            "h1",
            "h2",
            "h2");
        testWeightBasedTreeBuilding(
            "<h1><h5></h5><h2></h2></h1>",
            "h1",
            "h5",
            "h2");
        testWeightBasedTreeBuilding(
            "<h1><h3><h5></h5></h3><h2></h2></h1>",
            "h1",
            "h3",
            "h5",
            "h2");
        testWeightBasedTreeBuilding(
            "<h1><h2><h5></h5></h2><h2><h4><h5></h5></h4><h3></h3></h2></h1>",
            "h1",
            "h2",
            "h5",
            "h2",
            "h4",
            "h5",
            "h3");
    }

    private void testWeightBasedTreeBuilding(String control, String... strings) {
        final StringBuilder buf = new StringBuilder();
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return -o1.compareTo(o2);
            }
        };
        TreeBuilder<String> builder = new TreeBuilder<String>(
            new PrintListener<String>(false, true, false) {
                @Override
                protected void print(String string) {
                    buf.append(string);
                }
            });
        for (String string : strings) {
            builder.align(string, comparator);
        }
        builder.close();
        assertEquals(control, buf.toString());
    }

}
