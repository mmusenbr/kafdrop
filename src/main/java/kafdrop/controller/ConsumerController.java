package kafdrop.controller;

import io.swagger.annotations.*;
import kafdrop.model.*;
import kafdrop.service.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consumer")
public final class ConsumerController {
  private final KafkaMonitor kafkaMonitor;
  private final KafkaConsumerMonitor kafkaConsumerMonitor;

  public ConsumerController(KafkaMonitor kafkaMonitor, KafkaConsumerMonitor kafkaConsumerMonitor) {
    this.kafkaMonitor = kafkaMonitor;
    this.kafkaConsumerMonitor = kafkaConsumerMonitor;
  }

  @RequestMapping("/{groupId:.+}")
  public String consumerDetail(@PathVariable("groupId") String groupId, Model model) throws ConsumerNotFoundException {
    final var topicVos = kafkaMonitor.getTopics();
    final var consumer = kafkaConsumerMonitor.getConsumers(topicVos)
        .stream()
        .filter(c -> c.getGroupId().equals(groupId))
        .findAny();
    model.addAttribute("consumer", consumer.orElseThrow(() -> new ConsumerNotFoundException(groupId)));
    return "consumer-detail";
  }

  @ApiOperation(value = "getConsumer", notes = "Get topic and partition details for a consumer group")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success", response = ConsumerVO.class),
      @ApiResponse(code = 404, message = "Invalid consumer group")
  })
  @RequestMapping(path = "/{groupId:.+}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  public @ResponseBody ConsumerVO getConsumer(@PathVariable("groupId") String groupId) throws ConsumerNotFoundException {
    final var topicVos = kafkaMonitor.getTopics();
    final var consumer = kafkaConsumerMonitor.getConsumers(topicVos)
        .stream()
        .filter(c -> c.getGroupId().equals(groupId))
        .findAny();
    return consumer.orElseThrow(() -> new ConsumerNotFoundException(groupId));
  }
}