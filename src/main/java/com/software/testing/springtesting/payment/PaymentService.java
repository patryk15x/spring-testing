package com.software.testing.springtesting.payment;

import com.software.testing.springtesting.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {


    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

    private final CustomerRepository customerRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(CustomerRepository customerRepository,
                          PaymentRepository paymentRepository,
                          CardPaymentCharger cardPaymentCharger) {
        this.customerRepository = customerRepository;
        this.paymentRepository = paymentRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();
        if (!isCustomerFound) {
            throw new IllegalStateException(String.format("Customer with id [%s] not found", customerId));
        }

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.stream()
                .anyMatch(c -> c.equals(paymentRequest.payment().getCurrency()));

        if (!isCurrencySupported) {
            String message = String.format(
                    "Currency [%s] not supported",
                    paymentRequest.payment().getCurrency());
            throw new IllegalStateException(message);
        }

        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.payment().getSource(),
                paymentRequest.payment().getAmount(),
                paymentRequest.payment().getCurrency(),
                paymentRequest.payment().getDescription()
        );

        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }

        paymentRequest.payment().setCustomerId(customerId);

        paymentRepository.save(paymentRequest.payment());
    }

    public Payment getPayment(Long paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        boolean isPaymentFound = paymentOptional.isPresent();

        if (!isPaymentFound){
            throw new IllegalStateException(String.format("Payment with id [%s] not found", paymentId));
        }

        return paymentOptional.get();
    }
}
