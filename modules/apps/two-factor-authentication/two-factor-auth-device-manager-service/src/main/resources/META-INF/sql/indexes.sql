create index IX_C28AA5FE on Bemis_Device (portalUserId, deviceIP[$COLUMN_LENGTH:75$]);
create index IX_83DCE0BC on Bemis_Device (tempDevice, portalUserId, deviceIP[$COLUMN_LENGTH:75$]);
create index IX_951B1CFA on Bemis_Device (verified, portalUserId, deviceIP[$COLUMN_LENGTH:75$]);

create index IX_4A2B5580 on Bemis_DeviceCode (emailAddress[$COLUMN_LENGTH:75$]);
create index IX_1A70381A on Bemis_DeviceCode (portalUserId);