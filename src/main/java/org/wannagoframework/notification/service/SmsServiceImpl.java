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

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.notification.client.SmsProvider;
import org.wannagoframework.notification.client.SmsResultCodeEnum;
import org.wannagoframework.notification.domain.Sms;
import org.wannagoframework.notification.domain.SmsStatusEnum;
import org.wannagoframework.notification.repository.SmsMessageRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-03-04
 */
@Service
@Transactional(readOnly = true)
public class SmsServiceImpl implements SmsService, HasLogger {

  private final SmsProvider smsProvider;

  private final SmsMessageRepository smsMessageRepository;

  private final SmsTemplateService smsTemplateService;

  public SmsServiceImpl(SmsProvider smsProvider,
      SmsMessageRepository smsMessageRepository,
      SmsTemplateService smsTemplateService) {
    this.smsProvider = smsProvider;
    this.smsMessageRepository = smsMessageRepository;
    this.smsTemplateService = smsTemplateService;
  }

  @Override
  public void sendSimpleSms(String to, String text) {
    smsProvider.sendSms(to, text, UUID.randomUUID().toString());
  }

  @Override
  public Page<Sms> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<Sms> result;

    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = smsMessageRepository.findAll(pageable);
    }

    logger().debug(loggerString + "Out = " + result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    String loggerString = getLoggerPrefix("countAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.countByCriteria(filter);
    } else {
      result = smsMessageRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  /**
   * Save and then send the given sms. <br/> The SMS is firstly saved. Then we try to send it. At
   * the end the SMS is updated (in case of status change)
   *
   * @param sms the email to be sent.
   */
  @Override
  public void saveAndSendSms(Sms sms) {
    smsMessageRepository.save(sms);
    sendSms(sms);
    smsMessageRepository.save(sms);
  }

  public void sendSms(Sms sms) {
    String loggerPrefix = getLoggerPrefix("sendSmsMessage");
    logger().debug(loggerPrefix + "Sending...");
    SmsResultCodeEnum status = smsProvider
        .sendSms(sms.getPhoneNumber(), sms.getBody(), sms.getId());
    if (status.equals(SmsResultCodeEnum.SENT)) {
      sms.setErrorMessage(null);
      sms.setSmsStatus(SmsStatusEnum.SENT);
    } else {
      if (sms.getNbRetry() >= 3) {
        sms.setErrorMessage(status.name());
        sms.setSmsStatus(SmsStatusEnum.ERROR);
      } else {
        sms.setErrorMessage(status.name());
        sms.setNbRetry(sms.getNbRetry() + 1);
      }
    }
  }

  @Override
  @Transactional
  public void processNotSentSms() {
    List<Sms> unsentEsmss = smsMessageRepository.findBySmsStatus(SmsStatusEnum.NOT_SENT);
    unsentEsmss.forEach(sms -> {
      sendSms(sms);
      smsMessageRepository.save(sms);
    });
  }

  @Override
  public MongoRepository<Sms, String> getRepository() {
    return smsMessageRepository;
  }
}
