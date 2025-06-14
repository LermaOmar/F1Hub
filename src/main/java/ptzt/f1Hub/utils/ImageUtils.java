package ptzt.f1Hub.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class ImageUtils {

    private static String uploadDir;

    public ImageUtils(@Value("${spring.uploadDir}") String uploadDir) {
        ImageUtils.uploadDir = uploadDir;
    }

    public static String parseBase64image(String base64) throws IOException {

        String extension = base64.substring(base64.indexOf("/") + 1, base64.indexOf(";"));
        String base64Data = base64.split(",")[1];
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        String filename = System.currentTimeMillis() + "." + extension;

        Path path = Path.of(uploadDir + "/"+filename);

        Files.write(path, decodedBytes);

        return String.format("https://f1hub-back.onrender.com/images/%s",filename);

    }
}
