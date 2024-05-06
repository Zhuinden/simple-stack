package com.zhuinden.simplestack;

/**
 * {@link ServiceSearchResult} represents the service within a given node, associated with a given key.
 */
public class ServiceSearchResult {
    private final Backstack backstack;
    private final String scopeTag;
    private final String serviceTag;
    private final Object service;

    /**
     * Return the scope tag.
     *
     * @return the scope tag
     */
    public String getScopeTag() {
        return scopeTag;
    }

    /**
     * Return the service tag.
     *
     * @return the service tag
     */
    public String getServiceTag() {
        return serviceTag;
    }

    /**
     * Return the service.
     *
     * @return the service
     */
    public Object getService() {
        return service;
    }

    /**
     * Return the backstack.
     *
     * @return the backstack
     */
    public Backstack getBackstack() {
        return backstack;
    }

    public ServiceSearchResult(Backstack backstack, String scopeTag, String serviceTag, Object service) {
        this.backstack = backstack;
        this.scopeTag = scopeTag;
        this.serviceTag = serviceTag;
        this.service = service;
    }
}
