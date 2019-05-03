import ClaySelect from 'components/shared/ClaySelect.es';
import propTypes from 'prop-types';
import React from 'react';
import {BOOLEAN_OPTIONS} from 'utils/constants.es';

class BooleanInput extends React.Component {
	static propTypes = {
		onChange: propTypes.func.isRequired,
		value: propTypes.string
	};

	_handleChange = event => {
		this.props.onChange({value: event.target.value});
	}

	render() {
		return (
			<ClaySelect
				className="criterion-input form-control"
				data-testid="options-boolean"
				onChange={this._handleChange}
				options={BOOLEAN_OPTIONS}
				selected={this.props.value}
			/>
		);
	}
}

export default BooleanInput;