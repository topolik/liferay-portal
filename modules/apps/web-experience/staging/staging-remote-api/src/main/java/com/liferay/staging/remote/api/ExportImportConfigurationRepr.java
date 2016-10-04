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

package com.liferay.staging.remote.api;

import com.liferay.exportimport.kernel.model.ExportImportConfiguration;

import java.io.Serializable;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class ExportImportConfigurationRepr implements Serializable {

	public static ExportImportConfigurationRepr toSoapModel(
		ExportImportConfiguration model) {

		ExportImportConfigurationRepr soapModel =
			new ExportImportConfigurationRepr();

		soapModel.setMvccVersion(model.getMvccVersion());
		soapModel.setExportImportConfigurationId(
			model.getExportImportConfigurationId());
		soapModel.setGroupId(model.getGroupId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setName(model.getName());
		soapModel.setDescription(model.getDescription());
		soapModel.setType(model.getType());
		soapModel.setSettings(model.getSettings());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());

		return soapModel;
	}

	public ExportImportConfigurationRepr() {
	}

	public long getCompanyId() {
		return _companyId;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public String getDescription() {
		return _description;
	}

	public long getExportImportConfigurationId() {
		return _exportImportConfigurationId;
	}

	public long getGroupId() {
		return _groupId;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public long getMvccVersion() {
		return _mvccVersion;
	}

	public String getName() {
		return _name;
	}

	public long getPrimaryKey() {
		return _exportImportConfigurationId;
	}

	public String getSettings() {
		return _settings;
	}

	public int getStatus() {
		return _status;
	}

	public long getStatusByUserId() {
		return _statusByUserId;
	}

	public String getStatusByUserName() {
		return _statusByUserName;
	}

	public Date getStatusDate() {
		return _statusDate;
	}

	public int getType() {
		return _type;
	}

	public long getUserId() {
		return _userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		_exportImportConfigurationId = exportImportConfigurationId;
	}

	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public void setMvccVersion(long mvccVersion) {
		_mvccVersion = mvccVersion;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setPrimaryKey(long pk) {
		setExportImportConfigurationId(pk);
	}

	public void setSettings(String settings) {
		_settings = settings;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;
	}

	public void setType(int type) {
		_type = type;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	private long _companyId;
	private Date _createDate;
	private String _description;
	private long _exportImportConfigurationId;
	private long _groupId;
	private Date _modifiedDate;
	private long _mvccVersion;
	private String _name;
	private String _settings;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
	private int _type;
	private long _userId;
	private String _userName;

}