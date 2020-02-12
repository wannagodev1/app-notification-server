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

package org.wannagoframework.notification.converter;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.notification.domain.Mail;
import org.wannagoframework.notification.domain.MailTemplate;
import org.wannagoframework.notification.domain.Sms;
import org.wannagoframework.notification.domain.SmsTemplate;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */
@Component
public class NotificationsConverter {

  private final OrikaBeanMapper orikaBeanMapper;

  public NotificationsConverter(OrikaBeanMapper orikaBeanMapper) {
    this.orikaBeanMapper = orikaBeanMapper;
  }

  @Bean
  public void notificationsConverters() {
    orikaBeanMapper
        .addMapper(Mail.class, org.wannagoframework.dto.domain.notification.Mail.class);
    orikaBeanMapper
        .addMapper(org.wannagoframework.dto.domain.notification.Mail.class, Mail.class);

    orikaBeanMapper
        .addMapper(Sms.class, org.wannagoframework.dto.domain.notification.Sms.class);
    orikaBeanMapper
        .addMapper(org.wannagoframework.dto.domain.notification.Sms.class, Sms.class);

    orikaBeanMapper
        .addMapper(MailTemplate.class,
            org.wannagoframework.dto.domain.notification.MailTemplate.class);
    orikaBeanMapper
        .addMapper(org.wannagoframework.dto.domain.notification.MailTemplate.class,
            MailTemplate.class);

    orikaBeanMapper
        .addMapper(SmsTemplate.class,
            org.wannagoframework.dto.domain.notification.SmsTemplate.class);
    orikaBeanMapper
        .addMapper(org.wannagoframework.dto.domain.notification.SmsTemplate.class,
            SmsTemplate.class);
  }
}