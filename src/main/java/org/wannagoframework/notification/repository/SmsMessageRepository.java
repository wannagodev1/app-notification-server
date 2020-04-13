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

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.wannagoframework.notification.domain.Sms;
import org.wannagoframework.notification.domain.SmsStatusEnum;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-02-22
 */
public interface SmsMessageRepository extends MongoRepository<Sms, String> {

  @Query("{'$and' : [{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<Sms> findByCriteriaAndIsActive(String criteria, Boolean isShowInactive, Pageable pageable);

  @Query("{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}")
  Page<Sms> findByCriteria(String criteria, Pageable pageable);

  @Query(value = "{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}", count = true)
  long countByCriteria(String criteria);

  List<Sms> findBySmsStatusIn(SmsStatusEnum... smsStatusEnums);
}
