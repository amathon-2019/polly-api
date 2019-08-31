package com.example.polly.PollyDemo;

import com.example.polly.PollyDemo.service.PollyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/polly")
public class PollyController {

    @GetMapping(value = "/message")
    public ResponseEntity<Object> getTestMessage(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getTestMessage params [----[ : country = {} , text = {}", country, text);

        PollyService pollyService = new PollyService();

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime+"_polly_sound.mp3";
        String outputFile = "/Users/bsc/Desktop/workspace/aws-polly-test/src/main/resources/static/"+fileName;

//        FileOutputStream fileOutputStream = null;
        try {
            InputStream msg = pollyService.getMp3(text, country);
            String contentType = pollyService.getContentType(text, country);

            Files.copy(msg, Paths.get(outputFile));
            File file = new File(outputFile);

//
//            File convertFile = new File(outputFile);
//            convertFile.createNewFile();
//            fileOutputStream = new FileOutputStream(convertFile);
//
//            int read = 0;
//            byte[] msgByte = new byte[msg.available()];
//            while ((read = msg.read(msgByte)) != -1) {
//                fileOutputStream.write(msgByte, 0, read);
//            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            ResponseEntity<Object> responseEntity = ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(contentType)).body(resource);
            return responseEntity;

        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            if (fileOutputStream != null) {
//                fileOutputStream.close();
//            }
//        }
        return null;

//        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
//
//        ResponseEntity<Object> responseEntity = ResponseEntity.ok().contentLength(file.length()).contentType(MediaType.parseMediaType(contentType)).body(resource);
//        return responseEntity;
    }

    @GetMapping(value = "/message2")
    public byte[] getTestMessage2(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getTestMessage2 params [----[ : country = {} , text = {}", country, text);

        PollyService pollyService = new PollyService();

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime+"_polly_sound.mp3";
        String outputFile = "/Users/bsc/Desktop/workspace/aws-polly-test/src/main/resources/static/"+fileName;

        try {
            InputStream msg = pollyService.getMp3(text, country);
            String contentType = pollyService.getContentType(text, country);
            byte[] buff = inputStreamToByteArray(msg);

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            fileOutputStream.write(buff);
            fileOutputStream.close();

            return buff;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping(value = "/play")
    public String getPlay(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getPlay params [----[ : country = {} , text = {}", country, text);

        PollyService pollyService = new PollyService();

        try {
            pollyService.play(country, text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return "Success!";
        }
    }

    public byte[] inputStreamToByteArray(InputStream inStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] buffer = new byte[8192];
        byte[] buffer = new byte[inStream.available()];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
