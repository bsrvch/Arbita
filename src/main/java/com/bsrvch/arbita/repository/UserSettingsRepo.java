package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.UserSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSettingsRepo extends CrudRepository<UserSettings, UUID> {
}
