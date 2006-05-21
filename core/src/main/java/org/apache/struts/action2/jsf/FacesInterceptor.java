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

import javax.faces.context.FacesContext;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * Translates JSF phases into individual interceptors, and adapts their expected
 * workflow to Action 2
 */
public class FacesInterceptor extends FacesSupport implements Interceptor {

	private static final long serialVersionUID = -5418255964277566516L;

	/**
	 * Not used
	 */
	public void init() {
	}

	/**
	 * Adapts the phase workflow to Action 2
	 * 
	 * @param invocation
	 *            The action invocation
	 * @return The string result code
	 */
	public String intercept(ActionInvocation invocation) throws Exception {

		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getRenderResponse()) {
			return invocation.invoke();
		} else {
			String viewId = invocation.getProxy().getNamespace() + '/'
					+ invocation.getProxy().getActionName();
			executePhase(viewId, context);

			if (context.getResponseComplete()) {
				// Abort the chain as the result is done
				return null;
			} else {
				if (invocation.getResultCode() != null) {
					return invocation.getResultCode();
				} else {
					return invocation.invoke();
				}
			}
		}
	}

	/**
	 * Executes the specific phase. The phase id is constructed as a composite
	 * of the namespace and action name.
	 * 
	 * @param viewId
	 *            The view id
	 * @param facesContext
	 *            The current faces context
	 * @return True if the next phases should be skipped
	 */
	protected boolean executePhase(String viewId, FacesContext facesContext) {
		return false;
	}

	/**
	 * Not used
	 */
	public void destroy() {
	}

}