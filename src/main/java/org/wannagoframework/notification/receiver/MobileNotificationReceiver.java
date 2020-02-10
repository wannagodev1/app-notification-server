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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.dto.messageQueue.MobileMessage;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class MobileNotificationReceiver implements HasLogger {

  @JmsListener(destination = "mobile")
  public void sendNotificationToMobile(Message<MobileMessage> message) {
    String loggerPrefix = getLoggerPrefix("sendNotificationToMobile");
    logger().info(loggerPrefix + "+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    MessageHeaders headers = message.getHeaders();
    logger().info(loggerPrefix + "Headers received : " + headers);

    MobileMessage response = message.getPayload();
    logger().info(loggerPrefix + "Response received : " + response);
    logger().info(loggerPrefix + "+++++++++++++++++++++++++++++++++++++++++++++++++++++");
  }
}
