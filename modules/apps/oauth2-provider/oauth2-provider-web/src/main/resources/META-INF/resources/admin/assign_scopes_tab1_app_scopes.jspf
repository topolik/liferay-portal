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

											<ul class="list-group">

					<%
					Map<AuthorizationModel, AssignScopesModel.Relations> applicationAuthorizationModelsAssignModel = assignScopesModel.buildAssignmentModelForApplication(applicationName);

					for (Map.Entry<AuthorizationModel, AssignScopesModel.Relations> scopeAliasAuthorizationModelEntry : applicationAuthorizationModelsAssignModel.entrySet()) {
						String scopeAlias = scopeAliasAuthorizationModelEntry.getValue().getScopeAliases().stream().collect(Collectors.joining(","));

						Set<String> masterScopeAliases = scopeAliasAuthorizationModelEntry.getValue().getMasterAuthorizationModelsScopeAliases();
						AuthorizationModel authorizationModel = scopeAliasAuthorizationModelEntry.getKey();

						String masterScopeAliasesStr = String.join(" ", masterScopeAliases);
						%>

											<li class="list-group-item list-group-item-flex">
												<div class="autofit-col">
													<div class="custom-checkbox custom-control">
														<label>
															<input aria-labelledby="label<%= HtmlUtil.escape(masterScopeAliasesStr) %>" class="custom-control-input"<%= assignedScopes.contains(scopeAlias) ? " checked " : "" %>data-slave="<%= masterScopeAliasesStr %>"<% if (scopeAlias.length() > 0 ) { %> name="<portlet:namespace/>scope" value="<%= HtmlUtil.escapeAttribute(scopeAlias) %>" <% } %>type="checkbox">
															<span class="custom-control-label"></span>
														</label>
													</div>
												</div>

												<div class="autofit-col autofit-col-expand">
													<h4 class="list-group-title text-truncate">

						<%
						String applicationScopeDescription = authorizationModel.getApplicationScopeDescription(applicationName).stream().collect(Collectors.joining(", "));
						%>

													<%= HtmlUtil.escape(applicationScopeDescription) %>

													</h4>

													<p class="list-group-subtitle text-truncate"><%= HtmlUtil.escapeAttribute(scopeAlias) %></p>

						<%
						if (masterScopeAliasesStr.length() > 0) {
							if (scopeAlias.length() > 0) {%>
													<span><a href ="#1" data-slave="<%= masterScopeAliasesStr %>">CAN be assigned via a global scope</a></span>
							<%} else {%>
													<span><a href ="#1" data-slave="<%= masterScopeAliasesStr %>">MUST be assigned via a global scope</a></span>
							<%}
						} %>
												</div>
											</li>

						<%
					}
					%>

											</ul>