/*
 anonym no
 graph clique
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;

int maximalID,myID,freePorts;

void recieve(int port, string message) {
    int ID = 0;
    for(int i=0; i<message.length(); i++) {
        if(message[i]>='0' && message[i]<='9') {ID*=10; ID+=message[i]-'0';}
    }
    if(maximalID<ID) {
        maximalID=ID;
        char inf[100];
        sprintf(inf,"I recieve bigger ID, leader will be: %d",maximalID);
        sendInformation(string(inf));
    }
    freePorts--;
    if(freePorts==0)
    {
        char inf[100];
        sprintf(inf,"Leader have ID: %d, God bless king",maximalID);
        sendInformation(string(inf));
    }
}

void init(){
    freePorts = ports.size();
    char inf[100];
    sprintf(inf,"My ID: %d",myID);
    sendInformation(string(inf));
    for(int i=0; i<ports.size(); i++)
    {
        char buffer[100];
        sprintf(buffer,"I have ID: {%d}",myID);
        sendMessage(ports[i],string(buffer));
    }
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;
    myID = randid;
    maximalID = myID;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
