package com.thing.item.repository;

import com.thing.item.domain.ItemPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPhotoRepository extends JpaRepository<ItemPhoto, Integer> {
}
