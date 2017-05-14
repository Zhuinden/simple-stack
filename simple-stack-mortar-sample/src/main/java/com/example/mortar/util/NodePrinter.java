/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.mortar.util;

import com.zhuinden.servicetree.ServiceTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NodePrinter {
    private NodePrinter() {
    }

    public static String scopeHierarchyToString(ServiceTree.Node mortarScope) {
        StringBuilder result = new StringBuilder("Scope Hierarchy:\n");
        ServiceTree.Node rootScope = getRootScope(mortarScope);
        Node rootNode = new PrintNode(rootScope);
        nodeHierarchyToString(result, 0, 0, rootNode);
        return result.toString();
    }

    interface Node {
        String getName();

        List<Node> getChildNodes();
    }

    private static class PrintNode
            implements Node {
        private final ServiceTree.Node mortarScope;

        PrintNode(ServiceTree.Node mortarScope) {
            this.mortarScope = mortarScope;
        }

        @Override
        public String getName() {
            return "SCOPE " + mortarScope.getKey().toString();
        }

        @Override
        public List<Node> getChildNodes() {
            List<Node> childNodes = new ArrayList<>();
            addScopeChildren(childNodes);
            return childNodes;
        }

        private void addScopeChildren(List<Node> childNodes) {
            for(ServiceTree.Node childScope : mortarScope.getChildren()) {
                childNodes.add(new PrintNode(childScope));
            }
        }
    }


    private static ServiceTree.Node getRootScope(ServiceTree.Node scope) {
        while(scope.getParent() != null) {
            scope = scope.getParent();
        }
        return scope;
    }

    private static void nodeHierarchyToString(StringBuilder result, int depth, long lastChildMask, Node node) {
        appendLinePrefix(result, depth, lastChildMask);
        result.append(node.getName()).append('\n');

        List<Node> childNodes = node.getChildNodes();
        Collections.sort(childNodes, new NodeSorter());

        int lastIndex = childNodes.size() - 1;
        int index = 0;
        for(Node childNode : childNodes) {
            if(index == lastIndex) {
                lastChildMask = lastChildMask | (1 << depth);
            }
            nodeHierarchyToString(result, depth + 1, lastChildMask, childNode);
            index++;
        }
    }

    private static void appendLinePrefix(StringBuilder result, int depth, long lastChildMask) {
        int lastDepth = depth - 1;
        // Add a non-breaking space at the beginning of the line because Logcat eats normal spaces.
        result.append('\u00a0');
        for(int parentDepth = 0; parentDepth <= lastDepth; parentDepth++) {
            if(parentDepth > 0) {
                result.append(' ');
            }
            boolean lastChild = (lastChildMask & (1 << parentDepth)) != 0;
            if(lastChild) {
                if(parentDepth == lastDepth) {
                    result.append('`');
                } else {
                    result.append(' ');
                }
            } else {
                if(parentDepth == lastDepth) {
                    result.append('+');
                } else {
                    result.append('|');
                }
            }
        }
        if(depth > 0) {
            result.append("-");
        }
    }

    private static class NodeSorter
            implements Comparator<Node> {
        @Override
        public int compare(Node lhs, Node rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}  