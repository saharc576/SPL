package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.CommandProtocol.CommandEncoderDecoder;
import bgu.spl.net.impl.CommandProtocol.CommandInvocationProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        Database.getInstance().initialize("./Courses.txt");

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                Integer.parseInt(args[0]), //port
                CommandInvocationProtocol::new, //protocol factory
                CommandEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
