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
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.dto.domain.notification.Sms;
import org.wannagoframework.notification.service.SmsService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class SmsReceiver implements HasLogger {

  private final SmsService smsService;
  private final OrikaBeanMapper mapperFacade;

  public SmsReceiver(SmsService smsService, OrikaBeanMapper mapperFacade) {
    this.smsService = smsService;
    this.mapperFacade = mapperFacade;
  }

  /**
   * Automatically launched by JMS broker when new sms is received on <strong>sms</strong> channel.
   *
   * @param sms the received email
   * @see Sms
   */
  @JmsListener(destination = "sms")
  public void onNewSms(final Sms sms) {
    String loggerPrefix = getLoggerPrefix("onNewSms");
    logger().info(loggerPrefix + "Receiving a request from {} for sending sms to {} ",
        sms.getApplicationName(), sms.getPhoneNumber());
    final org.wannagoframework.notification.domain.Sms smsBean = mapperFacade
        .map(sms, org.wannagoframework.notification.domain.Sms.class);
    smsService.saveAndSendSms(smsBean);
    logger().info(loggerPrefix + "Sms status {} ", smsBean.getSmsStatus());
  }
}
