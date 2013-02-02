/*
 anonym no
 graph cycle
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
        if(getValue("boss")==-1) {
            char inf[100];
            sprintf(inf,"I wouldn't be boss.");
            sendInformation(string(inf));
            setValue("boss",0);
            char color[100];
            sprintf(color,"255,0,0");
            sendVertexColorChange(string(color));
        }
        int to;
        for(int i=0; i<ports.size(); i++)
            if(port!=ports[i]) to=ports[i];
        char mess[100];
        sprintf(mess,"I have ID: {%d}",ID);
        sendMessage(to,string(mess));
    }
    else if(myID==ID) {
        char inf[100];
        sprintf(inf,"I am boss.");
        sendInformation(string(inf));
        setValue("boss",1);
    }
}

void init(){
    freePorts = ports.size();
    myID = id;
    setValue("boss",-1);
    setValue("maximalID",myID);
    char buffer[100];
    sprintf(buffer,"I have ID: {%d}",myID);
    sendMessage(ports[0],string(buffer));
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
