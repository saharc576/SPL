//
// Created by inbar on 01-Jan-21.
//

#include "../include/connectionHandler.h"
#include <mutex>
#include <thread>
#include "../include/Tasks.h"
#include "../include/Messages.h"


using namespace std;

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);


    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    Tasks t(connectionHandler);
    thread serverListenerThread(&Tasks::run, &t);

    // fetching first message
    string msgFromClient;
    getline(cin, msgFromClient);

    while (!connectionHandler.ShouldTerminate()) {
        MessageFromClient message(msgFromClient);
        string result = message.process();
        if (!connectionHandler.sendLine(result)) {
            cout << "Disconnected. Exiting...\n" << endl;
            break;
        }

        // if the msg received was logout, must check if it was acknowledged
        if (message.isLogoutMsg()) {
            while (connectionHandler.getCanContinue().empty())
                continue;

            if (connectionHandler.getCanContinue() == "no")
                    break;
            // nullify the flag
            connectionHandler.setCanContinue("");
        }

        // else, it was not an approved logout, continue
        getline(cin, msgFromClient);
    }

    serverListenerThread.join(); // let the threads finish together
    return 0;
}


