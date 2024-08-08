package com.electric_titans.notificationservice.service.impl;

import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.common.event.ReconciliationEvent;
import com.electric_titans.common.event.TransactionCreatedEvent;
import com.electric_titans.common.event.UserCreatedEvent;
import com.electric_titans.notificationservice.entity.User;
import com.electric_titans.notificationservice.repository.UserRepository;
import com.electric_titans.notificationservice.service.MailService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final UserRepository userRepository;

    @Value("${sendgrid.api-key}")
    private String sendgridApiKey;

    @Value("${sendgrid.email}")
    private String sendgridEmail;

    @Value("${sendgrid.template.user-created}")
    private String templateUserCreated;

    @Value("${sendgrid.template.transaction-created}")
    private String templateTransactionCreated;

    @Value("${sendgrid.template.payment-succeeded}")
    private String templatePaymentSucceeded;

    @Value("${sendgrid.template.reconciliation-finished}")
    private String templateReconciliationFinished;

    @Override
    @KafkaListener(topics = "USER-CREATED", groupId = "${spring.kafka.consumer.group-id}")
    public String userCreatedEmail(UserCreatedEvent userCreatedEvent) throws IOException {
        log.debug("userCreatedEmail({})", userCreatedEvent.getEmail());
        Email to = new Email(userCreatedEvent.getEmail());
        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("fullName", userCreatedEvent.getName());
        return sendEmail(personalization, "User Created", templateUserCreated);
    }

    @Override
    @KafkaListener(topics = {"DEPOSIT-REQUEST", "WITHDRAW-REQUEST", "TRANSFER-REQUEST"}, groupId = "${spring.kafka.consumer.group-id}")
    public String transactionCreatedEmail(TransactionCreatedEvent transactionCreatedEvent) throws IOException {
        log.debug("transactionCreatedEmail({})", transactionCreatedEvent.getTransactionId());
        User user = userRepository.findByCustomerId(transactionCreatedEvent.getCustomerId()).get();
        String email = user.getEmail();
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName().concat(" ");
        String fullName = user.getFirstName().concat(" ").concat(middleName).concat(user.getLastName());
        Email to = new Email(email);

        String amount = formatAmount(transactionCreatedEvent.getAmount());

        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("name", fullName);
        personalization.addDynamicTemplateData("amount", amount);
        personalization.addDynamicTemplateData("currency", transactionCreatedEvent.getCurrency());
        personalization.addDynamicTemplateData("paymentMethod", "Card"); // Default payment method is card
        return sendEmail(personalization, "User Created", templateTransactionCreated);
    }

    @Override
    @KafkaListener(topics = {"DEPOSIT-SUCCESS", "WITHDRAW-SUCCESS", "TRANSFER-SUCCESS"}, groupId = "${spring.kafka.consumer.group-id}")
    public String paymentSucceededEmail(PaymentSuccessEvent paymentSuccessEvent) throws IOException {
        log.debug("paymentSucceededEmail({})", paymentSuccessEvent.getTransactionId());
        User user = userRepository.findByCustomerId(paymentSuccessEvent.getCustomerId()).get();
        String email = user.getEmail();
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName().concat(" ");
        String fullName = user.getFirstName().concat(" ").concat(middleName).concat(user.getLastName());
        Email to = new Email(email);

        String amount = formatAmount(paymentSuccessEvent.getTransactionAmount());

        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("amount", amount);
        personalization.addDynamicTemplateData("name", fullName);
        personalization.addDynamicTemplateData("date", Instant.now().toString());
        return sendEmail(personalization, "Payment Succeeded", templatePaymentSucceeded);
    }

    @Override
    @KafkaListener(topics = "RECONCILIATION-FINISHED", groupId = "${spring.kafka.consumer.group-id}")
    public String reconciliationFinishedEmail(ReconciliationEvent reconciliationEvent) throws IOException {
        log.debug("reconciliationFinishedEmail({})", reconciliationEvent.getCustomerId());
        User user = userRepository.findByCustomerId(reconciliationEvent.getCustomerId()).get();
        String email = user.getEmail();
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName().concat(" ");
        String fullName = user.getFirstName().concat(" ").concat(middleName).concat(user.getLastName());
        Email to = new Email(email);

        StringBuilder reconciliationStatus = new StringBuilder();
        if (reconciliationEvent.getStatus().equalsIgnoreCase("RECONCILED")) reconciliationStatus.append("Reconciled");
        else if (reconciliationEvent.getStatus().equalsIgnoreCase("FINISHED_WITH_DISCREPANCY"))
            reconciliationStatus.append("Finished With Discrepancy");
        else reconciliationStatus.append("Failed");

        String amount = formatAmount(reconciliationEvent.getAmount());

        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        personalization.addDynamicTemplateData("status", reconciliationStatus.toString());
        personalization.addDynamicTemplateData("amount", amount);
        personalization.addDynamicTemplateData("currency", reconciliationEvent.getCurrency());
        personalization.addDynamicTemplateData("name", fullName);
        personalization.addDynamicTemplateData("date", reconciliationEvent.getDate().toString());
        return sendEmail(personalization, "Reconciliation Finished", templateReconciliationFinished);
    }

    private String sendEmail(DynamicTemplatePersonalization personalization, String subject, String template) throws IOException {
        Mail mail = new Mail();
        Email emailFrom = new Email(sendgridEmail);
        mail.setFrom(emailFrom);
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.setTemplateId(template);
        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info(response.getBody());
            return response.getBody();
        } catch (IOException ex) {
            throw ex;
        }
    }

    private String formatAmount(Long amount) {
        double decimalAmount = amount / 100.0;
        DecimalFormat df = new DecimalFormat("0.00");
        String formattedAmount = df.format(decimalAmount);
        return formattedAmount;
    }

    private static class DynamicTemplatePersonalization extends Personalization {

        @JsonProperty(value = "dynamic_template_data")
        private Map<String, Object> dynamic_template_data;

        @JsonProperty("dynamic_template_data")
        public Map<String, Object> getDynamicTemplateData() {
            log.debug("getDynamicTemplateData()");
            if (dynamic_template_data == null) {
                return Collections.<String, Object>emptyMap();
            }
            return dynamic_template_data;
        }

        public void addDynamicTemplateData(String key, String value) {
            log.debug("addDynamicTemplateData({}, {})", key, value);
            if (dynamic_template_data == null) {
                dynamic_template_data = new HashMap<String, Object>();
                dynamic_template_data.put(key, value);
            } else {
                dynamic_template_data.put(key, value);
            }
        }
    }
}
