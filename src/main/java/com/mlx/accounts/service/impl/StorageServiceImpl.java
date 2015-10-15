package com.mlx.accounts.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.service.StorageService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class StorageServiceImpl implements StorageService {
    @Inject
    private AppConfiguration appConfiguration;

    public static AmazonS3 amazonS3;

    public StorageServiceImpl() {
    }

    @Override
    public String saveUserPicture(String url, String uid) {

        try {
            InputStream input;
            ObjectMetadata metadata = new ObjectMetadata();
            if (url != null) {
                URL oracle = new URL(url);
                URLConnection urlConnection = oracle.openConnection();
                metadata.setContentType(urlConnection.getContentType());
                metadata.setContentLength(urlConnection.getContentLength());
                input = urlConnection.getInputStream();
            } else {
                input = getClass().getResourceAsStream("/user-default.jpg");
                metadata.setContentType("image/jpeg");
                metadata.setContentLength(2901);
            }

            String fileId = "account/picture/" + uid;
            AppConfiguration.AWSConfiguration.AmazonS3Configuration s3Configuration =
                    appConfiguration.getAWSConfiguration().getS3();

            PutObjectRequest request = new PutObjectRequest(s3Configuration.getBucket(),
                    fileId, input, metadata);
            request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3().putObject(request);

            input.close();

            return s3Configuration.getDomain() + s3Configuration.getBucket() + "/" + fileId;
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private AmazonS3 s3() {
        if (amazonS3 == null) {
            AppConfiguration.AWSConfiguration.AmazonS3Configuration s3Configuration =
                    appConfiguration.getAWSConfiguration().getS3();
            AWSCredentials awsCredentials = new BasicAWSCredentials(
                    s3Configuration.getAccessKey(),
                    s3Configuration.getSecretKey());
            amazonS3 = new AmazonS3Client(awsCredentials);
        }
        return amazonS3;
    }
}
