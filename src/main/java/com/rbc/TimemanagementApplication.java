package com.rbc;

import com.rbc.model.Customer;
import com.rbc.persistence.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TimemanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimemanagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(getCustomer("Z2M4"));
			repository.save(getCustomer("HDS"));


			// fetch all customers
			System.err.println("Customers found with findAll():");
			System.err.println("-------------------------------");
			for (Customer customer : repository.findAll()) {
				System.err.println(customer.toString());
			}
			System.err.println("");

			// fetch an individual customer by ID
			Customer customer = repository.findOne(1);
			System.err.println("Customer found with findOne(1L):");
			System.err.println("--------------------------------");
			System.err.println(customer.toString());
			System.err.println("");

			// fetch customers by last name
			System.err.println("Customer found with findByLastName('Z2M4'):");
			System.err.println("--------------------------------------------");
			for (Customer custom : repository.findByContactName("Z2M4")) {
				System.err.println(custom.toString());
			}
			System.err.println("");
		};
	}

	private Customer getCustomer(String customerName) {
		Customer customer = new Customer();
		customer.setContactName(customerName);
		return customer;
	}

}
