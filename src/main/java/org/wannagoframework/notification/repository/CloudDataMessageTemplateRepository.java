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


package org.wannagoframework.notification.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.wannagoframework.notification.domain.CloudDataMessageTemplate;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-02-22
 */
public interface CloudDataMessageTemplateRepository extends
    MongoRepository<CloudDataMessageTemplate, String> {

  @Query("{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<CloudDataMessageTemplate> findByCriteriaAndIsActive(String criteria, Boolean isShowInactive,
      Pageable pageable);

  @Query(value = "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}")
  Page<CloudDataMessageTemplate> findByCriteria(String criteria, Pageable pageable);

  Page<CloudDataMessageTemplate> findByIsActive(Boolean isActive, Pageable page);

  @Query(value = "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}", count = true)
  Long countByCriteriaAndIsActive(String criteria, Boolean isActive);

  @Query(value = "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}", count = true)
  Long countByCriteria(String criteria);

  long countByIsActive(Boolean isActive);

  long countByCloudDataMessageActionAndIsActiveIsTrue(String cloudDataMessageAction);

  Optional<CloudDataMessageTemplate> findByCloudDataMessageActionAndIso3LanguageAndIsActiveIsTrue(
      String cloudDataMessageAction, String iso3Language);

  Optional<CloudDataMessageTemplate> findByCloudDataMessageActionAndIsActiveIsTrue(
      String cloudDataMessageAction);
}
