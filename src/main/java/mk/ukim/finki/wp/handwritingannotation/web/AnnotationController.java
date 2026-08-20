package mk.ukim.finki.wp.handwritingannotation.web;

import mk.ukim.finki.wp.handwritingannotation.model.Annotation;
import mk.ukim.finki.wp.handwritingannotation.model.Document;
import mk.ukim.finki.wp.handwritingannotation.service.AnnotationService;
import mk.ukim.finki.wp.handwritingannotation.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnnotationController {

    private final AnnotationService annotationService;
    private final DocumentService documentService;

    public AnnotationController(
            AnnotationService annotationService,
            DocumentService documentService) {

        this.annotationService = annotationService;
        this.documentService = documentService;
    }



    // GET ALL ANNOTATIONS FOR DOCUMENT

    @GetMapping("/documents/{documentId}/annotations")
    public List<Annotation> getAnnotations(
            @PathVariable Long documentId) {

        return annotationService.findAllByDocumentId(
                documentId
        );
    }



    // CREATE ANNOTATION

    @PostMapping("/documents/{documentId}/annotations")
    public Annotation createAnnotation(
            @PathVariable Long documentId,
            @RequestParam String letter,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double width,
            @RequestParam double height,
            @RequestParam int pageNumber) {

        Document document =
                documentService.findById(documentId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Document not found"
                                )
                        );

        Annotation annotation =
                new Annotation();

        annotation.setLetter(letter);
        annotation.setX(x);
        annotation.setY(y);
        annotation.setWidth(width);
        annotation.setHeight(height);
        annotation.setPageNumber(pageNumber);
        annotation.setDocument(document);

        return annotationService.save(
                annotation
        );
    }



    // UPDATE ANNOTATION

    @PostMapping("/annotations/{id}/update")
    public Annotation updateAnnotation(
            @PathVariable Long id,
            @RequestParam String letter,
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam double width,
            @RequestParam double height,
            @RequestParam int pageNumber) {

        Annotation annotation =
                annotationService.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Annotation not found"
                                )
                        );

        annotation.setLetter(letter);
        annotation.setX(x);
        annotation.setY(y);
        annotation.setWidth(width);
        annotation.setHeight(height);
        annotation.setPageNumber(pageNumber);

        return annotationService.save(
                annotation
        );
    }



    // DELETE ANNOTATION

    @PostMapping("/annotations/{id}/delete")
    public ResponseEntity<Void> deleteAnnotation(
            @PathVariable Long id) {

        Annotation annotation =
                annotationService.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Annotation not found"
                                )
                        );

        annotationService.deleteById(
                annotation.getId()
        );

        return ResponseEntity.ok().build();
    }
}