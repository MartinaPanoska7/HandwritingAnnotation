package mk.ukim.finki.wp.handwritingannotation.service;

import mk.ukim.finki.wp.handwritingannotation.model.Document;
import mk.ukim.finki.wp.handwritingannotation.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public Document save(Document document) {
        return documentRepository.save(document);
    }

    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }
}