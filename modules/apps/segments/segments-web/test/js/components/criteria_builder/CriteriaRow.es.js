import CriteriaRow from 'components/criteria_builder/CriteriaRow.es';
import React from 'react';
import {cleanup, render} from 'react-testing-library';

const connectDnd = jest.fn(el => el);

describe(
	'CriteriaRow',
	() => {
		afterEach(cleanup);

		it(
			'should render',
			() => {
				const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

				const {asFragment} = render(
					<OriginalCriteriaRow
						connectDragPreview={connectDnd}
						connectDragSource={connectDnd}
						connectDropTarget={connectDnd}
						groupId="group_01"
						index={0}
						onAdd={jest.fn()}
						onChange={jest.fn()}
						onDelete={jest.fn()}
						onMove={jest.fn()}
						propertyKey="user"
					/>
				);

				expect(asFragment()).toMatchSnapshot();
			}
		);
	}
);