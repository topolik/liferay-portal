<%@ include file="/init.jsp" %>

<liferay-ui:success key="keyStored" message="Key stored successfully." />
<liferay-ui:success key="keyDeleted" message="Key deleted successfully." />
<liferay-ui:error key="missingFields" message="Please fill in all fields." />
<liferay-ui:error key="providerNotFound" message="Provider not found." />

<div class="container-fluid-1280">
	<h1>Key Manager</h1>

	<c:forEach items="${availableProviders}" var="provider">
		<div class="card mb-4">
			<div class="card-header">
				<h3 class="card-title">${provider.displayName} (${provider.providerId})</h3>
				<c:if test="${!provider.available}">
					<span class="label label-danger">Unavailable</span>
				</c:if>
			</div>

			<div class="card-body">
				<c:if test="${provider.available}">
					<liferay-ui:search-container
						emptyResultsMessage="No keys found in this provider."
						total="${provider.listAliases().size()}"
					>
						<liferay-ui:search-container-results
							results="${provider.listAliases()}"
						/>

						<liferay-ui:search-container-row
							className="java.lang.String"
							modelVar="alias"
						>
							<liferay-ui:search-container-column-text
								name="Alias"
								value="${alias}"
							/>

							<liferay-ui:search-container-column-text
								name="Reference"
								value="\${keyref:${provider.providerId}/${alias}}"
							/>

							<liferay-ui:search-container-column-text name="Action">
								<portlet:actionURL name="deleteKey" var="deleteKeyURL">
									<portlet:param name="providerId" value="${provider.providerId}" />
									<portlet:param name="alias" value="${alias}" />
								</portlet:actionURL>

								<liferay-ui:icon-delete url="${deleteKeyURL}" />
							</liferay-ui:search-container-column-text>
						</liferay-ui:search-container-row>

						<liferay-ui:search-iterator />
					</liferay-ui:search-container>

					<hr />

					<h4>Add New Key</h4>

					<portlet:actionURL name="storeKey" var="storeKeyURL" />

					<aui:form action="${storeKeyURL}" name="fm">
						<aui:input name="providerId" type="hidden" value="${provider.providerId}" />

						<div class="row">
							<div class="col-md-4">
								<aui:input label="Alias" name="alias" type="text" />
							</div>
							<div class="col-md-6">
								<aui:input label="Secret Value" name="value" type="password" />
							</div>
							<div class="col-md-2" style="margin-top: 30px;">
								<aui:button type="submit" value="Store Key" />
							</div>
						</div>
					</aui:form>
				</c:if>
			</div>
		</div>
	</c:forEach>
</div>
