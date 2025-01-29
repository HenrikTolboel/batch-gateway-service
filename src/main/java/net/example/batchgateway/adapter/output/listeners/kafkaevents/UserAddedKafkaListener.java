package net.example.batchgateway.adapter.output.listeners.kafkaevents;

/*
@Component
@ConditionalOnProperty(name = "kafka.available", havingValue = "true", matchIfMissing = false)
class UserAddedKafkaListener implements ApplicationListener<UserAddedEvent> {

    private KafkaTemplate<String, String> template;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value(value = "${application-event.topic.name}")
    private String applicationEventTopicName;

    @Override
    public void onApplicationEvent(final UserAddedEvent event) {
        String jsonString = "empty";
        try {
            jsonString = (new ObjectMapper()).writeValueAsString(
                    new KafkaEvent(
                            Instant.ofEpochSecond(event.getTimestamp(), 0),
                            KafkaEvents.USER_ADDED,
                            event.getEventId().domainEventId(),
                            event.getUserId()
                            )
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        logger.info(jsonString);
        final ProducerRecord<String, String> record = new ProducerRecord<>(applicationEventTopicName, jsonString);
        var futureResult = template.send(record);


    }
}
*/
