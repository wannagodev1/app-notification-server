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


package org.wannagoframework.notification.receiver;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.dto.domain.notification.CloudNotificationMessage;
import org.wannagoframework.dto.domain.notification.Mail;
import org.wannagoframework.notification.domain.CloudNotificationMessageStatusEnum;
import org.wannagoframework.notification.service.CloudNotificationMessageService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class CloudNotificationMessageReceiver implements HasLogger {

  private final CloudNotificationMessageService cloudNotificationMessageService;

  public CloudNotificationMessageReceiver(
      CloudNotificationMessageService cloudNotificationMessageService) {
    this.cloudNotificationMessageService = cloudNotificationMessageService;
  }

  /**
   * Automatically launched by JMS broker when new email is received on
   * <strong>cloudNotification</strong> channel.
   *
   * @param cloudNotificationMessage the received notification
   * @see Mail
   */
  @JmsListener(destination = "cloudNotification")
  public void onNewCloudNotificationMessage(
      final CloudNotificationMessage cloudNotificationMessage) {
    String loggerPrefix = getLoggerPrefix("onNewCloudNotificationMessage");

    logger()
        .info(loggerPrefix + "Receiving a request from {} for sending cloud notification {} to {} ",
            cloudNotificationMessage.getApplicationName(), cloudNotificationMessage.getTitle(),
            cloudNotificationMessage.getDeviceToken());
    CloudNotificationMessageStatusEnum result = cloudNotificationMessageService
        .sendCloudNotificationMessage(cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getCloudNotificationMessageAction(),
            cloudNotificationMessage.getTitle(), cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(), cloudNotificationMessage.getAttributes(),
            cloudNotificationMessage.getIso3Language());
    logger().info(loggerPrefix + "Cloud Notification Message status {} ", result);
  }
}
