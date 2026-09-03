package com.saloon.services.impl;

import com.saloon.exceptions.ResourceNotFoundException;
import com.saloon.models.User;
import com.saloon.payloads.dtos.KeycloakUserDto;
import com.saloon.payloads.dtos.UserProfileDto;
import com.saloon.repositories.UserRepository;
import com.saloon.services.KeycloakService;
import com.saloon.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final KeycloakService keycloakService;

    @Override
    public User createUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+id));
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email : "+email));
    }

    @Override
    public User updateUser(User user, Long id) {
        User existingUser = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+id));
        if(!Objects.equals(user.getId(), id)){
            throw new ResourceNotFoundException("User id does not match");
        }
        existingUser.setFullName(user.getFullName());
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setPhone(user.getPhone());

        return this.userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+id));
        this.userRepository.delete(user);
    }

    @Override
    public User getUserByJwt(String jwt) {
        UserProfileDto userProfileDto = keycloakService.fetchUserProfileByJwt(jwt);
        return userRepository.findByUsername(userProfileDto.getUsername()).orElseThrow(
                () -> new ResourceNotFoundException("User not found with username : "+userProfileDto.getUsername()));
    }
}
