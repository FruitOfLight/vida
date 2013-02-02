/*
 anonym no
 graph clique
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

int maximalID,myID,freePorts;

void recieve(int port, string message) {
    int ID = 0;
    for(int i=0; i<message.length(); i++) {
        if(message[i]>='0' && message[i]<='9') {ID*=10; ID+=message[i]-'0';}
    }
    if(getValue("maximalID")<ID) {
        setValue("maximalID",ID);
        char inf[100];
        sprintf(inf,"I recieve bigger ID, leader will be: %d",ID);
        sendInformation(string(inf));
    }
    freePorts--;
    if(freePorts==0)
    {
        char inf[100];
        sprintf(inf,"Leader have ID: %d, God bless king",getValue("maximalID"));
        sendInformation(string(inf));
    }
}

void init(){
    freePorts = ports.size();
    myID = id;
    addVariable("maximalID",myID);
    for(int i=0; i<ports.size(); i++)
    {
        char buffer[100];
        sprintf(buffer,"I have ID: {%d}",myID);
        sendMessage(ports[i],string(buffer));
    }
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
