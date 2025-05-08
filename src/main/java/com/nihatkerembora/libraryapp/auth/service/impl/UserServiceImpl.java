package com.nihatkerembora.libraryapp.auth.service.impl;


import com.nihatkerembora.libraryapp.auth.exception.UserNotFoundException;
import com.nihatkerembora.libraryapp.auth.exception.UserStatusNotValidException;
import com.nihatkerembora.libraryapp.auth.model.User;
import com.nihatkerembora.libraryapp.auth.model.dto.request.CustomPagingRequest;
import com.nihatkerembora.libraryapp.auth.model.dto.request.UpdateUserRequest;
import com.nihatkerembora.libraryapp.auth.model.entity.UserEntity;
import com.nihatkerembora.libraryapp.auth.model.mapper.UserMapper;
import com.nihatkerembora.libraryapp.auth.repository.UserRepository;
import com.nihatkerembora.libraryapp.auth.service.UserService;
import com.nihatkerembora.libraryapp.common.model.CustomPage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(String id, boolean includeDeleted) {

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toDomain(userEntity);
    }

    @Override
    public CustomPage<User> getUsers(CustomPagingRequest pagingRequest, boolean includeDeleted) {
        Page<UserEntity> userPage = userRepository.findAllWithOptionalDeleted(includeDeleted, pagingRequest.toPageable());

        final List<User> users = UserMapper.toDomainList(userPage.getContent()); //List<User>

        return CustomPage.of(users, userPage);
    }

    @Override
    public User update(String id, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        BeanUtils.copyProperties(request, userEntity, getNullPropertyNames(request));

        UserEntity updatedEntity = userRepository.save(userEntity);

        return UserMapper.toDomain(updatedEntity);
    }

    @Override
    public void softDelete(String id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userEntity.isDeleted()) {
            throw new UserStatusNotValidException("User already marked as deleted!");
        }

        userEntity.setDeleted(true);

        userRepository.save(userEntity);
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> nullPropertyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            if (src.getPropertyValue(pd.getName()) == null) {
                nullPropertyNames.add(pd.getName());
            }
        }
        return nullPropertyNames.toArray(new String[0]);
    }
}