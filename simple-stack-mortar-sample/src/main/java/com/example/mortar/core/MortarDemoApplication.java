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
package com.example.mortar.core;

import android.app.Application;

import com.example.mortar.nodes.TreeNodes;
import com.example.mortar.util.DaggerService;
import com.zhuinden.servicetree.ServiceTree;

public class MortarDemoApplication
        extends Application {
    private ServiceTree serviceTree;

    @Override
    public Object getSystemService(String name) {
        if(serviceTree == null) {
            serviceTree = new ServiceTree();
            serviceTree.registerRootService(DaggerService.SERVICE_NAME, DaggerSingletonComponent.builder().rootModule(new RootModule(serviceTree)).build());
            // MortarScope.buildRootScope()
            //.withService(ObjectGraphService.SERVICE_NAME, ObjectGraph.create(new RootModule()))
            //.build("Root");
        }

        if(serviceTree.hasRootService(name)) {
            return serviceTree.getRootService(name);
        }
        if(TreeNodes.NODE_TAG.equals(name)) {
            return serviceTree.getTreeRoot();
        }
        return super.getSystemService(name);
    }
}
