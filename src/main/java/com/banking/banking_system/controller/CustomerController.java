package com.banking.banking_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.banking_system.dto.LoginRequest;
import com.banking.banking_system.dto.LoginResponse;
import com.banking.banking_system.entity.Customer;
import com.banking.banking_system.service.CustomerService;
import com.banking.banking_system.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final JwtService jwtService;

    public CustomerController(CustomerService customerService, JwtService jwtService) {
        this.customerService = customerService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@Valid @RequestBody Customer customer) {
        Customer savedCustomer = customerService.registerCustomer(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Customer customer = customerService.login(request.email(), request.password());
        String token = jwtService.generateToken(customer);
        return ResponseEntity.ok(new LoginResponse(
                token, "Bearer", customer.getId(), customer.getName(), customer.getEmail()));
    }

    @GetMapping("/test")
    public String test() {
        return "Customer API is working";
    }
}