package com.example.polly.PollyDemo.controller;

import com.amazonaws.regions.Regions;
import com.example.polly.PollyDemo.model.ResponseFileView;
import com.example.polly.PollyDemo.service.PollyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private PollyService pollyService;

    @GetMapping(value = "/message")
    public ResponseFileView getTestMessage(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getTestMessage params [----[ : country = {} , text = {}", country, text);

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime+"_polly_sound.mp3";

        ResponseFileView view = new ResponseFileView();

        try {
            view = pollyService.getMp3(text, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    /**
     *  지워도 되는 컨트롤러
     *  */
    @GetMapping(value = "/message2")
    public ResponseFileView getTestMessage2(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getTestMessage2 params [----[ : country = {} , text = {}", country, text);

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime+"_polly_sound.mp3";
        String outputFile = "/Users/bsc/Desktop/workspace/aws-polly-test/src/main/resources/static/"+fileName;

        ResponseFileView view = new ResponseFileView();

        try {
//            FileOutputStream msg = pollyService.synthesize(text, fileName);
//            String contentType = pollyService.getContentType(text, country);
//            byte[] buff = inputStreamToByteArray(msg);
//
//            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
//            fileOutputStream.write(buff);
//            fileOutputStream.close();

            view.setStatusCode("200");
            view.setMessage("Success!");
            view.setFileUrl("https://www.example.com");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @GetMapping(value = "/play")
    public String getPlay(@RequestParam String country, @RequestParam String text) {
        log.info("]-----] PollyController.getPlay params [----[ : country = {} , text = {}", country, text);

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
