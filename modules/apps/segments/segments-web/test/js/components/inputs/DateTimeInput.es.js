import DateTimeInput from 'components/inputs/DateTimeInput.es';
import React from 'react';
import {cleanup, render} from 'react-testing-library';
import {testControlledInput} from 'test/utils';

const DATE_INPUT_TESTID = 'date-input';

describe(
	'DateTimeInput',
	() => {
		afterEach(cleanup);

		it(
			'should render type date',
			() => {
				const mockOnChange = jest.fn();

				const defaultNumberValue = '2019-01-23';

				const {asFragment, getByTestId} = render(
					<DateTimeInput
						onChange={mockOnChange}
						value={defaultNumberValue}
					/>
				);

				expect(asFragment()).toMatchSnapshot();

				const element = getByTestId(DATE_INPUT_TESTID);

				testControlledInput(
					{
						element,
						mockFunc: mockOnChange,
						newValue: '2019-01-24',
						newValueExpected: '2019-01-24T00:00:00.000Z',
						value: defaultNumberValue
					}
				);
			}
		);
	}
);