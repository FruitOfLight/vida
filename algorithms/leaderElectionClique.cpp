/*
 anonym no
 graphType clique
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

int maximalID,myID,freePorts;

void recieve(int port, string message) {
    int ID = strToInt(message);
    if(getIValue("maximalID")<ID) {
        setIValue("maximalID",ID);
        char inf[100];
        sprintf(inf,"I've recieved bigger ID, leader will be: %d",ID);
        sendInformation(string(inf));
        setSValue("_vertex_color","200,125,0");
    }
    freePorts--;
    if(freePorts==0)
    {
        char inf[100];
        sprintf(inf,"Leader has ID: %d, God bless king",getIValue("maximalID"));
        sendInformation(string(inf));
        char color[100];
        if(myID == getIValue("maximalID"))
            setSValue("_vertex_color","50,50,255");
        else
            setSValue("_vertex_color","255,0,0");
    }
}

void init(){
    freePorts = ports.size();
    myID = id;
    setIValue("maximalID",myID);
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
