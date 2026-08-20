package mk.ukim.finki.wp.handwritingannotation.repository;

import mk.ukim.finki.wp.handwritingannotation.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnotationRepository extends JpaRepository<Annotation, Long> {

    List<Annotation> findAllByDocumentId(Long documentId);

    void deleteAllByDocumentId(Long documentId);
}