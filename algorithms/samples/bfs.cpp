/*
  anonym: [yes]
  model type: broadcast
 */

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"
#include<unistd.h>

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

void recieve(int port, string message) {
    if(getSValue("information")=="known") {
        sendInformation("I already know this gossip.");
        char up[100];
        sprintf(up,"old:%d",id);
        algorithmUpdate(string(up));
        return;
    }
   setSValue("information","known");
   setSValue("_vertex_color","100,200,255");
   char inf[100];
   sprintf(inf,"I recieved new gossip, I must tell everyone.");
   sendInformation(string(inf));
   char up[100];
   sprintf(up,"recieve:%d",id);
   algorithmUpdate(string(up));
   exitProgram("true");
   char buff[100];
   sprintf(buff,"I have new {gossip}.");
   for(int i=0; i<ports.size(); i++) {
       if(ports[i]==port) continue;
       sendMessage(ports[i],string(buff));
   }
}

void init() {
   int initValue = initvalue;
   setSValue("information","unknown");
   if(initValue == 0) return;
   setSValue("information","known");
   setSValue("_vertex_color","100,200,255");
   char inf[100];
   sprintf(inf,"I know new gossip, must tell to everyone.");
   sendInformation(string(inf));
   exitProgram("true");
   char buff[100];
   sprintf(buff,"I have new {gossip}.");
   for(int i=0; i<ports.size(); i++) {
       sendMessage(ports[i],string(buff));
   }
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
