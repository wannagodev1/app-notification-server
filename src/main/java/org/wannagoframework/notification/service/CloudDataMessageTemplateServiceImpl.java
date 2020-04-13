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
import org.wannagoframework.notification.domain.CloudDataMessageTemplate;
import org.wannagoframework.notification.repository.CloudDataMessageTemplateRepository;

@Service
@Transactional(readOnly = true)
public class CloudDataMessageTemplateServiceImpl implements CloudDataMessageTemplateService,
    HasLogger {

  private final CloudDataMessageTemplateRepository cloudDataMessageTemplateRepository;

  public CloudDataMessageTemplateServiceImpl(
      CloudDataMessageTemplateRepository cloudDataMessageTemplateRepository) {
    this.cloudDataMessageTemplateRepository = cloudDataMessageTemplateRepository;
  }

  @Override
  public CloudDataMessageTemplate getByCloudDataMessageAction(String cloudDataMessageAction) {
    return cloudDataMessageTemplateRepository
        .findByCloudDataMessageActionAndIsActiveIsTrue(cloudDataMessageAction).orElse(null);
  }

  @Override
  public Page<CloudDataMessageTemplate> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<CloudDataMessageTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudDataMessageTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudDataMessageTemplateRepository.findAll(pageable);
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
      result = cloudDataMessageTemplateRepository.countByCriteria(filter);
    } else {
      result = cloudDataMessageTemplateRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  public long countByCloudDataMessageAction(String cloudDataMessageAction) {
    return cloudDataMessageTemplateRepository
        .countByCloudDataMessageActionAndIsActiveIsTrue(cloudDataMessageAction);
  }

  @Override
  public Optional<CloudDataMessageTemplate> findByCloudDataMessageAction(
      String cloudDataMessageAction, String iso3Language) {
    return cloudDataMessageTemplateRepository
        .findByCloudDataMessageActionAndIso3LanguageAndIsActiveIsTrue(cloudDataMessageAction,
            iso3Language);
  }

  @Override
  @Transactional
  public CloudDataMessageTemplate add(CloudDataMessageTemplate cloudDataMessageTemplate) {
    return cloudDataMessageTemplateRepository.save(cloudDataMessageTemplate);
  }

  @Override
  @Transactional
  public CloudDataMessageTemplate update(CloudDataMessageTemplate cloudDataMessageTemplate) {
    return add(cloudDataMessageTemplate);
  }

  @Override
  @Transactional
  public void delete(CloudDataMessageTemplate cloudDataMessageTemplate) {
    if (cloudDataMessageTemplate.getIsActive()) {
      cloudDataMessageTemplate.setIsActive(false);
      cloudDataMessageTemplateRepository.save(cloudDataMessageTemplate);
    } else {
      cloudDataMessageTemplateRepository.delete(cloudDataMessageTemplate);
    }
  }

  @Override
  public MongoRepository<CloudDataMessageTemplate, String> getRepository() {
    return cloudDataMessageTemplateRepository;
  }
}
