// package com.pjds.backend_generator.controller;

// import org.eclipse.jgit.api.Git;
// import org.eclipse.jgit.api.errors.GitAPIException;
// import org.apache.commons.io.FileUtils;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.pjds.backend_generator.model.ProjectRequest;

// import java.io.File;
// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.util.Collection;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api")
// public class BackendGeneratorController {

//     // GET endpoint for testing
//     @GetMapping("/generate-backend")
//     public ResponseEntity<Map<String, Object>> testEndpoint() {
//         Map<String, Object> resp = new HashMap<>();
//         resp.put("message", "Backend generator API is running!");
//         resp.put("status", "active");
//         resp.put("endpoint", "POST /api/generate-backend to generate project");
//         return ResponseEntity.ok(resp);
//     }

//     // POST endpoint for actual backend generation
//     @PostMapping("/generate-backend")
//     public ResponseEntity<Map<String, Object>> generateBackend(@RequestBody ProjectRequest req) {
//         Map<String, Object> resp = new HashMap<>();
//         try {
//             // 1. Select template repo by architecture
//             String repoUrl = getRepoForArchitecture(req.getArchitecture());
//             Path tempDir = Files.createTempDirectory("backend-gen");
            
//             System.out.println("Cloning template from: " + repoUrl);
//             System.out.println("To temporary directory: " + tempDir.toString());

//             // 2. Clone the template repo
//             try (Git git = Git.cloneRepository()
//                     .setURI(repoUrl)
//                     .setDirectory(tempDir.toFile())
//                     .call()) {
//                 System.out.println("Successfully cloned repository");
//             }

//             // 3. Replace all placeholders
//             replacePlaceholders(tempDir.toString(), req);

//             // 4. Copy generated project to user savePath
//             String destination = req.getSavePath() + File.separator + req.getProjectName();
//             File destinationDir = new File(destination);
//             FileUtils.copyDirectory(tempDir.toFile(), destinationDir);

//             // 5. Clean up temporary directory
//             FileUtils.deleteDirectory(tempDir.toFile());

//             resp.put("success", true);
//             resp.put("message", "Backend project generated successfully!");
//             resp.put("generatedPath", destination);
//             resp.put("projectName", req.getProjectName());
//             return ResponseEntity.ok(resp);
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             resp.put("success", false);
//             resp.put("message", "Failed to generate backend: " + e.getMessage());
//             return ResponseEntity.badRequest().body(resp);
//         }
//     }

//     private String getRepoForArchitecture(String arch) {
//         return switch (arch.toLowerCase()) {
//             case "monolith" -> "https://github.com/spring-projects/spring-petclinic.git";
//             case "microservice" -> "https://github.com/microservices-patterns/ftgo-application.git";
//             case "event-driven" -> "https://github.com/event-driven-example/template.git";
//             default -> throw new RuntimeException("Unknown architecture: " + arch);
//         };
//     }

//     private void replacePlaceholders(String dir, ProjectRequest req) throws IOException {
//         Collection<File> files = FileUtils.listFiles(
//                 new File(dir),
//                 new String[]{"java", "xml", "yml", "properties", "yaml", "json", "md", "txt", "gradle", "kt"},
//                 true
//         );
        
//         System.out.println("Found " + files.size() + " files to process");
        
//         for (File file : files) {
//             try {
//                 String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//                 String originalContent = content;
                
//                 content = content
//                         .replace("${group}", safe(req.getGroup()))
//                         .replace("${artifact}", safe(req.getArtifact()))
//                         .replace("${projectName}", safe(req.getProjectName()))
//                         .replace("${packageName}", safe(req.getPackageName()))
//                         .replace("${dbName}", safe(req.getDbName()))
//                         .replace("${dbUser}", safe(req.getDbUser()))
//                         .replace("${dbPassword}", safe(req.getDbPassword()))
//                         .replace("${name}", safe(req.getName()))
//                         .replace("${architecture}", safe(req.getArchitecture()));
                
//                 if (!content.equals(originalContent)) {
//                     FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
//                     System.out.println("Updated placeholders in: " + file.getAbsolutePath());
//                 }
//             } catch (Exception e) {
//                 System.err.println("Error processing file: " + file.getAbsolutePath() + " - " + e.getMessage());
//             }
//         }
//     }

//     private String safe(String s) {
//         return s == null ? "" : s;
//     }
// }



package com.pjds.backend_generator.controller;

