package com.rbc.timemanagmentservice.util;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

/**
 * Created by russbaker on 3/2/16.
 */
@Component
public class VelocityEmailHelper {
    private static final Logger logger = LoggerFactory.getLogger(VelocityEmailHelper.class);
//    private String TEMPLATES_INVOICE_VM = "templates/invoice.vm";

    private final VelocityEngine velocityEngine;
    private final JavaMailSender mailSender;

    /**
     * Constructor
     */
    @Autowired
    public VelocityEmailHelper(VelocityEngine velocityEngine,
                               JavaMailSender mailSender) {
        this.velocityEngine = velocityEngine;
        this.mailSender = mailSender;
    }
    /**
     * Sends e-mail using Velocity template for the body and
     * the properties passed in as Velocity variables.
     *  @param   msg                 The e-mail message to be sent, except for the body.
     * @param   hTemplateVariables  Variables to use when processing the template.
     * @param template
     */
    public void send(final SimpleMailMessage msg,
                     final Map<String, Object> hTemplateVariables, String template) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(msg.getTo());
            message.setFrom(msg.getFrom());
            message.setSubject(msg.getSubject());

            String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", hTemplateVariables);

            logger.info("body={}", body);

            message.setText(body, true);
        };

        mailSender.send(preparator);

        logger.info("Sent e-mail to '{}'.", msg.getTo());
    }
}
