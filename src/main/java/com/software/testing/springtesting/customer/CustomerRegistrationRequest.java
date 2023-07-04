package com.software.testing.springtesting.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerRegistrationRequest(@JsonProperty("customer") Customer customer) {

}
