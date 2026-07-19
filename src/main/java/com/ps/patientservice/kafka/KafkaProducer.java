package com.ps.patientservice.kafka;

import com.ps.patientservice.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;


@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
//
//    public void sendEvent(Patient patient){
//        PatientEvent event = PatientEvent.newBuilder()
//                .setPatientId(patient.getId().toString())
//                .setName(patient.getName())
//                .setEmail(patient.getEmail())
//                .setEventType("PATIENT_CREATED")
//                .build();
//
//        try{
//            kafkaTemplate.send("patient", event.toByteArray());
//        }
//        catch (Exception e){
//            log.error("Error sending PatientCreated event: {}", event);
//        }
//    }

    public void sendEvent(Patient patient){
        log.info("--> ATTEMPTING to send Kafka event for Patient ID: {}", patient.getId());

        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        // The callback forces hidden network failures to print to your logs
        kafkaTemplate.send("patient", event.toByteArray())
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("--> SUCCESS! Event sent to partition: {} with offset: {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("--> FATAL KAFKA ERROR: Could not send event for Patient ID: {}. Reason: {}",
                                patient.getId(), ex.getMessage(), ex);
                    }
                });
    }
}
