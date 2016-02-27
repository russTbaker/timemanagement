package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.*;
import com.rbc.timemanagmentservice.persistence.ContractRepository;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.data.domain.PageRequest;
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

    public UserService(UserRepository userRepository, ContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
    }


    //-- User CRUD

    @Transactional(propagation = Propagation.REQUIRED)
    public U createUser(U user) {
        return (U) userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public U getUser(final Integer userId){
        final U user = (U)userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with ID: " + userId);
        }
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public U updateUser(final U employee) {
        return (U) userRepository.save(employee);
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
        userRepository.delete(userId);
    }

    //--------- Address

    @Transactional(propagation = Propagation.REQUIRED)
    public void addAddressToUser(final Integer userId, final Address address){
        final U user = (U) userRepository.findOne(userId);
        user.addAddress(address);
        userRepository.save(user);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void removeAddressFromUser(final Integer userId, final Integer addressId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Address> addresses = user.getAddress();
        addresses.remove(addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .get());
        userRepository.save(user);
    }

    //----------- Phone

    @Transactional(propagation = Propagation.REQUIRED)
    public void addPhoneToUser(final Integer userId, final Phone phone) {
        final U user = (U) userRepository.findOne(userId);
        user.addPhone(phone);
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removePhoneFromUser(final Integer userId, final Integer phoneId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Phone> phones = user.getPhones();
        phones.remove(phones.stream()
                .filter(phone -> phone.getId().equals(phoneId))
                .findFirst()
                .get());
        userRepository.save(user);
    }

    //--------- Email

    @Transactional(propagation = Propagation.REQUIRED)
    public void addEmailToUser(final Integer userId, final Email email) {
        final U user = (U) userRepository.findOne(userId);
        user.addEmail(email);
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeEmailFromUser(final Integer userId, final Integer emailId) {
        final U user = (U) userRepository.findOne(userId);
        final List<Email> emails = user.getEmails();
        emails.remove(emails.stream()
                .filter(email -> email.getId().equals(emailId))
                .findFirst()
                .get());
        userRepository.save(user);
    }


    protected DateTime getLastDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.SUNDAY);
    }

    protected DateTime getFirstDayOfWeek() {
        return new DateTime().withDayOfWeek(DateTimeConstants.MONDAY);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addContractToUser(Integer customerId, Integer contractId) {
        final U user = (U) userRepository.findOne(customerId);
        Contract contract = contractRepository.findOne(contractId);
        if(contract == null){
            contract = contractRepository.save(contract);
        }
        user.addContract(contract);
    }
}
