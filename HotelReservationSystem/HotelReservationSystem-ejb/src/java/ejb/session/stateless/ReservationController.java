/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.ExceptionReport;
import entity.Guest;
import entity.NormalRate;
import entity.OnlineReservation;
import entity.Partner;
import entity.PartnerReservation;
import entity.PeakRate;
import entity.PromotionRate;
import entity.PublishedRate;
import entity.ReservationLineItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import entity.WalkInReservation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;
import util.exception.GuestNotFoundException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author casseylow
 */
@Stateless
@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;
    @EJB
    private EmployeeControllerLocal employeeControllerLocal;
    @EJB
    private GuestControllerLocal guestControllerLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    
    public ReservationController()
    {
        
    }
    
    @Override
    public List<ReservationLineItem> retrieveReservationLineItemByCheckInDate(Date checkInDate)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.checkInDate = :inCheckInDate");
        query.setParameter("inCheckInDate", checkInDate);
        return query.getResultList();
    }
    
    public List<ReservationLineItem> retrieveReservationLineItemByCheckOutDate(Date checkOutDate)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.checkOutDate = :inCheckOutDate");
        query.setParameter("inCheckOutDate", checkOutDate); //reservationlineitem is due for check out today
        return query.getResultList();
    }
    
    @Override
    public List<ReservationLineItem> retrieveReservationLineItemByRoomType(Long roomTypeId)
    {
        Query query = em.createQuery("SELECT rli FROM ReservationLineItem rli WHERE rli.roomType = :inRoomType");
        query.setParameter("inRoomType", em.find(RoomType.class, roomTypeId));
        
        return query.getResultList();
    }
    
    @Override
    public ReservationLineItem retrieveReservationLineItemById(Long reservationLineItemId) throws ReservationLineItemNotFoundException
    {
        ReservationLineItem resLineItem = em.find(ReservationLineItem.class, reservationLineItemId);
        
        if(resLineItem != null)
        {
            return resLineItem;
        }
        else
        {
            throw new ReservationLineItemNotFoundException("Reservation Line Item " + resLineItem + " does not exist!");
        }
    }
    
    @Override
    public OnlineReservation createOnlineReservation(Guest guest)
    {
       OnlineReservation onlineReservation=new OnlineReservation();
       em.persist(onlineReservation);
       guest.getOnlineReservations().add(onlineReservation);
       em.flush();;
       
       return onlineReservation;
    }
    
    @Override
     public PartnerReservation createPartnerReservation(Partner partner)
    {
       PartnerReservation partnerReservation=new PartnerReservation();
       em.persist(partnerReservation);
       partner.getPartnerReservations().add(partnerReservation);
       em.flush();
       
       return partnerReservation;
    }

    public WalkInReservation createWalkInReservation(WalkInReservation newWalkInReservation, Long employeeId) throws EmployeeNotFoundException
    {
        Date todayDate = new Date();
        Employee employee = employeeControllerLocal.retrieveEmployeeByEmployeeId(employeeId);
        newWalkInReservation.setEmployee(employee);
        newWalkInReservation.setReservationDate(todayDate);
        
        em.persist(newWalkInReservation);
        em.flush();

        return newWalkInReservation;
    }
    
    @Override
    public OnlineReservation createOnlineReservation(OnlineReservation newOnlineReservation, Long guestId) throws GuestNotFoundException
    {
        Date todayDate = new Date();
        Guest guest = guestControllerLocal.retrieveGuestById(guestId);
        newOnlineReservation.setGuest(guest);
        newOnlineReservation.setReservationDate(todayDate);
        
        em.persist(newOnlineReservation);
        em.flush();

        return newOnlineReservation;
    }
    
    @Override
    public ReservationLineItem createReservationLineItem(Date checkInDate,Date checkOutDate,String roomType)throws RoomTypeNotFoundException
    {
        try {
            ReservationLineItem reservationLineItem=new ReservationLineItem();
            reservationLineItem.setCheckInDate(checkInDate);
            reservationLineItem.setCheckOutDate(checkOutDate);
            
            
            RoomType roomTypeItem=roomTypeControllerLocal.retrieveRoomTypeByName(roomType);
            reservationLineItem.setRoomType(roomTypeItem);
            
            Boolean normalRateInNeeded=false;
            Boolean promotionRateInNeeded=false;
            Boolean peakRateInNeeded=false;
            for(RoomRate roomRate:roomTypeItem.getRoomRates())
            {
                if(roomRate instanceof PromotionRate)
                {
                    promotionRateInNeeded=true;
                }
                else if(roomRate instanceof NormalRate)
                {
                    normalRateInNeeded=true;
                }
                else if(roomRate instanceof PeakRate)
                {
                    peakRateInNeeded=true;
                }
            }
            if(peakRateInNeeded&&promotionRateInNeeded&&normalRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PromotionRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(peakRateInNeeded&&promotionRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PeakRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(peakRateInNeeded&&normalRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PeakRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(normalRateInNeeded&&promotionRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PromotionRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(normalRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof NormalRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(promotionRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PromotionRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            else if(peakRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PeakRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            
            em.persist(reservationLineItem);
            em.flush();
            
            return reservationLineItem;
        } 
        catch (RoomTypeNotFoundException ex) {
            throw new RoomTypeNotFoundException("Unable to find the room type !");
        }
            
    }
    
    @Override
    public OnlineReservation retrieveOnlineReservationById(Long reservationId)
    {
        Query query=em.createQuery("SELECT o FROM OnlineReservation o WHERE o.reservationId=:inReservationId");
        query.setParameter("inReservationId", reservationId);
        
        return (OnlineReservation) query.getSingleResult();
    }
    
    @Override
    public List<OnlineReservation> retrieveAllOnlineReservationsByGuestId(Long guestId){
        Query query=em.createQuery("SELECT o FROM OnlineReservation o WHERE o.guest = :inGuest");
        query.setParameter("inGuest", guestId);
        return query.getResultList();
    }
    
    @Override
    public List<OnlineReservation> retrieveAllOnlineReservations()
    {
        Query query=em.createQuery("SELECT o FROM OnlineReservation o");
        
        return query.getResultList();
    }
    
    @Override
    public PartnerReservation retrievePartnerReservationById(Long reservationId)
    {
        Query query=em.createQuery("SELECT p FROM PartnerReservationp WHERE p.reservationId=:inReservationId");
        query.setParameter("inReservationId", reservationId);
        
        return (PartnerReservation) query.getSingleResult();
    }
    
    @Override
    public List<PartnerReservation> retrieveAllPartnerReservations()
    {
        Query query=em.createNamedQuery("SELECT p FROM PartnerReservation o");
        
        return query.getResultList();
    }
    
    @Override
    public ExceptionReport createExceptionReport(ExceptionReport exceptionReport){
        em.persist(exceptionReport);
        em.flush();
        return exceptionReport;
    }
    
    @Override
    public List<Room> allocateRoomToCurrentDayReservations(){
        Date todayDate = new Date();
        List<ReservationLineItem> reservationLineItemsCheckInToday = retrieveReservationLineItemByCheckInDate(todayDate);
        List<ReservationLineItem> reservationLineItemsCheckOutToday = retrieveReservationLineItemByCheckOutDate(todayDate);
        ExceptionReport exReport = new ExceptionReport();
        List<Room> roomsReserved = new ArrayList<>();
        List<Room> roomsAvailableForCheckIn = new ArrayList<>();
        
        //get reservation list for check in today
        for(ReservationLineItem reservationLineItem:reservationLineItemsCheckInToday)
        {
            //get rooms available
            List<Room> roomsFiltered = roomControllerLocal.retrieveAvailableRoomsByRoomType(reservationLineItem.getRoomType().getRoomTypeId());
            for(Room room: roomsFiltered){
                roomsAvailableForCheckIn.add(room);
            }
            //get rooms that are due for check out today
            if(!reservationLineItemsCheckOutToday.isEmpty()){
                
            }
            for(ReservationLineItem reservations:reservationLineItemsCheckOutToday){
                for(Room room:reservations.getRoomList()){
                    if(room.getRoomStatus().equals("occupied") && reservations.getRoomType() == reservationLineItem.getRoomType()){
                        roomsAvailableForCheckIn.add(room);
                    }
                }
            }
            List<Room> otherRooms = roomControllerLocal.retrieveAllAvailableRooms();
            
            //room available for reserved room type
            if(!roomsAvailableForCheckIn.isEmpty()){
                for(Room room:roomsAvailableForCheckIn){
                    if(room.getRoomStatus().equals("occupied")){
                        room.setRoomStatus("occupied reserved");
                    }else if(room.getRoomStatus().equals("available")){
                        room.setRoomStatus("reserved");
                    }
                    reservationLineItem.getRoomList().add(room);
                    roomsReserved.add(room);
                }
            }
            else if(otherRooms != null)
            {
                //no room of room type, upgrade available
                for(Room room:otherRooms){
                    if(room.getRoomStatus().equals("available") 
                            && room.getRoomType().getRoomRank() < reservationLineItem.getRoomType().getRoomRank())
                    {
                        room.setRoomStatus("reserved");
                        reservationLineItem.getRoomList().add(room);
                        roomsReserved.add(room);
                        //raise exception report
                        exReport.setDescription("No available room for reserved room type, upgrade to next higher room type available. Room " + room.getRoomNumber() + " allocated.");
                        createExceptionReport(exReport);
                    }
                    else 
                    {
                        //no room no upgrade
                        if(reservationLineItem.getRoomList() == null){
                            exReport.setDescription("No available room for reserved room type, no upgrade to next higher room type available. No room allocated.");
                            createExceptionReport(exReport);
                        }
                    }
                }
            }
            else 
            {
                //no room no upgrade
                if(reservationLineItem.getRoomList() == null){
                    exReport.setDescription("No available room for reserved room type, no upgrade to next higher room type available. No room allocated.");
                    createExceptionReport(exReport);
                }
            }
        }
        return roomsReserved;
    }
    
    @Override
    public ReservationLineItem createWalkInReservationLineItem(Date checkInDate,Date checkOutDate,String roomType)throws RoomTypeNotFoundException
    {
        try {
            ReservationLineItem reservationLineItem=new ReservationLineItem();
            reservationLineItem.setCheckInDate(checkInDate);
            reservationLineItem.setCheckOutDate(checkOutDate);
            
            
            RoomType roomTypeItem=roomTypeControllerLocal.retrieveRoomTypeByName(roomType);
            reservationLineItem.setRoomType(roomTypeItem);
            
            Boolean publishedRateInNeeded=false;
            for(RoomRate roomRate:roomTypeItem.getRoomRates())
            {
                if(roomRate instanceof PublishedRate)
                {
                    publishedRateInNeeded=true;
                }
            }
            if(publishedRateInNeeded)
            {
                for(RoomRate roomRate: roomTypeItem.getRoomRates())
                {
                    if(roomRate instanceof PublishedRate)
                    {
                        reservationLineItem.setRoomRate(roomRate);
                    }
                }
            }
            
            em.persist(reservationLineItem);
            em.flush();
            
            return reservationLineItem;
        } 
        catch (RoomTypeNotFoundException ex) {
            throw new RoomTypeNotFoundException("Unable to find the room type !");
        }
    }
    
    @Override
    public ReservationLineItem createRoomReservationLineItem(Date checkInDate, Date checkOutDate, Long roomTypeId, Long roomRateId) throws RoomTypeNotFoundException, RoomRateNotFoundException
    {
        try 
        {
            ReservationLineItem reservationLineItem=new ReservationLineItem();
            reservationLineItem.setCheckInDate(checkInDate);
            reservationLineItem.setCheckOutDate(checkOutDate);
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            reservationLineItem.setRoomType(roomType);
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(roomRateId, false);
            reservationLineItem.setRoomRate(roomRate);

            em.persist(reservationLineItem);
            em.flush();

            return reservationLineItem;
        }
        catch(RoomTypeNotFoundException ex)
        {
            throw new RoomTypeNotFoundException("Unable to create new reservation line item as the room type record does not exist");
        } 
        catch (RoomRateNotFoundException ex) 
        {
            throw new RoomRateNotFoundException("Unable to create new reservation line item as the room rate record does not exist");
        }
    }
    
    @Override
    public List<ReservationLineItem> retrieveAllReservationLineItem(OnlineReservation onlineReservation)
    {
        List<ReservationLineItem> reservationLineItems=new ArrayList<>();
        for(ReservationLineItem reservationLineItem:onlineReservation.getReservationLineItems())
        {
            reservationLineItems.add(reservationLineItem);
        }
        
        return reservationLineItems;
    }
    
    @Override
    public List<ReservationLineItem> retrieveAllReservationLineItems()
    {
        Query query=em.createQuery("SELECT rl FROM ReservationLineItem rl");
        
        return query.getResultList();
    }
    
}
