package com.example.PIAPS.service;

import com.example.PIAPS.model.Examination;

import java.io.ByteArrayOutputStream;
import com.example.PIAPS.model.ProtocolTemplate;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import com.example.PIAPS.repository.ExaminationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExaminationService {

    @Autowired
    private ExaminationRepository examinationRepository;

    public Examination createExamination(Examination examination) {
        examination.setCreatedAt(LocalDateTime.now());
        return examinationRepository.save(examination);
    }

    public Page<Examination> searchExaminations(String title, Long patientId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (title != null) {
            return examinationRepository.findByTitleContaining(title, pageRequest);
        } else if (patientId != null) {
            return examinationRepository.findByPatientId(patientId, pageRequest);
        } else {
            return examinationRepository.findAll(pageRequest);
        }
    }
    public Examination uploadExaminationFiles(Long id, MultipartFile image, MultipartFile video) throws IOException {
        Examination examination = examinationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examination not found"));

        if (image != null && !image.isEmpty()) {
            try {
                examination.setImage(image.getBytes());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Error saving image file", e);
            }
        }

        if (video != null && !video.isEmpty()) {
            try {
                examination.setVideo(video.getBytes());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Error saving video file", e);
            }
        }

        return examinationRepository.save(examination);
    }

    public byte[] generateProtocolPdf(Examination examination, ProtocolTemplate template) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Protocol: " + examination.getTitle()));
        document.add(new Paragraph("Patient: " + examination.getPatient().getFirstName() + " " + examination.getPatient().getLastName()));
        document.add(new Paragraph("Template: " + template.getName()));
        document.add(new Paragraph("Content: " + template.getTemplateContent()));

        // Добавление изображения (если есть)
        if (examination.getImage() != null) {
            try {
                Image img = new Image(ImageDataFactory.create(examination.getImage()));
                document.add(img);
            } catch (Exception e) {
                document.add(new Paragraph("Error displaying image."));
            }
        }

        // Добавление видео (если есть)
        if (examination.getVideo() != null) {
            document.add(new Paragraph("Video: [VIDEO FILE]"));
        }

        document.close();
        return outputStream.toByteArray();
    }
    public Optional<Examination> getExaminationById(Long id) {
        return examinationRepository.findById(id);
    }

    public List<Examination> getAllExaminations() {
        return examinationRepository.findAll();
    }

    public Examination updateExamination(Long id, Examination updatedExamination) {
        return examinationRepository.findById(id).map(examination -> {
            examination.setTitle(updatedExamination.getTitle());
            examination.setParameters(updatedExamination.getParameters());
            examination.setProtocol(updatedExamination.getProtocol());
            examination.setConclusion(updatedExamination.getConclusion());
            examination.setUpdatedAt(LocalDateTime.now());
            return examinationRepository.save(examination);
        }).orElseThrow(() -> new RuntimeException("Examination not found"));
    }

    public void deleteExamination(Long id) {
        examinationRepository.deleteById(id);
    }
}
