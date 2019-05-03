import ClayButton from '../shared/ClayButton.es';
import propTypes from 'prop-types';
import React from 'react';

class SelectEntityInput extends React.Component {
	static propTypes = {
		displayValue: propTypes.oneOfType(
			[
				propTypes.string,
				propTypes.number
			]
		),
		onChange: propTypes.func.isRequired,
		selectEntity: propTypes.shape(
			{
				id: propTypes.string,
				multiple: propTypes.bool,
				title: propTypes.string,
				uri: propTypes.string
			}
		),
		value: propTypes.oneOfType(
			[
				propTypes.string,
				propTypes.number
			]
		)
	};

	/**
	 * Opens a modal for selecting entities. Uses different methods for
	 * selecting multiple entities versus single because of the way the event
	 * and data is submitted.
	 */
	_handleSelectEntity = () => {
		const {
			onChange,
			selectEntity: {id, multiple, title, uri}
		} = this.props;

		if (multiple) {
			AUI().use(
				'liferay-item-selector-dialog',
				A => {
					const itemSelectorDialog = new A.LiferayItemSelectorDialog(
						{
							eventName: id,
							on: {
								selectedItemChange: event => {
									const newVal = event.newVal;

									if (newVal) {
										const selectedValues = event.newVal.map(
											item => ({
												displayValue: item.name,
												value: item.id
											})
										);

										onChange(selectedValues);
									}
								}
							},
							strings: {
								add: Liferay.Language.get('select'),
								cancel: Liferay.Language.get('cancel')
							},
							title,
							url: uri
						}
					);

					itemSelectorDialog.open();
				}
			);
		}
		else {
			Liferay.Util.selectEntity(
				{
					dialog: {
						constrain: true,
						destroyOnHide: true,
						modal: true
					},
					id,
					title,
					uri
				},
				event => {
					onChange(
						{
							displayValue: event.entityname,
							value: event.entityid
						}
					);
				}
			);
		}
	};

	render() {
		const {displayValue, value} = this.props;

		return (
			<div className="criterion-input input-group select-entity-input">
				<div className="input-group-item input-group-prepend">
					<input
						data-testid="entity-select-input"
						type="hidden"
						value={value}
					/>

					<input
						className="form-control"
						readOnly
						value={displayValue}
					/>
				</div>

				<span className="input-group-append input-group-item input-group-item-shrink">
					<ClayButton
						label={Liferay.Language.get('select')}
						onClick={this._handleSelectEntity}
					/>
				</span>
			</div>
		);
	}
}

export default SelectEntityInput;