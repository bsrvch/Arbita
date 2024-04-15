package com.bsrvch.arbita.repository;

import com.bsrvch.arbita.model.Filter;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface FilterRepo extends CrudRepository<Filter, UUID> {
    void deleteByTypeAndName(String type, String name);
}
