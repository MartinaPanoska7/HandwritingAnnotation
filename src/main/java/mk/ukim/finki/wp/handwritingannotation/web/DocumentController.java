package mk.ukim.finki.wp.handwritingannotation.web;

import mk.ukim.finki.wp.handwritingannotation.model.Document;
import mk.ukim.finki.wp.handwritingannotation.service.AnnotationService;
import mk.ukim.finki.wp.handwritingannotation.service.DocumentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class DocumentController {

    private final DocumentService documentService;
    private final AnnotationService annotationService;

    public DocumentController(
            DocumentService documentService,
            AnnotationService annotationService) {

        this.documentService = documentService;
        this.annotationService = annotationService;
    }



    // HOME

    @GetMapping("/")
    public String home() {
        return "redirect:/documents";
    }


    // DOCUMENTS

    @GetMapping("/documents")
    public String getDocuments(Model model) {

        model.addAttribute(
                "documents",
                documentService.findAll()
        );

        return "documents";
    }



    // UPLOAD PAGE

    @GetMapping("/documents/upload")
    public String getUploadPage() {
        return "upload";
    }



    // UPLOAD MULTIPLE DOCUMENTS


    @PostMapping("/documents/upload")
    public String uploadDocuments(
            @RequestParam("files") MultipartFile[] files
    ) throws IOException {

        String uploadDir =
                System.getProperty("user.dir")
                        + File.separator
                        + "uploads";

        File directory =
                new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }


        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue;
            }


            String originalFilename =
                    file.getOriginalFilename();


            if (originalFilename == null) {
                continue;
            }


            /*
             * Get the original file extension.
             */
            String extension = "";

            int dotIndex =
                    originalFilename.lastIndexOf(".");


            if (dotIndex >= 0) {

                extension =
                        originalFilename.substring(
                                dotIndex
                        );
            }


            /*
             * Generate a unique physical filename.
             *
             * The original filename is still stored
             * in document.name and shown to the user.
             */
            String storedFilename =
                    UUID.randomUUID()
                            + extension;


            String filePath =
                    uploadDir
                            + File.separator
                            + storedFilename;


            File destinationFile =
                    new File(filePath);


            /*
             * Save the uploaded file
             * in the uploads directory.
             */
            file.transferTo(
                    destinationFile
            );


            /*
             * Save document information
             * in the database.
             */
            Document document =
                    new Document();


            document.setName(
                    originalFilename
            );


            document.setFilePath(
                    destinationFile.getAbsolutePath()
            );


            documentService.save(
                    document
            );
        }


        return "redirect:/documents";
    }



    // VIEW DOCUMENT


    @GetMapping("/documents/{id}/view")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id
    ) {

        Document document =
                documentService.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Document not found"
                                )
                        );


        /*
         * First try the file path stored
         * in the database.
         */
        File file =
                new File(
                        document.getFilePath()
                );


        /*
         * FALLBACK FOR OLD DOCUMENTS
         *
         * Older documents were saved using their
         * original filename instead of a UUID.
         *
         * If the stored path does not work,
         * search the uploads directory using
         * the original document name.
         */
        if (!file.exists()) {

            String uploadDir =
                    System.getProperty("user.dir")
                            + File.separator
                            + "uploads";


            File fallbackFile =
                    new File(
                            uploadDir
                                    + File.separator
                                    + document.getName()
                    );


            /*
             * If the old file is found,
             * repair the path stored in the database.
             */
            if (fallbackFile.exists()) {

                file =
                        fallbackFile;


                document.setFilePath(
                        fallbackFile.getAbsolutePath()
                );


                documentService.save(
                        document
                );
            }
        }


        /*
         * If neither the stored path nor
         * the fallback path exists,
         * the physical file is missing.
         */
        if (!file.exists()) {

            throw new RuntimeException(
                    "File not found: "
                            + document.getName()
            );
        }


        Resource resource =
                new FileSystemResource(
                        file
                );


        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline"
                )

                .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/pdf"
                )

                .body(resource);
    }



    // DELETE DOCUMENT


    @PostMapping("/documents/{id}/delete")
    public String deleteDocument(
            @PathVariable Long id
    ) {

        Document document =
                documentService.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Document not found"
                                )
                        );


        /*
         * First delete all annotations
         * associated with this document.
         */
        annotationService.deleteAllByDocumentId(
                id
        );


        /*
         * Delete the physical uploaded file.
         */
        File file =
                new File(
                        document.getFilePath()
                );


        /*
         * Fallback for older documents.
         */
        if (!file.exists()) {

            String uploadDir =
                    System.getProperty("user.dir")
                            + File.separator
                            + "uploads";


            File fallbackFile =
                    new File(
                            uploadDir
                                    + File.separator
                                    + document.getName()
                    );


            if (fallbackFile.exists()) {

                file =
                        fallbackFile;
            }
        }


        if (file.exists()) {

            file.delete();
        }


        /*
         * Finally delete the document
         * from the database.
         */
        documentService.deleteById(
                id
        );


        return "redirect:/documents";
    }



    // ANNOTATE DOCUMENT

    @GetMapping("/documents/{id}/annotate")
    public String annotateDocument(
            @PathVariable Long id,
            Model model
    ) {

        Document document =
                documentService.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Document not found"
                                )
                        );


        model.addAttribute(
                "document",
                document
        );


        return "annotate";
    }
}