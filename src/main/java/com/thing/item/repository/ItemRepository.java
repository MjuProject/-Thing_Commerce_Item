package com.thing.item.repository;

import com.thing.item.domain.Item;
import com.thing.item.dto.ItemSimpleResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("select new com.thing.item.dto.ItemSimpleResponseDTO(i.itemId, i.itemTitle, i.itemAddress, i.price, p.itemPhotoIndex, i.status, i.createdDate) " +
            "from Item i " +
            "inner join i.photos p on i.itemId = p.itemId " +
            "where i.ownerId = :ownerId " +
            "and p.isMain = true " +
            "order by i.createdDate desc ")
    Slice<ItemSimpleResponseDTO> findByOwnerId(Integer ownerId, Pageable pageable);

    @Query("select new com.thing.item.dto.ItemSimpleResponseDTO(i.itemId, i.itemTitle, i.itemAddress, i.price, p.itemPhotoIndex, i.status, i.createdDate) " +
            "from Item i " +
            "inner join i.photos p on i.itemId = p.itemId " +
            "where i.ownerId = :ownerId " +
            "and p.isMain = true " +
            "and i.itemId IN :itemIdList")
    List<ItemSimpleResponseDTO> findByItemIdIn(List<Integer> itemIdList);
}
