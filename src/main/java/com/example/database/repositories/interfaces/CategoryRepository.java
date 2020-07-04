package com.example.database.repositories.interfaces;

import com.example.database.entity.Category;
import io.smallrye.mutiny.Uni;

import java.util.Set;

public interface CategoryRepository {

    Uni<Set<Category>> getAll();

    Uni<Set<Category>> getCategoriesByItemId(Long id);

}
