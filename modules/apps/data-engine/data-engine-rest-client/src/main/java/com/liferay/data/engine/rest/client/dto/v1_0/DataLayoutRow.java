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

package com.liferay.data.engine.rest.client.dto.v1_0;

import com.liferay.data.engine.rest.client.function.UnsafeSupplier;
import com.liferay.data.engine.rest.client.serdes.v1_0.DataLayoutRowSerDes;

import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataLayoutRow {

	public DataLayoutColumn[] getDataLayoutColums() {
		return dataLayoutColums;
	}

	public void setDataLayoutColums(DataLayoutColumn[] dataLayoutColums) {
		this.dataLayoutColums = dataLayoutColums;
	}

	public void setDataLayoutColums(
		UnsafeSupplier<DataLayoutColumn[], Exception>
			dataLayoutColumsUnsafeSupplier) {

		try {
			dataLayoutColums = dataLayoutColumsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DataLayoutColumn[] dataLayoutColums;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataLayoutRow)) {
			return false;
		}

		DataLayoutRow dataLayoutRow = (DataLayoutRow)object;

		return Objects.equals(toString(), dataLayoutRow.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DataLayoutRowSerDes.toJSON(this);
	}

}