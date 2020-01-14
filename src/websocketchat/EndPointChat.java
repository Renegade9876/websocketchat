package websocketchat;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/* WebSocket  */
@ServerEndpoint("/endpointchat")
public class EndPointChat {

    private static final Logger logger = Logger.getLogger("EndPointChat");
	
    /* Queue for all open WebSocket sessions */
    static Queue<Session> queue = new ConcurrentLinkedQueue<>();
	
    /* PriceVolumeBean calls this method to send updates */
    public static void enviarMensajeDesdeEndPointChatAlCliente(String mensaje, String idSession) {
    	
    	System.out.println("EndPointChat___________ public static void enviarMensajeDesdeEndPointChatAlCliente!"+mensaje);
    	
        try {
            /* Send updates to all open WebSocket sessions */
            for (Session session : queue) {
                session.getBasicRemote().sendText("[Usuario # "+idSession+"]:" + mensaje);
                System.out.println("Enviado MensajeDesdeEndPointChatAlCliente [Usuario # "+idSession+"]: "+mensaje);
            }
        } catch (IOException e) {
            System.out.println("EndPointChat: IOException:"+e.getMessage());
        }
    }
    
    @OnMessage
    public void enviarMensaje(String message, Session session) {
    	
    	System.out.println("EndPointChat____________ @OnOpen openConnection: Conexión abierta Usuario # " + session.getId());
    	
    	enviarMensajeDesdeEndPointChatAlCliente(message, session.getId());
    }
    
    @OnOpen
    public void openConnection(Session session) {
        
    	/* Register this connection in the queue */
        queue.add(session);

    	System.out.println("EndPointChat____________ @OnOpen openConnection: Conexión abierta Usuario # " + session.getId());
    	
    	enviarMensajeDesdeEndPointChatAlCliente("Abrió la conexión", session.getId());
    }
    
    @OnClose
    public void closedConnection(Session session) {
    	
        /* Remove this connection from the queue */
        queue.remove(session);
        
    	System.out.println("EndPointChat____________ @OnClose closedConnection: Conexión cerrada Usuario # " + session.getId());
        
    	enviarMensajeDesdeEndPointChatAlCliente("Cerró la conexión", session.getId());
    }
    
    @OnError
    public void errorConnection(Session session, Throwable t) {
        /* Remove this connection from the queue */
        queue.remove(session);
        
    	System.out.println("EndPointChat____________ @OnError errorConnection: Conexión cerrada Usuario # " + session.getId());
        
    	enviarMensajeDesdeEndPointChatAlCliente("Error en la conexión: " + t.toString(), session.getId());
    }
	
}