import org.eclipse.jgit.api.Git;
import org.apache.commons.io.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pjds.backend_generator.model.ProjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BackendGeneratorController {

    // GET endpoint for testing
    @GetMapping("/generate-backend")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Backend generator API is running!");
        resp.put("status", "active");
        resp.put("endpoint", "POST /api/generate-backend to generate project");
        return ResponseEntity.ok(resp);
    }

    // POST endpoint for actual backend generation
    @PostMapping("/generate-backend")
    public ResponseEntity<Map<String, Object>> generateBackend(@RequestBody ProjectRequest req) {
        Map<String, Object> resp = new HashMap<>();
        try {
            // 1. Select template repo by architecture
            String repoUrl = getRepoForArchitecture(req.getArchitecture());

            // 2. Create temporary directory for cloning
            Path tempDir = Files.createTempDirectory("backend-gen");
            System.out.println("Cloning template from: " + repoUrl);
            System.out.println("To temporary directory: " + tempDir.toString());

            // 3. Clone the template repo
            try (Git git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(tempDir.toFile())
                    .call()) {
                System.out.println("Successfully cloned repository");
            }

            // 4. Replace placeholders in cloned project
            replacePlaceholders(tempDir.toString(), req);

            // 5. Determine safe destination path
            // String savePath = req.getSavePath();
            // if (savePath == null || savePath.isEmpty()) {
            //     savePath = System.getProperty("java.io.tmpdir"); // Use /tmp on Linux
            // }
            // Path destinationPath = Paths.get(savePath, req.getProjectName());
            // File destinationDir = destinationPath.toFile();

            String savePath;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Running locally on Windows → use client-provided path or default
                savePath = req.getSavePath();
                if (savePath == null || savePath.isEmpty()) {
                    savePath = System.getProperty("user.home") + File.separator + "Test Project Backend";
                }
            } else {
                // Running on Linux (Render) → ignore client path, use /tmp
                savePath = "/tmp/generated-projects";
            }
            Path destinationPath = Paths.get(savePath, req.getProjectName());
            Files.createDirectories(destinationPath); // ensure folder exists
            File destinationDir = destinationPath.toFile();

            // 6. Copy generated project to destination
            FileUtils.copyDirectory(tempDir.toFile(), destinationDir);

            // 7. Clean up temporary directory
            FileUtils.deleteDirectory(tempDir.toFile());

            // 8. Return response
            resp.put("success", true);
            resp.put("message", "Backend project generated successfully!");
            resp.put("projectName", req.getProjectName());
            resp.put("generatedPath", destinationPath.toString());

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.put("success", false);
            resp.put("message", "Failed to generate backend: " + e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // Helper to choose template repo
    private String getRepoForArchitecture(String arch) {
        return switch (arch.toLowerCase()) {
            case "monolith" -> "https://github.com/spring-projects/spring-petclinic.git";
            case "microservice" -> "https://github.com/microservices-patterns/ftgo-application.git";
            case "event-driven" -> "https://github.com/event-driven-example/template.git";
            default -> throw new RuntimeException("Unknown architecture: " + arch);
        };
    }

    // Helper to replace placeholders in project files
    private void replacePlaceholders(String dir, ProjectRequest req) throws IOException {
        Collection<File> files = FileUtils.listFiles(
                new File(dir),
                new String[]{"java", "xml", "yml", "properties", "yaml", "json", "md", "txt", "gradle", "kt"},
                true
        );

        System.out.println("Found " + files.size() + " files to process");

        for (File file : files) {
            try {
                String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                String originalContent = content;

                content = content
                        .replace("${group}", safe(req.getGroup()))
                        .replace("${artifact}", safe(req.getArtifact()))
                        .replace("${projectName}", safe(req.getProjectName()))
                        .replace("${packageName}", safe(req.getPackageName()))
                        .replace("${dbName}", safe(req.getDbName()))
                        .replace("${dbUser}", safe(req.getDbUser()))
                        .replace("${dbPassword}", safe(req.getDbPassword()))
                        .replace("${name}", safe(req.getName()))
                        .replace("${architecture}", safe(req.getArchitecture()));

                if (!content.equals(originalContent)) {
                    FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
                    System.out.println("Updated placeholders in: " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Error processing file: " + file.getAbsolutePath() + " - " + e.getMessage());
            }
        }
    }

    // Safe string helper
    private String safe(String s) {
        return s == null ? "" : s;
    }
}
