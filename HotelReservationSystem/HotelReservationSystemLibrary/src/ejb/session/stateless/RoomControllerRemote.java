/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ExceptionReport;
import entity.Room;
import java.util.List;
import util.exception.DeleteRoomException;
import util.exception.GeneralException;
import util.exception.ReservationLineItemNotFoundException;
import util.exception.RoomExistException;
import util.exception.RoomNotFoundException;
import util.exception.RoomTypeNotFoundException;

public interface RoomControllerRemote {

    public Room createRoom(Room newRoom, Long roomTypeId) throws RoomTypeNotFoundException, RoomExistException, GeneralException;

    public List<Room> retrieveAllRooms();

    public Room updateRoom(Room room, String roomTypeName, Long reservationLineItemId) throws RoomNotFoundException, RoomTypeNotFoundException, ReservationLineItemNotFoundException;

    public void deleteRoom(Integer roomNo) throws RoomNotFoundException, DeleteRoomException ;

    public List<Room> retrieveRoomByRoomType(Long roomTypeId);
    
    public List<ExceptionReport> generateRoomAllocationExceptionReport();
    
    public List<Room> retrieveAllAvailableRooms();
    
    public List<Room> retrieveAvailableRoomsByRoomType(Long roomTypeId);
    
    public void updateRoomListInRoomType(Integer roomNo) throws RoomNotFoundException;
    
    public Room retrieveRoomById(Integer roomNumber, Boolean fetchRoomType, Boolean fetchReservation) throws RoomNotFoundException;
}
