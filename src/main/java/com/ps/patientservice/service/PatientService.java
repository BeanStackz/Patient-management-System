package com.ps.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ps.patientservice.grpc.BillingServiceGrpcClient;
import com.ps.patientservice.kafka.KafkaProducer;
import org.springframework.stereotype.Service;

import com.ps.patientservice.dto.PatientRequestDTO;
import com.ps.patientservice.dto.PatientResponseDTO;
import com.ps.patientservice.exception.EmailAlreadyExistsException;
import com.ps.patientservice.exception.PatientNotFoundException;
import com.ps.patientservice.mapper.PatientMapper;
import com.ps.patientservice.model.Patient;
import com.ps.patientservice.repository.PatientRepository;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;


    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient,
                          KafkaProducer kafkaProducer
                          ) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        // System.out.println("Patients: " + patients); // Debugging statement
        
        return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        try {
            System.out.println("--> Calling gRPC billing service...");
            billingServiceGrpcClient.createBillingAccount(
                    newPatient.getId().toString(), newPatient.getName(), newPatient.getEmail()
            );
            System.out.println("--> gRPC billing service SUCCESS!");
        } catch (Exception e) {
            System.err.println("--> gRPC billing service FAILED: " + e.getClass().getName());
            e.printStackTrace();
        }

        try {
            System.out.println("--> Sending Kafka Event...");
            kafkaProducer.sendEvent(newPatient);
            System.out.println("--> Kafka Event SUCCESS!");
        } catch (Exception e) {
            System.err.println("--> Kafka FAILED: " + e.getClass().getName());
            e.printStackTrace();
        }

        return PatientMapper.toDto(newPatient);
    }
    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }

        existingPatient.setName(patientRequestDTO.getName());
        existingPatient.setEmail(patientRequestDTO.getEmail());
        existingPatient.setAddress(patientRequestDTO.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(existingPatient);
        return PatientMapper.toDto(updatedPatient);
    }

    
    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
