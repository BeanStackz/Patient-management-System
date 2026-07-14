package com.ps.patientservice.mapper;

import java.time.LocalDate;

import com.ps.patientservice.dto.PatientRequestDTO;
import com.ps.patientservice.dto.PatientResponseDTO;
import com.ps.patientservice.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toDto(Patient patient){
        PatientResponseDTO patientResponseDTO = new PatientResponseDTO();
        patientResponseDTO.setId(patient.getId() != null ? patient.getId().toString() : null);
        patientResponseDTO.setName(patient.getName());
        patientResponseDTO.setEmail(patient.getEmail());
        patientResponseDTO.setAddress(patient.getAddress());
        patientResponseDTO.setDateOfBirth(patient.getDateOfBirth() != null ? patient.getDateOfBirth().toString() : null);
        patientResponseDTO.setRegistrationDate(patient.getRegistrationDate() != null ? patient.getRegistrationDate().toString() : null);
        return patientResponseDTO;

    }


    public static Patient toModel(PatientRequestDTO patientRequestDTO){
        Patient patient = new Patient();
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(patientRequestDTO.getDateOfBirth() != null ? LocalDate.parse(patientRequestDTO.getDateOfBirth()) : null);
        patient.setRegistrationDate(patientRequestDTO.getRegistrationDate() != null ? LocalDate.parse(patientRequestDTO.getRegistrationDate()) : null);
        return patient;
    }
}
