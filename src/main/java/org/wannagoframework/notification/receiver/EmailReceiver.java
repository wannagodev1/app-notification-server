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
import org.wannagoframework.dto.domain.notification.Mail;
import org.wannagoframework.notification.domain.MailActionEnum;
import org.wannagoframework.notification.service.MailService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class EmailReceiver implements HasLogger {

  private final MailService mailService;
  private final OrikaBeanMapper mapperFacade;

  public EmailReceiver(MailService mailService,
      OrikaBeanMapper mapperFacade) {
    this.mailService = mailService;
    this.mapperFacade = mapperFacade;
  }

  /**
   * Automatically launched by JMS broker when new email is received on <strong>mailbox</strong>
   * channel.
   *
   * @param mail the received email
   * @see Mail
   */
  @JmsListener(destination = "mailbox")
  public void onNewMail(final Mail mail) {
    String loggerPrefix = getLoggerPrefix("onNewMail");
    logger().info(loggerPrefix + "Receiving a request from {} for sending email {} to {} ",
        mail.getApplicationName(), mail.getSubject(), mail.getTo());
    mailService
        .sendEmail(mail.getTo(), MailActionEnum.valueOf(mail.getMailAction().name()),
            mail.getAttributes(), mail.getAttachements(),
            mail.getIso3Language());
  }
}
