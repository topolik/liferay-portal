<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

						<div aria-orientation="vertical" class="panel-group" id="globalAccordion" role="tablist">

			<%
				for (Map.Entry<AuthorizationModel, AssignScopesModel.Relations> scopeAliasAuthorizationModelEntry : assignScopesModel.getGlobalAuthorizationModelsRelations().entrySet()) {
					String scopeAlias = scopeAliasAuthorizationModelEntry.getValue().getScopeAliases().stream().findAny().get();
			%>

							<div class="panel panel-secondary" id="<%= HtmlUtil.escapeAttribute(scopeAlias) %>">
								<div class="collapse-icon collapsed panel-header panel-header-link" id="globalAccordionHeading<%= scopeAlias %>">
									<div class="custom-checkbox custom-control" style="display: inline-block;">
										<label>
											<input onChange="<portlet:namespace />recalculateDependants(this)" value="<%= scopeAlias %>" <%= assignedScopes.contains(scopeAlias) ? "checked " : "" %>class="custom-control-input" name="<portlet:namespace />scope" type="checkbox">
											<span class="custom-control-label">
												<span class="custom-control-label-text panel-title"><%= HtmlUtil.escape(scopeAlias) %></span>
											</span>
										</label>
									</div>

									<a aria-controls="globalAccordion<%= scopeAlias %>" aria-expanded="false" class="" data-parent="#globalAccordion" data-toggle="collapse" href="#globalAccordion<%= scopeAlias %>" role="tab">
										<span>(details)</span>
										<span class="collapse-icon-closed">
											<svg aria-hidden="true" class="lexicon-icon lexicon-icon-angle-right">
												<use xlink:href="https://clayui.com/vendor/lexicon/icons.svg#angle-right" />
											</svg>
										</span>
										<span class="collapse-icon-open">
											<svg aria-hidden="true" class="lexicon-icon lexicon-icon-angle-down">
												<use xlink:href="https://clayui.com/vendor/lexicon/icons.svg#angle-down" />
											</svg>
										</span>
									</a>
								</div>

								<div aria-labelledby="globalAccordionHeading<%= scopeAlias %>" class="collapse panel-collapse" id="globalAccordion<%= scopeAlias %>" role="tabpanel">

			<%
					AuthorizationModel authorizationModel = scopeAliasAuthorizationModelEntry.getKey();

					for (String appName : authorizationModel.getApplicationNames()) {
					%>

									<ul>
										<li>
										<%= HtmlUtil.escape(assignScopesModel.getApplicationDescription(appName)) %><br />
											<font size="1">(

										<%
										String applicationScopeDescription = authorizationModel.getApplicationScopeDescription(appName).stream().collect(Collectors.joining(", "));
										%>

										<%= HtmlUtil.escape(applicationScopeDescription) %>

											)</font>
										</li>
									</ul>

					<%
					}
					%>

								</div>
							</div>

					<%
				}
			%>

						</div>