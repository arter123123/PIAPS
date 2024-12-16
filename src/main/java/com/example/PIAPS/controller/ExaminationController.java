package com.example.PIAPS.controller;

import com.example.PIAPS.model.Examination;
import com.example.PIAPS.model.ProtocolTemplate;
import com.example.PIAPS.service.ExaminationService;
import com.itextpdf.io.exceptions.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/examinations")
public class ExaminationController {

    @Autowired
    private ExaminationService examinationService;

    @PostMapping
    public ResponseEntity<Examination> createExamination(@RequestBody Examination examination) {
        return ResponseEntity.ok(examinationService.createExamination(examination));
    }
    @PostMapping("/{id}/upload")
    public ResponseEntity<Examination> uploadExaminationFiles(@PathVariable Long id,
                                                              @RequestParam(value = "image", required = false) MultipartFile image,
                                                              @RequestParam(value = "video", required = false) MultipartFile video) {
        try {
            Examination updatedExamination = examinationService.uploadExaminationFiles(id, image, video);
            return ResponseEntity.ok(updatedExamination);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Examination> getExaminationById(@PathVariable Long id) {
        return examinationService.getExaminationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/search")
    public Page<Examination> searchExaminations(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return examinationService.searchExaminations(title, patientId, page, size);
    }

    @GetMapping
    public List<Examination> getAllExaminations() {
        return examinationService.getAllExaminations();
    }
    @GetMapping("/protocol/{id}")
    public ResponseEntity<byte[]> downloadProtocol(@PathVariable Long id) {
        try {
            Examination examination = examinationService.getExaminationById(id)
                    .orElseThrow(() -> new RuntimeException("Examination not found"));

            // Нужно передавать конкретный шаблон протокола
            ProtocolTemplate template = new ProtocolTemplate(); // Или найти из БД
            byte[] pdf = examinationService.generateProtocolPdf(examination, template);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"protocol_" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Examination> updateExamination(@PathVariable Long id, @RequestBody Examination examination) {
        return ResponseEntity.ok(examinationService.updateExamination(id, examination));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamination(@PathVariable Long id) {
        examinationService.deleteExamination(id);
        return ResponseEntity.noContent().build();
    }
}
