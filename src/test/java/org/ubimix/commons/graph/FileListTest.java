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

import java.io.File;
import java.util.Iterator;

import org.ubimix.commons.graph.IteratorBasedGraphIterator;
import org.ubimix.commons.graph.PrintListener;

import junit.framework.TestCase;

/**
 * @author kotelnikov
 */
public class FileListTest extends TestCase {

    /**
     * @param name
     */
    public FileListTest(String name) {
        super(name);
    }

    public void test() throws Exception {
        PrintListener<File> printer = new PrintListener<File>(
            true,
            false,
            false) {
            @Override
            protected String getName(File node) {
                return node != null ? node.getName() : null;
            }
        };
        File file = new File("./");
        IteratorBasedGraphIterator<File> iterator = new IteratorBasedGraphIterator<File>(
            file,
            printer) {

            @Override
            protected Iterator<File> newIterator(File node) {
                if (!node.isDirectory())
                    return null;
                final File[] array = node.listFiles();
                if (array == null)
                    return null;
                return new Iterator<File>() {
                    int fPos;

                    public boolean hasNext() {
                        return fPos < array.length;
                    }

                    public File next() {
                        return array[fPos++];
                    }

                    public void remove() {
                    }

                };
            }
        };
        while (iterator.hasNext()) {
            file = iterator.next();
            printer.println("[" + file.getName() + "]");
        }
    }

}
