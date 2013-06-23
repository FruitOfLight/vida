#include"vidalib.h"
using namespace Messager;
using namespace Property;
using namespace WatchVariables;

void recieve(int port, string message) {
   setSValue("information","known");
   setSValue("_vertex_color","100,200,255");
   sendInformation("I recieved new gossip");
   exitProgram("true");
   sendMessage(ports[1],"I have new {gossip}");
}
void init() {
   if(initvalue == 0) return;
   for(int i=0; i<ports.size(); i++) {
       sendMessage(ports[i],"I have new {gossip}");
   }
}
int main(){
    setInitListener(init);
    setMessageListener(recieve);
    run();
}
