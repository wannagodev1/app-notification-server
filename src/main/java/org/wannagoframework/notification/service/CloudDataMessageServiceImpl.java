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

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.notification.client.CloudDataMessageProvider;
import org.wannagoframework.notification.domain.CloudDataMessage;
import org.wannagoframework.notification.domain.CloudDataMessageStatusEnum;
import org.wannagoframework.notification.domain.CloudDataMessageTemplate;
import org.wannagoframework.notification.repository.CloudDataMessageRepository;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-03-04
 */
@Service
@Transactional(readOnly = true)
public class CloudDataMessageServiceImpl implements CloudDataMessageService, HasLogger {

  private final CloudDataMessageProvider cloudDataMessageProvider;

  private final CloudDataMessageRepository cloudDataMessageRepository;

  private final CloudDataMessageTemplateService cloudDataMessageTemplateService;

  public CloudDataMessageServiceImpl(CloudDataMessageProvider cloudDataMessageProvider,
      CloudDataMessageRepository cloudDataMessageRepository,
      CloudDataMessageTemplateService cloudDataMessageTemplateService) {
    this.cloudDataMessageProvider = cloudDataMessageProvider;
    this.cloudDataMessageRepository = cloudDataMessageRepository;
    this.cloudDataMessageTemplateService = cloudDataMessageTemplateService;
  }

  @Override
  public void sendSimpleCloudDataMessage(String deviceToken, String data) {
    cloudDataMessageProvider.sendCloudDataMessage(deviceToken, data, UUID.randomUUID().toString());
  }

  @Override
  public Page<CloudDataMessage> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<CloudDataMessage> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudDataMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudDataMessageRepository.findAll(pageable);
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
      result = cloudDataMessageRepository.countByCriteria(filter);
    } else {
      result = cloudDataMessageRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  @Transactional
  public CloudDataMessageStatusEnum sendCloudDataMessage(String deviceToken,
      String cloudDataMessageAction, String data, Map<String, String> attributes,
      String iso3Language) {
    String loggerPrefix = getLoggerPrefix("sendCloudDataMessage");

    Optional<CloudDataMessageTemplate> _cloudDataMessageTemplate = cloudDataMessageTemplateService
        .findByCloudDataMessageAction(cloudDataMessageAction, iso3Language);
    CloudDataMessageTemplate template = null;
    if (_cloudDataMessageTemplate.isPresent()) {
      template = _cloudDataMessageTemplate.get();
      logger().trace(loggerPrefix + "Template found = {}", template);
    }

    if (StringUtils.isNotBlank(deviceToken)) {
      return sendAndSave(deviceToken, data, template, attributes);
    } else {
      logger().warn(loggerPrefix + "No cloudDataMessage to send.");
    }
    return null;
  }

  private CloudDataMessageStatusEnum sendAndSave(String deviceToken, String data,
      CloudDataMessageTemplate cloudDataMessageTemplate, Map<String, String> attributes) {
    String loggerPrefix = getLoggerPrefix("sendAndSave");
    logger().trace(loggerPrefix + "Template = {}, attributes = {}", cloudDataMessageTemplate,
        attributes);
    CloudDataMessage cloudDataMessage = new CloudDataMessage();
    cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.NOT_SENT);
    try {
      logger().debug(loggerPrefix + "Building the message...");

      String _data = null;
      if (StringUtils.isNotBlank(data)) {
        _data = data;
      } else if (cloudDataMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudDataMessageTemplate.getData())) {
          Template dataTemplate = new Template(
              null,
              cloudDataMessageTemplate.getData(),
              new Configuration(Configuration.VERSION_2_3_28)
          );

          data = FreeMarkerTemplateUtils.processTemplateIntoString(dataTemplate, attributes);
        }
      }
      // initialize saved cloudDataMessage data
      cloudDataMessage.setDeviceToken(deviceToken);
      cloudDataMessage.setData(_data);

      sendCloudDataMessage(cloudDataMessage);
    } catch (Exception e) {
      logger().error(loggerPrefix + "Error while preparing mail = {}, message = {}",
          cloudDataMessage.getCloudDataMessageAction(), e.getMessage());
      cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.ERROR);
    } finally {
      logger().debug(loggerPrefix + "Email sent status = {}",
          cloudDataMessage.getCloudDataMessageStatus());
    }
    cloudDataMessage = cloudDataMessageRepository.save(cloudDataMessage);
    return cloudDataMessage.getCloudDataMessageStatus();
  }

  private CloudDataMessageStatusEnum sendCloudDataMessage(CloudDataMessage cloudDataMessage) {
    String loggerPrefix = getLoggerPrefix("sendCloudDataMessage");
    logger().debug(
        loggerPrefix + "Sending '" + cloudDataMessage.getData() + "' to " + cloudDataMessage
            .getDeviceToken());
    String status = cloudDataMessageProvider
        .sendCloudDataMessage(cloudDataMessage.getDeviceToken(), cloudDataMessage.getData(),
            cloudDataMessage.getId());
    logger().debug(loggerPrefix + "Result = " + status);

    /*
    if (status.equals(CloudDataMessageResultCodeEnum.SENT)) {
      cloudDataMessage.setErrorMessage(null);
      cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.SENT);
    } else {
      if (cloudDataMessage.getNbRetry() >= 3) {
        cloudDataMessage.setErrorMessage(status.name());
        cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.ERROR);
      } else {
        cloudDataMessage.setErrorMessage(status.name());
        cloudDataMessage.setNbRetry(cloudDataMessage.getNbRetry() + 1);
        cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.RETRYING);
      }
    }
     */
    cloudDataMessage = cloudDataMessageRepository.save(cloudDataMessage);
    return cloudDataMessage.getCloudDataMessageStatus();
  }

  @Override
  @Transactional
  public void processNotSentCloudDataMessages() {
    List<CloudDataMessage> unsentEcloudDataMessages = cloudDataMessageRepository
        .findByCloudDataMessageStatusIn(CloudDataMessageStatusEnum.NOT_SENT,
            CloudDataMessageStatusEnum.RETRYING);
    unsentEcloudDataMessages.forEach(cloudDataMessage -> {
      sendCloudDataMessage(cloudDataMessage);
      cloudDataMessageRepository.save(cloudDataMessage);
    });
  }

  @Override
  public MongoRepository<CloudDataMessage, String> getRepository() {
    return cloudDataMessageRepository;
  }
}
