<#assign percentage = backgroundTaskDisplay.getPercentage() />

<div class="background-task-status-in-progress">
	<div class="progress-group">
		<div class="progress">
			<div aria-valuemax="100" aria-valuemin="0" aria-valuenow="${percentage}" class="progress-bar" role="progressbar" style="width: ${percentage}%;"></div>
		</div>

		<div class="progress-group-addon">${percentage}%</div>
	</div>
</div>