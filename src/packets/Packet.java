package packets;

import java.io.Serializable;

public interface Packet extends Serializable {

    static final long serialVersionUID = 1L;

    /*
     * Server: packets sent from server to client exclusively
     * Client: packets sent from client to server exclusively
     */

}
