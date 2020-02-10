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


package org.wannagoframework.notification.client;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.wannagoframework.commons.utils.HasLogger;

/**
 * @author WannaGo Dev1.
 * @version 1.0
 * @since 2019-07-10
 */
@Component
public class AtraitSmsProvider implements SmsProvider, HasLogger {

  @Value("${app.sms.atrait.login}")
  private String login;
  @Value("${app.sms.atrait.password}")
  private String password;
  @Value("${app.sms.atrait.url}")
  private String url;
  @Value("${app.sms.atrait.shortCode}")
  private String shortCode;

  @Override
  public SmsResultCodeEnum sendSms(String to, String message, String id) {
    String loggerPrefix = getLoggerPrefix("sendSms");
    ResponseEntity<String> response;
    try {
      Map<String, String> uriParams = new HashMap<>();
      uriParams.put("login", login);
      uriParams.put("password", password);
      uriParams.put("mobileNumber", to);
      uriParams.put("message", message);
      uriParams.put("id", id);
      uriParams.put("shortCode", shortCode);
      response = (new RestTemplate()).getForEntity(url, String.class, uriParams);
    } catch (HttpServerErrorException | HttpClientErrorException error) {
      HttpStatus httpStatus = error.getStatusCode();
      if (httpStatus.is4xxClientError()) {
        logger().warn(loggerPrefix + "Auth Login error .. ");
        return SmsResultCodeEnum.ERROR_4XX;
      } else if (httpStatus.is5xxServerError()) {
        logger().warn(loggerPrefix + "Something wrong .. " + error.getLocalizedMessage(), error);
        return SmsResultCodeEnum.ERROR_5XX;
      } else {
        logger().warn(loggerPrefix + "Something wrong .. " + error.getLocalizedMessage(), error);
        return SmsResultCodeEnum.ERROR_OTHER;
      }
    }
    int responseCode = Integer.parseInt(response.getBody());
    switch (responseCode) {
      case 0:
        return SmsResultCodeEnum.SENT;
      case 1:
        return SmsResultCodeEnum.MISSING_PARAMETER;
      case 2:
        return SmsResultCodeEnum.INCORRECT_ID;
      case 3:
        return SmsResultCodeEnum.INCORRECT_SHORT_CODE;
      case 4:
        return SmsResultCodeEnum.INSUFFISANT_CREDIT;
      default:
        return SmsResultCodeEnum.UNKNOWN;
    }
  }
}
