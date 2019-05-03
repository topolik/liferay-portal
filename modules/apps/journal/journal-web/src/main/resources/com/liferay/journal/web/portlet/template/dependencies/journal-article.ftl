<#include "init.ftl">

<#assign
	variableData = name + ".getData()"
	variableFriendlyUrl = name + ".getFriendlyUrl()"
/>

<#if repeatable>
	<#assign
		variableData = "cur_" + variableData
		variableFriendlyUrl = "cur_" + variableFriendlyUrl
	/>
</#if>

<a href="${getVariableReferenceCode(variableFriendlyUrl)}">
	${getVariableReferenceCode(variableData)}
</a>