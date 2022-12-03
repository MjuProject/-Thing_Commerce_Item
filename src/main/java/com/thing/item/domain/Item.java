package com.thing.item.domain;

import com.thing.item.dto.ItemSaveRequestDTO;
import lombok.*;
import org.springframework.data.geo.Point;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name="Item")
public class Item{

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;
    @Column(name = "owner_id")
    private Integer ownerId;
    @Column(name = "category_big")
    private Integer categoryBig;
    @Column(name = "category_middle")
    private Integer categoryMiddle;
    @Column(name = "category_small")
    private Integer categorySmall;
    @Column(name = "item_title")
    private String itemTitle;
    @Column(name = "item_content")
    private String itemContent;
    @Column
    private int price;
    @Column(name = "item_latitude")
    private double itemLatitude;
    @Column(name = "item_longitude")
    private double itemLongitude;
    @Column(name = "item_address")
    private String itemAddress;
    @Column
    private int views;
    @Column(name = "created_date")
    private Date createdDate;
    @Column
    private boolean status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "item_id")
    @Builder.Default
    private List<ItemPhoto> photos = new ArrayList<ItemPhoto>();

    public void modifyItemInfo(ItemSaveRequestDTO itemSaveRequestDTO){
        this.categoryBig = itemSaveRequestDTO.getCategoryBig();
        this.categoryMiddle = itemSaveRequestDTO.getCategoryMiddle();
        this.categorySmall = itemSaveRequestDTO.getCategorySmall();
        this.itemTitle = itemSaveRequestDTO.getItemTitle();
        this.itemContent = itemSaveRequestDTO.getItemContent();
        this.price = itemSaveRequestDTO.getPrice();
        this.itemLatitude = itemSaveRequestDTO.getItemLatitude();
        this.itemLongitude = itemSaveRequestDTO.getItemLongitude();
        this.itemAddress = itemSaveRequestDTO.getItemAddress();
    }

    public void setPoint(Point point){
        this.itemLongitude = point.getX();
        this.itemLatitude = point.getY();
    }

    public void addView(){
        this.views += 1;
    }

}
