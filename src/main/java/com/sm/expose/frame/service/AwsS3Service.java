package com.sm.expose.frame.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.dto.FrameCreateDto;
import com.sm.expose.frame.dto.FrameUploadDto;
import com.sm.expose.frame.respository.FrameRepository;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
@Transactional
public class AwsS3Service {

    private AmazonS3 s3Client;

    @Autowired
    private FrameRepository frameRepository;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    /**
     * aws s3 로 파일 업로드
     *
     * @param file
     * @return
     */
    public FrameUploadDto upload(MultipartFile file) throws IOException {

        // 고유의 파일명 위해 UUID 사용
        String fileName = UUID.randomUUID() + "_uuid_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());

            /** Warn message : No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory errors.
             **  방지 하기 위한 byte length 추가
             */
            byte[] bytes = IOUtils.toByteArray(file.getInputStream());
            metadata.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(bytes);

            PutObjectRequest putObjReq = new PutObjectRequest(bucket, fileName, byteArrayIs, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjReq);

            String filePath = s3Client.getUrl(bucket, fileName).toString();

            FrameUploadDto frameUploadDto = new FrameUploadDto();
            frameUploadDto.setFramePath(filePath);
            frameUploadDto.setS3FrameName(fileName);

            return frameUploadDto;

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            log.error("uploadToAWS AmazonServiceException filePath={}, yyyymm={}, error={}", e.getMessage());
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error("uploadToAWS SdkClientException filePath={}, error={}", e.getMessage());
        } catch (Exception e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            log.error("uploadToAWS SdkClientException filePath={}, error={}", e.getMessage());
        }
        return null;
    }

    public FrameCreateDto.downloadFileResponse getObject(long frameId) throws IOException {

        Optional<Frame> frame = frameRepository.findById(frameId);
        String storedFileName = frame.get().getS3FrameName();

        S3Object o = s3Client.getObject(new GetObjectRequest(bucket, storedFileName));

        S3ObjectInputStream objectInputStream = ((S3Object) o).getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String tempFileName = URLEncoder.encode(storedFileName, "UTF-8").replaceAll("\\+", "%20");
        String[] fileNameSplit = tempFileName.split("_uuid_");
        String fileName = fileNameSplit[1];

        return new FrameCreateDto.downloadFileResponse(bytes, fileName);
    }
}
