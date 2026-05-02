package com.n11bootcamp.payment_service.client;

import com.iyzipay.Options;
import com.iyzipay.model.Payment;
import com.iyzipay.request.CreatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IyzipayHttpClient {

    private static final Logger log = LoggerFactory.getLogger(IyzipayHttpClient.class);

    private final Options options;

    public IyzipayHttpClient(Options options) {
        this.options = options;
    }

    public Payment createPayment(CreatePaymentRequest request) {
        Payment payment = Payment.create(request, options);
        log.info("Iyzipay payment response status={}, paymentId={}, conversationId={}",
                payment.getStatus(), payment.getPaymentId(), payment.getConversationId());
        return payment;
    }
}
