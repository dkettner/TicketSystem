package com.kett.TicketSystem.user.application;

import com.kett.TicketSystem.domainprimitives.EmailAddress;
import com.kett.TicketSystem.membership.application.MembershipService;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.domain.exceptions.NoUserFoundException;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import com.kett.TicketSystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembershipService membershipService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MembershipService membershipService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.membershipService = membershipService;
    }

    public User getUserById(UUID id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NoUserFoundException("could not find user with id: " + id));
    }

    public User getUserByEMailAddress(EmailAddress eMailAddress) {
        return userRepository
                .findByEmailEquals(eMailAddress)
                .orElseThrow(() -> new NoUserFoundException("could not find user with eMailAddress: " + eMailAddress));
    }

    public UUID getUserIdByEmail(EmailAddress postingUserEmail) {
        return this.getUserByEMailAddress(postingUserEmail).getId();
    }

    public UUID getUserIdByEmail(String postingUserEmail) {
        return this.getUserIdByEmail(EmailAddress.fromString(postingUserEmail));
    }

    public boolean isExistentById(UUID id) {
        return userRepository.existsById(id);
    }

    public User addUser(User user) {
        if (userRepository.findByEmailEquals(user.getEmail()).isPresent()) {
            throw new UserException("user with email: " + user.getEmail().toString() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.getUserByEMailAddress(EmailAddress.fromString(email));
        List<GrantedAuthority> grantedAuthorities = membershipService.getAuthoritiesByUserId(user.getId());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail().toString(),
                user.getPassword(),
                grantedAuthorities
        );
    }
}
