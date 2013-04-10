package org.glassfish.weld.tck;

import javax.enterprise.context.spi.Context;

import org.jboss.cdi.tck.spi.Contexts;
import org.jboss.weld.Container;
import org.jboss.weld.context.ApplicationContext;
import org.jboss.weld.context.DependentContext;
import org.jboss.weld.context.ManagedContext;
import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.http.HttpRequestContext;
import org.jboss.weld.util.ForwardingContext;

public class ContextsImpl implements Contexts<Context> {

    public RequestContext getRequestContext() {
        return Container.instance().deploymentManager().instance().select(HttpRequestContext.class).get();
    }

    public void setActive(Context context) {
        context = ForwardingContext.unwrap(context);
        if (context instanceof ManagedContext) {
            ((ManagedContext) context).activate();
        } else if (context instanceof ApplicationContext) {
            // No-op, always active
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void setInactive(Context context) {
        context = ForwardingContext.unwrap(context);
        if (context instanceof ManagedContext) {
            ((ManagedContext) context).deactivate();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public DependentContext getDependentContext() {
        return Container.instance().deploymentManager().instance().select(DependentContext.class).get();
    }

    public void destroyContext(Context context) {
        context = ForwardingContext.unwrap(context);
        if (context instanceof ManagedContext) {
            ManagedContext managedContext = (ManagedContext) context;
            managedContext.invalidate();
            managedContext.deactivate();
            managedContext.activate();
        } else if (context instanceof ApplicationContext) {
            ((ApplicationContext) context).invalidate();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
