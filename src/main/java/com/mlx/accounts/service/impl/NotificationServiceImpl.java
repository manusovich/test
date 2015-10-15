package com.mlx.accounts.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.google.inject.Inject;
import com.mlx.accounts.AppConfiguration;
import com.mlx.accounts.exception.ApplicationException;
import com.mlx.accounts.model.Notification;
import com.mlx.accounts.notification.NotificationEmail;
import com.mlx.accounts.notification.builder.NotificationBuilder;
import com.mlx.accounts.repository.ApplicationRepository;
import com.mlx.accounts.service.NotificationService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.StringWriter;

/**
 * <p>
 * 9/8/14.
 */
@Singleton
public class NotificationServiceImpl implements NotificationService {
    @Inject
    private AppConfiguration appConfiguration;

    @Inject
    private ApplicationRepository repository;


    @Override
    public void notification(Notification notification) throws ApplicationException {
        if (notification != null && notification instanceof NotificationEmail) {
            sendAmazonSESMail((NotificationEmail) notification);
        } else {
            throw new UnsupportedOperationException();
        }
    }


    @Override
    public void systemAlert(String text) {
        NotificationEmail notification = NotificationBuilder.get().email()
                .subject("System Alert")
                .title("")
                .simple(appConfiguration.getAWSConfiguration().getSes().getSystemAlertReceiver())
                .message(text)
                .build();
        try {
            sendAmazonSESMail(notification);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    private String getBody(NotificationEmail notificationEmail) throws TemplateException {
        String html = "";
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
        cfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/"));
        try {
            Template template = cfg.getTemplate(notificationEmail.getTemplate().getPath());

            StringWriter out = new StringWriter();
            template.process(notificationEmail, out);
            out.flush();

            html = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    public void sendAmazonSESMail(NotificationEmail notificationEmail) throws ApplicationException {

        try {
            // Construct an object to contain the recipient address.
            Destination destination = new Destination();
            if (notificationEmail.getTo() != null) {
                destination = destination.withToAddresses(
                        notificationEmail.getTo().getName() + " <" + notificationEmail.getTo().getEmail() + ">");
            }
            if (notificationEmail.getCc() != null) {
                destination = destination.withCcAddresses(
                        notificationEmail.getCc().getName() + " <" + notificationEmail.getCc().getEmail() + ">");
            }

            // Create the subject and body of the message.
            Content subject = new Content().withData(notificationEmail.getSubject());

            String html = getBody(notificationEmail);
            Body body = new Body().withHtml(new Content().withData(html));

            // Create a message with the specified subject and body.
            Message message = new Message().withSubject(subject).withBody(body);

            // Assemble the email.
            SendEmailRequest request = new SendEmailRequest()
                    .withSource(appConfiguration.getAWSConfiguration().getSes().getFrom())
                    .withDestination(destination)
                    .withMessage(message);


            // Instantiate an Amazon SES client, which will make the service call. The service call requires your AWS credentials.
            // Because we're not providing an argument when instantiating the client, the SDK will attempt to find your AWS credentials
            // using the default credential provider chain. The first place the chain looks for the credentials is in environment variables
            // AWS_ACCESS_KEY_ID and AWS_SECRET_KEY.
            // For more information, see http://docs.aws.amazon.com/AWSSdkDocsJava/latest/DeveloperGuide/credentials.html
            AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return appConfiguration.getAWSConfiguration().getSes().getAccessKey();
                }

                @Override
                public String getAWSSecretKey() {
                    return appConfiguration.getAWSConfiguration().getSes().getSecretKey();
                }
            });

            // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your sandbox
            // status, sending limits, and Amazon SES identity-related settings are specific to a given AWS
            // region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
            // the US West (Oregon) region. Examples of other regions that Amazon SES supports are US_EAST_1
            // and EU_WEST_1. For a complete list, see http://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html
            Region REGION = Region.getRegion(Regions.US_WEST_2);
            client.setRegion(REGION);

            // Send the email.
            client.sendEmail(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public ApplicationRepository getRepository() {
        return repository;
    }

    public void setRepository(ApplicationRepository repository) {
        this.repository = repository;
    }
}
