import propTypes from 'prop-types';
import React from 'react';
import {jsDatetoYYYYMMDD} from '../../utils/utils.es';
import {PROPERTY_TYPES} from 'utils/constants.es';

class DateInput extends React.Component {
	static propTypes = {
		onChange: propTypes.func.isRequired,
		value: propTypes.string
	};

	_handleDateChange = event => {
		const value = event.target.value ||
			jsDatetoYYYYMMDD((new Date()));

		this.props.onChange(
			{
				type: PROPERTY_TYPES.DATE,
				value
			}
		);
	}

	render() {
		const date = new Date(this.props.value);

		const domStringDate = jsDatetoYYYYMMDD(date);

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

export default DateInput;