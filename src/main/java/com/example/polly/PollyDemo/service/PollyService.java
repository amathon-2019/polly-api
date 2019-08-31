package com.example.polly.PollyDemo.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class PollyService {

    private static final String MESSAGE = "축하합니다 Java에서 Amazon Polly의 데모를 성공적으로 구축했습니다. Amazon Polly를 사용하여 음성 지원 앱을 구축하는 것은 재미 있으며, 항상 AWS 웹 사이트에서 Amazon Polly 및 기타 AWS의 훌륭한 서비스 사용에 대한 팁과 요령을 확인하십시오.";

    @Autowired
    @Qualifier("pollyClientKorea")
    private AmazonPollyClient polly;

    @Autowired
    private PollyService self;
    private Voice voice;

    @PostConstruct
    public void init() {
        // describe voices request를 생성
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest().withLanguageCode("ko-KR");

        // Synchronously ask Amazon Polly to describe available TTS voices.
        DescribeVoicesResult describeVoicesResult = polly.describeVoices(describeVoicesRequest);
        voice = describeVoicesResult.getVoices().get(0);
    }

    /**
     * text와 format 파라미터를 받아서 파라미터 값에 맞는 음성파일을 합성하여 리턴해주는 메서드
     *
     * @param text
     * @param format
     */
    public SynthesizeSpeechResult synthesize(String text, OutputFormat format) throws IOException {
        SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId()).withOutputFormat(format);
        SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);

        return synthRes;
    }

    /**
     * synthesize 메서드를 통해 생성된 음성의 contentType을 리턴해주는 메서드
     */
    public String getContentType(String text, String country) throws IOException {
        return self.synthesize(text, OutputFormat.Mp3).getContentType();
    }

    /**
     * synthesize 메서드를 통해 생성된 음성의 mp3파일의 InputStream을 리턴해주는 메서드
     */
    public InputStream getMp3(String text, String country) throws Exception {
        return self.synthesize(text, OutputFormat.Mp3).getAudioStream();
    }

    /**
     * synthesize 메서드를 통해 생성된 음성을 플레이어로 실행시키는 메서드
     */
    public void playDemo() throws Exception {
        //create the test class
        PollyService helloWorld = self;
        //get the audio stream
        InputStream speechStream = helloWorld.synthesize(MESSAGE, OutputFormat.Mp3).getAudioStream();

        //create an MP3 player
        AdvancedPlayer player = new AdvancedPlayer(speechStream,
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent evt) {
                System.out.println("Playback started");

                System.out.println(MESSAGE);
            }

            @Override
            public void playbackFinished(PlaybackEvent evt) {
                System.out.println("Playback finished");
            }
        });

        // play it!
        player.play();
    }

    /**
     * synthesize 메서드를 통해 생성된 음성을 플레이어로 실행시키는 메서드
     */
    public void play(String country, String text) throws Exception {
        //create the test class
        PollyService service = self;
        //get the audio stream
        InputStream speechStream = service.synthesize(text, OutputFormat.Mp3).getAudioStream();

        //create an MP3 player
        AdvancedPlayer player = new AdvancedPlayer(speechStream,
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackStarted(PlaybackEvent evt) {
                System.out.println("Playback started");
                System.out.println(text);
            }

            @Override
            public void playbackFinished(PlaybackEvent evt) {
                System.out.println("Playback finished");
            }
        });

        // play it!
        player.play();
    }

    private Region getRegionEnum(String country) {
        Region region;
        switch (country) {
            case "KR":
                region = Region.getRegion(Regions.AP_NORTHEAST_2);
                break;
            case "US":
                region = Region.getRegion(Regions.US_EAST_1);
                break;
            case "JP":
                region = Region.getRegion(Regions.AP_NORTHEAST_1);
                break;
            default:
                region = Region.getRegion(Regions.AP_NORTHEAST_2);
                break;
        }
        return region;
    }

    private Regions getRegions(String country) {
        Regions regions;
        switch (country) {
            case "KR":
                regions = Regions.AP_NORTHEAST_2;
                break;
            case "US":
                regions = Regions.US_EAST_1;
                break;
            case "JP":
                regions = Regions.AP_NORTHEAST_1;
                break;
            default:
                regions = Regions.AP_NORTHEAST_2;
                break;
        }
        return regions;
    }

    private String getVoiceLanguageCode(String country) {
        String languageCode;
        switch (country) {
            case "KR":
                languageCode = "ko-KR";
                break;
            case "US":
                languageCode = "en-US";
                break;
            case "JP":
                languageCode = "ja-JP";
                break;
            default:
                languageCode = "ko-KR";
                break;
        }
        return languageCode;
    }

    private String getVoiceLanguageCode(Regions regions) {
        String languageCode;
        switch (regions.getName()) {
            case "ap-northeast-2":
                languageCode = "ko-KR";
                break;
            case "us-east-1":
                languageCode = "en-US";
                break;
            case "ap-northeast-1":
                languageCode = "ja-JP";
                break;
            default:
                languageCode = "ko-KR";
                break;
        }
        return languageCode;
    }

}