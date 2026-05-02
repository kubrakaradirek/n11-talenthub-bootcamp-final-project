package com.n11bootcamp.shopping_card_service.repository;


import com.n11bootcamp.shopping_card_service.entity.ShoppingCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCardRepository extends CrudRepository<ShoppingCard, String> {
}