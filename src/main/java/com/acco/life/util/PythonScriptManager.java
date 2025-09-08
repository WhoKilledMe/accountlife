package com.acco.life.util;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class PythonScriptManager {

    private String pythonExecutable = "python3";
    private String cachedScriptPath;

    @PostConstruct
    public void preloadScript() {
        try {
            String resourcePath = "/tools/mail_download.py";
            try (InputStream in = PythonScriptManager.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    // Fallback to project path during dev
                    Path devPath = Path.of("src/main/resources/tools/mail_download.py");
                    if (Files.exists(devPath)) {
                        cachedScriptPath = devPath.toAbsolutePath().toString();
                        return;
                    }
                    throw new IllegalStateException("Script resource not found: " + resourcePath);
                }
                Path temp = Files.createTempFile("mail_download", ".py");
                try (FileOutputStream out = new FileOutputStream(temp.toFile())) {
                    in.transferTo(out);
                }
                File f = temp.toFile();
                f.setExecutable(true);
                this.cachedScriptPath = f.getAbsolutePath();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to preload python script", e);
        }
    }

    public String getPythonExecutable() {
        return pythonExecutable;
    }

    public String getScriptPath() {
        return cachedScriptPath;
    }
}


