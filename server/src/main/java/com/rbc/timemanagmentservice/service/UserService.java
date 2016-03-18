package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * Created by russbaker on 2/22/16.
 */
@SuppressWarnings("unchecked")
public class UserService<U extends User> {

    protected final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, ContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    //-- User CRUD

    @Transactional(propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public U createUser(U user) {
        if(!(user instanceof Customer)){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return (U) userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMINISTRATOR')")
    public U getUser(final Integer userId){
        final U user = (U)userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with ID: " + userId);
        }
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @PreAuthorize("#user.username == authentication.name or hasRole('ROLE_ADMINISTRATOR')")
    public U updateUser(final U user) {
        return (U) userRepository.save(user);
    }


    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<U> findAll(Integer start, Integer end) {
        final List<Employee> employeeList = start != null && end != null ?
                userRepository.findAll(new PageRequest(start, end)).getContent()
                : (List<Employee>) userRepository.findAll();
        if (CollectionUtils.isEmpty(employeeList)) {
            throw new NotFoundException("List of employees is empty");
        }
        return (List<U>) employeeList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer userId) {
        final  U user = (U) userRepository.findOne(userId);
        user.getEmails()
                .stream()
                .forEach(email -> email.setUser(null));

        user.getAddress()
                .stream()
                .forEach(address -> address.setUser(null));

        user.getPhones()
                .stream()
                .forEach(phone -> phone.setUser(null));

        user.getContracts()
                .stream()
                .forEach(contract -> contract.getUsers().remove(user));

        userRepository.delete(userId);
    }

    //--------- Address

    @Transactional(propagation = Propagation.REQUIRED)
    public Address addAddressToUser(final Integer userId, final Address address){
        final U user = (U) userRepository.findOne(userId);
        user.addAddress(address);
        final U savedUser = (U) userRepository.save(user);
        return savedUser.getAddress()
                .stream()
                .filter(addr -> addr.getStreet1().equals(address.getStreet1()))
                .findFirst()
                .get();
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAddressFromUser(final Integer userId, final Integer addressId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Address> addresses = user.getAddress();
        addresses.stream()
                .forEach(address1 -> address1.setUser(null));
        addresses.remove(addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .get());
        userRepository.save(user);
    }

    //----------- Phone

    @Transactional(propagation = Propagation.REQUIRED)
    public Phone addPhoneToUser(final Integer userId, final Phone phone) {
        final U user = (U) userRepository.findOne(userId);
        user.addPhone(phone);
        final U saved = (U) userRepository.save(user);
        return saved.getPhones()
                .stream()
                .filter(ph -> ph.getPhone().equals(phone.getPhone()))
                .findFirst()
                .get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removePhoneFromUser(final Integer userId, final Integer phoneId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Phone> phones = user.getPhones();
        phones.stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .forEach(phone1 -> phone1.setUser(null));
        user.getPhones().remove(user.getPhones()
        .stream()
        .filter(phone -> phone.getId().equals(phoneId))
        .findFirst()
        .get());
        userRepository.save(user);
    }

    //--------- Email

    @Transactional(propagation = Propagation.REQUIRED)
    public Email addEmailToUser(final Integer userId, final Email email) {
        final U user = (U) userRepository.findOne(userId);
        user.addEmail(email);
        final U tempVal = (U) userRepository.save(user);
        return tempVal.getEmails()
                .stream()
                .filter(em -> em.getEmail().equalsIgnoreCase(email.getEmail()))
                .findFirst()
                .get();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEmailFromUser(final Integer userId, final Integer emailId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Email> emails = user.getEmails();
        emails.stream()
                .filter(email -> email.getId().equals(emailId))
                .forEach(email1 -> email1.setUser(null));
        user.getEmails().remove(user.getEmails()
        .stream()
        .filter(email -> email.getId().equals(emailId))
        .findFirst()
        .get());
        userRepository.save(user);
    }

    //--------- Contracts

    @Transactional(propagation = Propagation.REQUIRED)
    public void addContractToUser(Integer customerId, Integer contractId) {
        final U user = (U) userRepository.findOne(customerId);
        Contract contract = contractRepository.findOne(contractId);
        if(contract == null){
            contract = contractRepository.save(contract);
        }
        user.addContract(contract);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Contract> getUserContracts(Integer userId) {
        final U user = (U) userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with Id: " + userId);
        }
        return user.getContracts();
    }
}
