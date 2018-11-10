/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import javax.ejb.EJB;

/**
 *
 * @author zhu yixin
 */
public class Main {

    @EJB
    private static RoomTypeControllerRemote roomTypeControllerRemote;

    @EJB
    private static RoomRateControllerRemote roomRateControllerRemote;

    @EJB
    private static RoomControllerRemote roomControllerRemote;

    @EJB
    private static GuestControllerRemote guestControllerRemote;

    
    public static void main(String[] args) {
        MainApp mainApp=new MainApp(guestControllerRemote,roomControllerRemote,roomRateControllerRemote,roomTypeControllerRemote);
        mainApp.runApp();
    }
    
}
