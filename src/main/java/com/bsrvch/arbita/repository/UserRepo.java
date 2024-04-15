package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.User;
import com.bsrvch.arbita.model.dictionary.UserRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<User, UUID> {
    User findByTelegramId(Long telegramId);
    List<User> findAll();
    List<User> findAllByRole(UserRole role);
}
