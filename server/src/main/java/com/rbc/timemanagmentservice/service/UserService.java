package com.rbc.timemanagmentservice.service;

import com.rbc.timemanagmentservice.model.Address;
import com.rbc.timemanagmentservice.model.Email;
import com.rbc.timemanagmentservice.model.Phone;
import com.rbc.timemanagmentservice.model.User;
import com.rbc.timemanagmentservice.persistence.UserRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.NotFoundException;
import java.util.List;

/**
 * Created by russbaker on 2/22/16.
 */
public class UserService<U extends User> {

    protected final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public U getUser(final Integer userId){
        final U user = (U)userRepository.findOne(userId);
        if(user == null){
            throw new NotFoundException("Cannot find user with ID: " + userId);
        }
        return user;
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
}
