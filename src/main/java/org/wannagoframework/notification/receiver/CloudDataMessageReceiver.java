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
import org.wannagoframework.dto.domain.notification.CloudDataMessage;
import org.wannagoframework.dto.domain.notification.Mail;
import org.wannagoframework.notification.domain.CloudDataMessageStatusEnum;
import org.wannagoframework.notification.service.CloudDataMessageService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class CloudDataMessageReceiver implements HasLogger {

  private final CloudDataMessageService cloudDataMessageService;

  public CloudDataMessageReceiver(
      CloudDataMessageService cloudDataMessageService) {
    this.cloudDataMessageService = cloudDataMessageService;
  }

  /**
   * Automatically launched by JMS broker when new email is received on <strong>cloudData</strong>
   * channel.
   *
   * @param cloudDataMessage the received notification
   * @see Mail
   */
  @JmsListener(destination = "cloudData")
  public void onNewCloudDataMessage(final CloudDataMessage cloudDataMessage) {
    String loggerPrefix = getLoggerPrefix("onNewCloudDataMessage");

    logger().info(loggerPrefix + "Receiving a request from {} for sending cloud data {} to {} ",
        cloudDataMessage.getApplicationName(), cloudDataMessage.getData(),
        cloudDataMessage.getDeviceToken());
    CloudDataMessageStatusEnum result = cloudDataMessageService
        .sendCloudDataMessage(cloudDataMessage.getDeviceToken(),
            cloudDataMessage.getCloudDataMessageAction(),
            cloudDataMessage.getTopic(),
            cloudDataMessage.getData(), cloudDataMessage.getAttributes(),
            cloudDataMessage.getIso3Language());
    logger().info(loggerPrefix + "Cloud Data Message status {} ", result);
  }
}
