/*
 * $Id: TokenInterceptor.java 394468 2006-04-16 12:16:03Z tmjee $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.jsf;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.struts.action2.ServletActionContext;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * Initializes the JSF context for this request
 */
public class FacesSetupInterceptor extends FacesSupport implements Interceptor {

	private static final long serialVersionUID = -621512342655103941L;

	private String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;

	private FacesContextFactory facesContextFactory;

	private Lifecycle lifecycle;

	/**
	 * Sets the lifecycle id
	 * 
	 * @param id
	 *            The id
	 */
	public void setLifecycleId(String id) {
		this.lifecycleId = id;
	}

	/**
	 * Initializes the lifecycle and factories
	 */
	public void init() {
		facesContextFactory = (FacesContextFactory) FactoryFinder
				.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		// Javadoc says: Lifecycle instance is shared across multiple
		// simultaneous requests, it must be implemented in a thread-safe
		// manner.
		// So we can acquire it here once:
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
				.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		lifecycle = lifecycleFactory.getLifecycle(lifecycleId);

		Application application = ((ApplicationFactory) FactoryFinder
				.getFactory(FactoryFinder.APPLICATION_FACTORY))
				.getApplication();
		if (!(application.getNavigationHandler() instanceof StrutsNavigationHandler)) {
			application.setNavigationHandler(new StrutsNavigationHandler());
		}
		if (!(application.getVariableResolver() instanceof StrutsVariableResolver)) {
			application.setVariableResolver(new StrutsVariableResolver(
					application.getVariableResolver()));
		}
	}

	/**
	 * Creates the faces context for other phases.
	 * 
	 * @param invocation
	 *            The action invocation
	 */
	public String intercept(ActionInvocation invocation) throws Exception {
		FacesContext facesContext = facesContextFactory.getFacesContext(
				ServletActionContext.getServletContext(), ServletActionContext
						.getRequest(), ServletActionContext.getResponse(),
				lifecycle);

		setLifecycle(lifecycle);

		try {
			return invocation.invoke();
		} finally {
			facesContext.release();
		}
	}

	/**
	 * Cleans up the lifecycle and factories
	 */
	public void destroy() {
		facesContextFactory = null;
		lifecycle = null;
	}
}
