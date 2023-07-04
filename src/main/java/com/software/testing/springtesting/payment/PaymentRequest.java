package com.software.testing.springtesting.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentRequest(@JsonProperty("payment") Payment payment) {
}
