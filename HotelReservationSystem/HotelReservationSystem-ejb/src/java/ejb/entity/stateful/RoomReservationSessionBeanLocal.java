/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.entity.stateful;

import entity.OnlineReservation;
import entity.ReservationLineItem;
import java.util.Date;
import java.util.List;
import util.exception.GuestNotFoundException;

public interface RoomReservationSessionBeanLocal {

    public void remove();

    public void preDestroy();

    public List<ReservationLineItem> searchHotelRoom(Date checkInDate, Date checkOutDate);

    public Long getTotalAmount(ReservationLineItem reservationLineItem);

    public OnlineReservation reserveRoom(String email, ReservationLineItem reservationRoom) throws GuestNotFoundException;
    
    public List<ReservationLineItem> walkInSearchHotelRoom(Date checkInDate, Date checkOutDate);
}
