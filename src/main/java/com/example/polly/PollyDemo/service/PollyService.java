package com.example.polly.PollyDemo.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3control.model.AWSS3ControlException;
import com.example.polly.PollyDemo.model.ResponseFileView;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PollyService {

    private static final String MESSAGE = "축하합니다 Java에서 Amazon Polly의 데모를 성공적으로 구축했습니다. Amazon Polly를 사용하여 음성 지원 앱을 구축하는 것은 재미 있으며, 항상 AWS 웹 사이트에서 Amazon Polly 및 기타 AWS의 훌륭한 서비스 사용에 대한 팁과 요령을 확인하십시오.";

    @Autowired
    @Qualifier("pollyClientKorea")
    private AmazonPollyClient polly;

    @Autowired
    @Qualifier("s3Client")
    private AmazonS3 s3Client;

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
     * synthesize 메서드를 통해 생성된 음성의 mp3파일을 S3 버킷에 업로드 하고
     * 업로드한 url을 리턴해주는 메서드
     */
    public String getMp3(String text, String fileName) {
        log.info("]-----] PollyService.getMp3::params [-----[ : text : {} , fileName : {}", text, fileName);

        String returnUrl = null;

        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Mp3)
                .withVoiceId(voice.getId())
                .withText(text);
        try {
            SynthesizeSpeechResult synthesizeSpeechResult = polly.synthesizeSpeech(synthesizeSpeechRequest);

            Bucket bucketItem = new Bucket();

            for (Bucket bucket : s3Client.listBuckets()) {
                log.info("]-----] AWS S3 Bucket List item [-----[ : {}", bucket);
                bucketItem = bucket;
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("audio/mpeg3");
            s3Client.putObject(bucketItem.getName(), fileName, synthesizeSpeechResult.getAudioStream(), metadata);

            URL url = s3Client.getUrl(bucketItem.getName(), fileName);
            log.info("]-----] AWS S3 File Uploaded link [-----[ : Host : {} , Path : {} , File : {} ", url.getHost(), url.getPath(), url.getFile());

            returnUrl = "http://"+url.getHost() + url.getPath();
        } catch (Exception e) {
            System.err.println("Exception caught: " + e);
        } finally {
            return returnUrl;
        }
    }

    /**
     * synthesize 메서드를 통해 생성된 음성의 mp3파일을 FileOutputStream으로 return 해주는 메서드
     */
    public FileOutputStream getFileOutputStream(String text, String fileName) {
        log.info("]-----] PollyService.getFileOutputStream::params [-----[ : text : {}", text);

        SynthesizeSpeechRequest synthesizeSpeechRequest = new SynthesizeSpeechRequest()
                .withOutputFormat(OutputFormat.Mp3)
                .withVoiceId(voice.getId())
                .withText(text);

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(new File(fileName));
            SynthesizeSpeechResult synthesizeSpeechResult = polly.synthesizeSpeech(synthesizeSpeechRequest);
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            try (InputStream in = synthesizeSpeechResult.getAudioStream()){
                while ((readBytes = in.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readBytes);
                }
                outputStream.close();
            }
        } catch (Exception e) {
            System.err.println("Exception caught: " + e);
        } finally {
            return outputStream;
        }
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

    /**
     * country -> RegionsEnum
     * */
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

    /**
     * country -> Regions
     * */
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

    /**
     * country -> languageCode
     * */
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

    /**
     * Regions -> languageCode
     * */
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

//    private String doSentenceConversion(String type, String title, String description, LocalDateTime dateTime) {
//
//        /*
//        1. 할일 하나에 대한 미리알림 시 제목, 본문 같이 읽기
//        ex) 00시00분에 {제목}, {본문} 이 있습니다.
//
//        2. briefing
//        - 버튼 누를 시 -> 현재부터 24:00까지 해야할 모든 일정을 브리핑
//        ex) 앞으로 오늘 남은 일정은 00시00분에 {제목}, 00시00분에 {제목}, 00시00분에 {제목} 입니다.
//        ex) 앞으로 남은 일정은 없습니다.
//        - 알림 시각을 설정하여 브리핑
//        ex) 오늘 예정된 일정은 ({시간}, {제목}), ({시간} {제목}), ({시간} {제목}) 입니다.
//        ex) 앞으로 남은 일정은 없습니다. (edited)
//        * */
//        String converionText = null;
//
//        switch (type) {
//            case "schedule":
//                converionText = dateTime.toString()+"에 "+title+", "+description+" 이 있습니다.";
//                break;
//            case "brief":
//                converionText = "오늘 예정된 일정은 ";
//                for ()
//                break;
//            default :
//
//                break;
//
//
//        }
//    }

}