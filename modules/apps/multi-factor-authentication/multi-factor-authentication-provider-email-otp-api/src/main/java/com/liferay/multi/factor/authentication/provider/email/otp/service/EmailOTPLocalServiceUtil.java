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

package com.liferay.multi.factor.authentication.provider.email.otp.service;

import aQute.bnd.annotation.ProviderType;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for EmailOTP. This utility wraps
 * <code>com.liferay.multi.factor.authentication.provider.email.otp.service.impl.EmailOTPLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author arthurchan35
 * @see EmailOTPLocalService
 * @generated
 */
@ProviderType
public class EmailOTPLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.multi.factor.authentication.provider.email.otp.service.impl.EmailOTPLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the email otp to the database. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTP the email otp
	 * @return the email otp that was added
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP addEmailOTP(
				com.liferay.multi.factor.authentication.provider.email.otp.
					model.EmailOTP emailOTP) {

		return getService().addEmailOTP(emailOTP);
	}

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never reference this class directly. Always use {@link com.liferay.multi.factor.authentication.provider.otp.service.OTPLocalServiceUtil} to access the otp local service.
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP addEmailOTP(long userId, String email, String ip)
				throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addEmailOTP(userId, email, ip);
	}

	/**
	 * Creates a new email otp with the primary key. Does not add the email otp to the database.
	 *
	 * @param emailOTPId the primary key for the new email otp
	 * @return the new email otp
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP createEmailOTP(long emailOTPId) {

		return getService().createEmailOTP(emailOTPId);
	}

	/**
	 * Deletes the email otp from the database. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTP the email otp
	 * @return the email otp that was removed
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP deleteEmailOTP(
				com.liferay.multi.factor.authentication.provider.email.otp.
					model.EmailOTP emailOTP) {

		return getService().deleteEmailOTP(emailOTP);
	}

	/**
	 * Deletes the email otp with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp that was removed
	 * @throws PortalException if a email otp with the primary key could not be found
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP deleteEmailOTP(long emailOTPId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteEmailOTP(emailOTPId);
	}

	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP deleteEmailOTPByUserId(long userId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteEmailOTPByUserId(userId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.provider.email.otp.model.impl.EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.provider.email.otp.model.impl.EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP fetchEmailOTP(long emailOTPId) {

		return getService().fetchEmailOTP(emailOTPId);
	}

	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP fetchEmailOTPByUserId(long userId) {

		return getService().fetchEmailOTPByUserId(userId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	/**
	 * Returns the email otp with the primary key.
	 *
	 * @param emailOTPId the primary key of the email otp
	 * @return the email otp
	 * @throws PortalException if a email otp with the primary key could not be found
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP getEmailOTP(long emailOTPId)
				throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getEmailOTP(emailOTPId);
	}

	/**
	 * Returns a range of all the email otps.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent and pagination is required (<code>start</code> and <code>end</code> are not <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code>), then the query will include the default ORDER BY logic from <code>com.liferay.multi.factor.authentication.provider.email.otp.model.impl.EmailOTPModelImpl</code>. If both <code>orderByComparator</code> and pagination are absent, for performance reasons, the query will not have an ORDER BY clause and the returned result set will be sorted on by the primary key in an ascending order.
	 * </p>
	 *
	 * @param start the lower bound of the range of email otps
	 * @param end the upper bound of the range of email otps (not inclusive)
	 * @return the range of email otps
	 */
	public static java.util.List
		<com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP> getEmailOTPs(int start, int end) {

		return getService().getEmailOTPs(start, end);
	}

	/**
	 * Returns the number of email otps.
	 *
	 * @return the number of email otps
	 */
	public static int getEmailOTPsCount() {
		return getService().getEmailOTPsCount();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP updateAttempts(long userId, boolean success, String ip)
				throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateAttempts(userId, success, ip);
	}

	/**
	 * Updates the email otp in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param emailOTP the email otp
	 * @return the email otp that was updated
	 */
	public static
		com.liferay.multi.factor.authentication.provider.email.otp.model.
			EmailOTP updateEmailOTP(
				com.liferay.multi.factor.authentication.provider.email.otp.
					model.EmailOTP emailOTP) {

		return getService().updateEmailOTP(emailOTP);
	}

	public static EmailOTPLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<EmailOTPLocalService, EmailOTPLocalService>
		_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(EmailOTPLocalService.class);

		ServiceTracker<EmailOTPLocalService, EmailOTPLocalService>
			serviceTracker =
				new ServiceTracker<EmailOTPLocalService, EmailOTPLocalService>(
					bundle.getBundleContext(), EmailOTPLocalService.class,
					null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}