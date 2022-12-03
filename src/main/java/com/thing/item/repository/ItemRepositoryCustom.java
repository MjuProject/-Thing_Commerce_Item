package com.thing.item.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thing.item.domain.ElasticItem;
import com.thing.item.dto.ItemSearchRequestDTO;
import com.thing.item.dto.ItemSimpleResponseDTO;
import com.thing.item.dto.QItemSimpleResponseDTO;
import com.thing.item.type.OrderByNull;
import com.thing.item.type.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.thing.item.domain.QItem.item;
import static com.thing.item.domain.QItemPhoto.itemPhoto1;

@RequiredArgsConstructor
@Repository
public class ItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static final String ST_DISTANCE_SPHERE_QUERY = "ST_Distance_Sphere(POINT({0}, {1}), POINT(item_longitude, item_latitude))";
    private static final String DISTANCE = "6000";

    public List<ItemSimpleResponseDTO> findByItemList(ItemSearchRequestDTO itemSearchRequestDTO, Pageable pageable, List<ElasticItem> itemList){
        List<Integer> itemIdList = itemList.stream().map(ElasticItem::getItemId).collect(Collectors.toList());
        JPAQuery<ItemSimpleResponseDTO> query = jpaQueryFactory.select(
                        new QItemSimpleResponseDTO(
                                item.itemId.as("itemId"),
                                item.itemTitle.as("itemTitle"),
                                item.itemAddress.as("itemAddress"),
                                item.price.as("price"),
                                itemPhoto1.itemPhoto.as("itemPhoto"),
                                item.status.as("status"),
                                item.createdDate
                        )
                ).from(item)
                .leftJoin(item.photos, itemPhoto1)
                .on(itemPhoto1.isMain.eq(true))
                .where(
                        itemIdIn(itemIdList, StringUtils.hasText(itemSearchRequestDTO.getQuery())),
                        categoryBigEq(itemSearchRequestDTO.getCategoryBig()),
                        categoryMiddleEq(itemSearchRequestDTO.getCategoryMiddle()),
                        categorySmallEq(itemSearchRequestDTO.getCategorySmall()),
                        stDistanceSphere(itemSearchRequestDTO.getLatitude(), itemSearchRequestDTO.getLongitude())
                );

        return ordering(query, itemSearchRequestDTO, pageable, itemIdList);
    }

    private List<ItemSimpleResponseDTO> ordering(JPAQuery<ItemSimpleResponseDTO> query, ItemSearchRequestDTO itemSearchRequestDTO, Pageable pageable, List<Integer> itemIdList){
        OrderType orderType = itemSearchRequestDTO.getOrderType();
        if (orderType != null){
            switch(orderType){
                case ACCURATE:
                    query = query.orderBy(accurateSort(itemIdList));
                    break;
                case DISTANCE:
                    query = query.orderBy(stDistanceSphereSort(itemSearchRequestDTO.getLatitude(), itemSearchRequestDTO.getLongitude()));
                    break;
                default:
                    query = query.orderBy(orderType.getOrder());
                    break;
            }
        }
        return query.offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
    }

    private OrderSpecifier accurateSort(List<Integer> itemIdList){
        return itemIdList == null? OrderByNull.DEFAULT : Expressions.stringTemplate("FIELD({0}, {1})", item.itemId, itemIdList).asc();
    }

    private OrderSpecifier stDistanceSphereSort(Double latitude, Double longitude){ // 거리순
        return (latitude == null || longitude == null)? OrderByNull.DEFAULT : Expressions.stringTemplate(ST_DISTANCE_SPHERE_QUERY, longitude, latitude).asc();
    }

    private BooleanExpression itemIdIn(List<Integer> itemIdList, boolean isSearch){
        return !isSearch? null : item.itemId.in(itemIdList);
    }

    private BooleanExpression stDistanceSphere(Double latitude, Double longitude){ // 6Km 이내
        return (latitude == null || longitude == null) ? null : Expressions.stringTemplate(ST_DISTANCE_SPHERE_QUERY, longitude, latitude).loe(DISTANCE);
    }

    private BooleanExpression categoryBigEq(Integer categoryBig){
        return categoryBig != null ? item.categoryBig.eq(categoryBig) : null;
    }

    private BooleanExpression categoryMiddleEq(Integer categoryMiddle){
        return categoryMiddle != null ? item.categoryMiddle.eq(categoryMiddle) : null;
    }

    private BooleanExpression categorySmallEq(Integer categorySmall){
        return categorySmall != null ? item.categorySmall.eq(categorySmall) : null;
    }
}
