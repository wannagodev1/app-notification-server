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


package org.wannagoframework.notification.config.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.wannagoframework.notification.domain.Mail;
import org.wannagoframework.notification.domain.Sms;

@Component
@ChangeLog
public class InitialValuesChangeLog {

  @ChangeSet(order = "001", id = "createTransactionalCollections", author = "Wanna Go Dev1")
  public void createTransactionalCollections(MongoTemplate mongoTemplate) {
    mongoTemplate.createCollection(Sms.class);
    mongoTemplate.createCollection(Mail.class);

  }
}
