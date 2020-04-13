package org.wannagoframework.notification.receiver;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wannagoframework.dto.domain.notification.CloudDataMessage;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 13/04/2020
 */
@SpringBootTest
@ActiveProfiles({"local", "no-eureka"})
class CloudDataMessageReceiverTest {

  @Autowired
  private CloudDataMessageReceiver cloudDataMessageReceiver;

  @Test
  void onNewCloudDataMessage() {
    CloudDataMessage testMessage = new CloudDataMessage();
    testMessage.setDeviceToken("XXXX");
    testMessage.setData("{\"Nick\" : \"Mario\",\n"
        + "      \"body\" : \"great match!\"}");
    testMessage.setApplicationName("Test");
    cloudDataMessageReceiver.onNewCloudDataMessage(testMessage);
  }
}