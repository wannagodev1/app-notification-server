/*
 * This file is part of the WannaGo distribution (https://github.com/wannago).
 * Copyright (c) [2019] - [2020].
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


package org.wannagoframework.notification.service;


import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wannagoframework.notification.domain.CloudNotificationMessage;
import org.wannagoframework.notification.domain.CloudNotificationMessageStatusEnum;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-03-04
 */
public interface CloudNotificationMessageService extends BaseCrudService<CloudNotificationMessage> {

  Page<CloudNotificationMessage> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  void sendSimpleCloudNotificationMessage(String deviceToken, String title, String body,
      String data);

  CloudNotificationMessageStatusEnum sendCloudNotificationMessage(String deviceToken,
      String cloudNotificationMessageAction, String title, String body, String data,
      Map<String, String> attributes, String iso3Language);

  void processNotSentCloudNotificationMessages();
}
