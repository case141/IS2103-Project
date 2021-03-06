/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author casseylow
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(unique = true, nullable = false)
    private String name; //Deluxe Room, Premier Room, Family Room, Junior Suite, Grand Suite, etc. new types can be defined
    private String description;
    @Column(nullable = false)
    private Integer roomSize;
    private String bed;
    @Column(nullable = false)
    private Integer capacity;
    private String amenities;
    private String status;
    private Integer roomRank; //1 is highest room rank
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    @OneToMany(mappedBy = "roomType",fetch = FetchType.EAGER)
    private List<Room> rooms;
    @OneToMany(mappedBy = "roomType",fetch = FetchType.EAGER)
    private List<ReservationLineItem> reservationLineItems;
    

    public RoomType() {
        roomRates=new ArrayList<>();
        rooms=new ArrayList<>();
        reservationLineItems=new ArrayList<>();
    }
        
    public RoomType(String name, String description, Integer roomSize, String bed, Integer capacity, String amenities, String status, Integer roomRank) {
        this();
        this.name = name;
        this.description = description;
        this.roomSize = roomSize;
        this.bed = bed;
        this.capacity = capacity;
        this.amenities = amenities;
        this.status = status;
        this.roomRank = roomRank;
    }

    public Integer getRoomRank() {
        return roomRank;
    }

    public void setRoomRank(Integer roomRank) {
        this.roomRank = roomRank;
    }
    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRoomSize() {
        return roomSize;
    }

    public void setRoomSize(Integer roomSize) {
        this.roomSize = roomSize;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ReservationLineItem> getReservationLineItems() {
        return reservationLineItems;
    }

    public void setReservationLineItems(List<ReservationLineItem> reservationLineItems) {
        this.reservationLineItems = reservationLineItems;
    }

    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomTypeId fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "entity.RoomTypeEntity[ id=" + roomTypeId + " ]";
    }
    
}