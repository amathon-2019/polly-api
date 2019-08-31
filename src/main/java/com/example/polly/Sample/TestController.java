package com.example.polly.Sample;

import com.example.polly.Sample.model.TestMessageView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/message")
    public TestMessageView getTestMessage() {
        TestMessageView view = new TestMessageView();
        view.setStatusCode("200");
        view.setMessage("Hello world!");
        return view;
    }
}
