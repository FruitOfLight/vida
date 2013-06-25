/*
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"
#include<unistd.h>

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

void receive(int port, string message) {
    if(getSValue("information")=="known") {
        tell("I already know this gossip.");
        event(strprintf("old:%d",id));
        return;
    }
   setSValue("information","known");
   setSValue("_vertex_color","100,200,255");
   tell("I received new gossip, I must tell everyone.");
   event(strprintf("receive:%d",id));
   exitProgram("true");
   for(int i=0; i<ports.size(); i++) {
       if(ports[i]==port) continue;
       sendMessage(ports[i],"I have a new {gossip}");
   }
}

void init() {
   int initValue = initvalue;
   setSValue("information","unknown");
   if(initValue == 0) return;
   setSValue("information","known");
   setSValue("_vertex_color","100,200,255");
   tell("I received new gossip, I must tell everyone.");
   exitProgram("true");
   for(int i=0; i<ports.size(); i++) {
       sendMessage(ports[i],"I have a new {gossip}");
   }
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(receive);

    run();
}
