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

package org.wannagoframework.notification.endpoint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wannagoframework.commons.endpoint.BaseEndpoint;
import org.wannagoframework.commons.utils.OrikaBeanMapper;
import org.wannagoframework.dto.domain.notification.CloudDataMessage;
import org.wannagoframework.dto.serviceQuery.ServiceResult;
import org.wannagoframework.dto.serviceQuery.generic.CountAnyMatchingQuery;
import org.wannagoframework.dto.serviceQuery.generic.DeleteByStrIdQuery;
import org.wannagoframework.dto.serviceQuery.generic.FindAnyMatchingQuery;
import org.wannagoframework.dto.serviceQuery.generic.GetByStrIdQuery;
import org.wannagoframework.notification.service.CloudDataMessageService;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-06-05
 */

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/cloudDataMessageService")
public class CloudDataMessageServiceEndpoint extends BaseEndpoint {

  private final CloudDataMessageService cloudDataMessageService;

  public CloudDataMessageServiceEndpoint(CloudDataMessageService cloudDataMessageService,
      OrikaBeanMapper mapperFacade) {
    super(mapperFacade);
    this.cloudDataMessageService = cloudDataMessageService;
  }

  @PostMapping(value = "/findAnyMatching")
  public ResponseEntity<ServiceResult> findAnyMatching(@RequestBody FindAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("findAnyMatching");
    try {
      Page<org.wannagoframework.notification.domain.CloudDataMessage> result = cloudDataMessageService
          .findAnyMatching(query.getFilter(),
              mapperFacade.map(query.getPageable(),
                  Pageable.class, getOrikaContext(query)));
      org.wannagoframework.dto.utils.Page<CloudDataMessage> convertedResult = new org.wannagoframework.dto.utils.Page<>();
      mapperFacade.map(result, convertedResult, getOrikaContext(query));
      return handleResult(loggerPrefix, convertedResult);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/countAnyMatching")
  public ResponseEntity<ServiceResult> countAnyMatching(@RequestBody CountAnyMatchingQuery query) {
    String loggerPrefix = getLoggerPrefix("countAnyMatching");
    try {
      return handleResult(loggerPrefix, cloudDataMessageService
          .countAnyMatching(query.getFilter()));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/getById")
  public ResponseEntity<ServiceResult> getById(@RequestBody GetByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("getById");
    try {
      return handleResult(loggerPrefix, mapperFacade.map(cloudDataMessageService
          .load(query.getId()), CloudDataMessage.class, getOrikaContext(query)));
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }

  @PostMapping(value = "/delete")
  public ResponseEntity<ServiceResult> delete(@RequestBody DeleteByStrIdQuery query) {
    String loggerPrefix = getLoggerPrefix("delete");
    try {
      cloudDataMessageService.delete(query.getId());
      return handleResult(loggerPrefix);
    } catch (Throwable t) {
      return handleResult(loggerPrefix, t);
    }
  }
}