//
// Created by inbar on 01-Jan-21.
//

#include "../include/Tasks.h"
#include "../include/Messages.h"

using namespace std;


// ========== CONSTRUCTOR
Tasks::Tasks(ConnectionHandler &connectionHandler) : handler(connectionHandler) {

}


void Tasks::run() {
    while (!handler.ShouldTerminate()) {

        string msgFromServer;
        if (!handler.getLine(msgFromServer)) {
            cout << "Disconnected. Exiting...\n" << endl;
            break;
        }

        MessageFromServer message (msgFromServer);
        string result = message.process();
        if (result=="terminate") {
            handler.setCanContinue("no");
            handler.Terminate();
        }
        else if (result == "continue") {
            handler.setCanContinue("yes");
        }
        else
            handler.setCanContinue("");
    }

    handler.close();
}


