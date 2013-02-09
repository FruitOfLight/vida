/*
 anonym no
 graphType cycle
 blablabloblo mehehe
 mehehe blablabloblo
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
    if(getIValue("maximalID")<ID) {
        setIValue("maximalID",ID);
        if(getIValue("boss")==-1) {
            char inf[100];
            sprintf(inf,"I wouldn't be boss.");
            sendInformation(string(inf));
            setIValue("boss",0);
            char color[100];
            setSValue("_vertex_color","255,0,0");
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
        setIValue("boss",1);
        setSValue("_vertex_color","0,0,255");
    }
}

void init(){
    freePorts = ports.size();
    myID = id;
    setIValue("boss",-1);
    setIValue("maximalID",myID);
    char buffer[100];
    sprintf(buffer,"I have ID: {%d}",myID);
    sendMessage(ports[0],string(buffer));
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
