package com.ps.patientservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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

        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDto(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with id: " + id));

        if(patientRepository.existsByEmail(patientRequestDTO.getEmail()) && !existingPatient.getEmail().equals(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }

        existingPatient.setName(patientRequestDTO.getName());
        existingPatient.setEmail(patientRequestDTO.getEmail());
        existingPatient.setAddress(patientRequestDTO.getAddress());
        existingPatient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(existingPatient);
        return PatientMapper.toDto(updatedPatient);
    }
}
