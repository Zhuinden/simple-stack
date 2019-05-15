/*
 * Copyright 2019 Gabor Varadi
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
package com.zhuinden.simplestack;

import java.util.Set;

/**
 * Specifies the mode used for looking up services within the scopes.
 *
 * Allows customizing whether both implicit and explicit parents are used, or only explicit parents.
 */
public enum ScopeLookupMode {
    ALL {
        @Override
        protected boolean executeCanFindFromService(ScopeManager scopeManager, String scopeTag, String serviceTag) {
            return scopeManager.canFindFromScopeAll(scopeTag, serviceTag);
        }

        @Override
        protected <T> T executeLookupFromScope(ScopeManager scopeManager, String scopeTag, String serviceTag) {
            return scopeManager.lookupFromScopeAll(scopeTag, serviceTag);
        }

        @Override
        protected Set<String> executeFindScopesForKey(ScopeManager scopeManager, Object key) {
            return scopeManager.findScopesForKeyAll(key);
        }
    },
    EXPLICIT {
        @Override
        protected boolean executeCanFindFromService(ScopeManager scopeManager, String scopeTag, String serviceTag) {
            return scopeManager.canFindFromScopeExplicit(scopeTag, serviceTag);
        }

        @Override
        protected <T> T executeLookupFromScope(ScopeManager scopeManager, String scopeTag, String serviceTag) {
            return scopeManager.lookupFromScopeExplicit(scopeTag, serviceTag);
        }

        @Override
        protected Set<String> executeFindScopesForKey(ScopeManager scopeManager, Object key) {
            return scopeManager.findScopesForKeyExplicit(key);
        }
    };

    protected abstract boolean executeCanFindFromService(ScopeManager scopeManager, String scopeTag, String serviceTag);
    
    protected abstract <T> T executeLookupFromScope(ScopeManager scopeManager, String scopeTag, String serviceTag);

    protected abstract Set<String> executeFindScopesForKey(ScopeManager scopeManager, Object key);
}