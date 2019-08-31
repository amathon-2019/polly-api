package com.example.polly.PollyDemo.model;

import com.example.polly.common.model.MessageView;
import lombok.Data;

@Data
public class ResponseFileView extends MessageView {
    private String fileUrl;
}
