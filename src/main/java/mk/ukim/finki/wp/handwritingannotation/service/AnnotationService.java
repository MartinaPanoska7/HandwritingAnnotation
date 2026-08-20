package mk.ukim.finki.wp.handwritingannotation.service;

import mk.ukim.finki.wp.handwritingannotation.model.Annotation;
import mk.ukim.finki.wp.handwritingannotation.repository.AnnotationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AnnotationService {

    private final AnnotationRepository annotationRepository;

    public AnnotationService(AnnotationRepository annotationRepository) {
        this.annotationRepository = annotationRepository;
    }

    public List<Annotation> findAll() {
        return annotationRepository.findAll();
    }

    public Optional<Annotation> findById(Long id) {
        return annotationRepository.findById(id);
    }

    public List<Annotation> findAllByDocumentId(Long documentId) {
        return annotationRepository.findAllByDocumentId(documentId);
    }

    public Annotation save(Annotation annotation) {
        return annotationRepository.save(annotation);
    }

    public void deleteById(Long id) {
        annotationRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByDocumentId(Long documentId) {
        annotationRepository.deleteAllByDocumentId(documentId);
    }
}