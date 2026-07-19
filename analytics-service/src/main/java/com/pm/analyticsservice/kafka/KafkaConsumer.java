package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import org.slf4j.Logger;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);


    @KafkaListener(topics="patient", groupId = "analytics-service-debug-v1", properties = {"auto.offset.reset=earliest"})
    public void consumeEvent(byte[] event){
        log.info("In the consumer Event");
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            // ... perform any business logic related to analytics here
            log.info("Received Patient Event: [PatientId={},PatientName={},PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Error deserializing event{}", e.getMessage());
        }
    }
}
