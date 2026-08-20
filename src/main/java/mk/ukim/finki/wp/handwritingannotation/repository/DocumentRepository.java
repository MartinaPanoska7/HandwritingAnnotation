package mk.ukim.finki.wp.handwritingannotation.repository;

import mk.ukim.finki.wp.handwritingannotation.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}