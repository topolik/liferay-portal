import dateFns from 'date-fns';
import propTypes from 'prop-types';
import React from 'react';
import {PROPERTY_TYPES} from 'utils/constants.es';

const INPUT_DATE_FORMAT = 'YYYY-MM-DD';

class DateTimeInput extends React.Component {
	static propTypes = {
		onChange: propTypes.func.isRequired,
		value: propTypes.string
	};

	_handleDateChange = event => {
		const value = event.target.value ||
			dateFns.format(new Date(), INPUT_DATE_FORMAT);

		const iSOStringValue = dateFns
			.parse(value, INPUT_DATE_FORMAT)
			.toISOString();

		this.props.onChange(
			{
				type: PROPERTY_TYPES.DATE,
				value: iSOStringValue
			},
		);
	}

	render() {
		const date = new Date(this.props.value);

		const domStringDate = dateFns.format(date, INPUT_DATE_FORMAT);

		return (
			<div className="criterion-input date-input">
				<input
					className="form-control"
					data-testid="date-input"
					onChange={this._handleDateChange}
					type="date"
					value={domStringDate}
				/>
			</div>
		);
	}
}

export default DateTimeInput;