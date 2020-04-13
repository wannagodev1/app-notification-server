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

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wannagoframework.commons.utils.HasLogger;
import org.wannagoframework.notification.domain.CloudNotificationMessageTemplate;
import org.wannagoframework.notification.repository.CloudNotificationMessageTemplateRepository;

@Service
@Transactional(readOnly = true)
public class CloudNotificationMessageTemplateServiceImpl implements
    CloudNotificationMessageTemplateService, HasLogger {

  private final CloudNotificationMessageTemplateRepository cloudNotificationMessageTemplateRepository;

  public CloudNotificationMessageTemplateServiceImpl(
      CloudNotificationMessageTemplateRepository cloudNotificationMessageTemplateRepository) {
    this.cloudNotificationMessageTemplateRepository = cloudNotificationMessageTemplateRepository;
  }

  @Override
  public CloudNotificationMessageTemplate getByCloudNotificationMessageAction(
      String cloudNotificationMessageAction) {
    return cloudNotificationMessageTemplateRepository
        .findByCloudNotificationMessageActionAndIsActiveIsTrue(cloudNotificationMessageAction)
        .orElse(null);
  }

  @Override
  public Page<CloudNotificationMessageTemplate> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<CloudNotificationMessageTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudNotificationMessageTemplateRepository.findAll(pageable);
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
      result = cloudNotificationMessageTemplateRepository.countByCriteria(filter);
    } else {
      result = cloudNotificationMessageTemplateRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  public long countByCloudNotificationMessageAction(String cloudNotificationMessageAction) {
    return cloudNotificationMessageTemplateRepository
        .countByCloudNotificationMessageActionAndIsActiveIsTrue(cloudNotificationMessageAction);
  }

  @Override
  public Optional<CloudNotificationMessageTemplate> findByCloudNotificationMessageAction(
      String cloudNotificationMessageAction, String iso3Language) {
    return cloudNotificationMessageTemplateRepository
        .findByCloudNotificationMessageActionAndIso3LanguageAndIsActiveIsTrue(
            cloudNotificationMessageAction, iso3Language);
  }

  @Override
  @Transactional
  public CloudNotificationMessageTemplate add(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    return cloudNotificationMessageTemplateRepository.save(cloudNotificationMessageTemplate);
  }

  @Override
  @Transactional
  public CloudNotificationMessageTemplate update(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    return add(cloudNotificationMessageTemplate);
  }

  @Override
  @Transactional
  public void delete(CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    if (cloudNotificationMessageTemplate.getIsActive()) {
      cloudNotificationMessageTemplate.setIsActive(false);
      cloudNotificationMessageTemplateRepository.save(cloudNotificationMessageTemplate);
    } else {
      cloudNotificationMessageTemplateRepository.delete(cloudNotificationMessageTemplate);
    }
  }

  @Override
  public MongoRepository<CloudNotificationMessageTemplate, String> getRepository() {
    return cloudNotificationMessageTemplateRepository;
  }
}
