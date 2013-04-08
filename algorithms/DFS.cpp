/*
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

int T[150];
int wait;

void recieve(int port, string message) {
    if(getSValue("discovered")=="true" && wait!=port) {
        For(i,ports.size()) if(ports[i]==port) T[i]=-1;
        sendInformation("I have been already discovered, lets return token.");
        sendMessage(port, "Here is {token}");
        return;
    }
   setSValue("discovered","true");
   if(wait!=port) For(i,ports.size()) if(ports[i]==port) T[i]=1;
   setSValue("_vertex_color","255,100,255");
   char inf[100];
   sprintf(inf,"I have token, lets find unuesed edge");
   sendInformation(string(inf));
   int p=-1;
   For(i,ports.size()) if(T[i]==0) p=i;
   if(p==-1) {
       For(i,ports.size()) if(T[i]==1) p=i;
       if(p!=-1) {
            char inf[100];
            sprintf(inf,"All my edges are used, send token to parent.");
            sendInformation(string(inf));
            sendMessage(ports[p], "Here is {token}");
       }
       else {
            char inf[100];
            sprintf(inf,"All vertex are discovered.");
            sendInformation(string(inf));
       }
       exitProgram("true");
       return ;
   }
   T[p]=-1;
   wait = ports[p];
   char buff[100];
   sprintf(buff,"Here is {token}.");
   sendMessage(ports[p],string(buff));
}

void init() {
    wait = -1;
   int initValue = initvalue;
   For(i,ports.size()) T[i]=0;
   setSValue("discovered","false");
   if(initValue == 0) return;
   setSValue("discovered","true");
   setSValue("_vertex_color","255,100,255");
   char inf[100];
   sprintf(inf,"I have token, lets find unuesed edge");
   sendInformation(string(inf));
   char buff[100];
   sprintf(buff,"Here is {token}.");
   for(int i=0; i<ports.size(); i++) {
       if(T[i]==0) {
           T[i]=-1;
           wait = ports[i];
           sendMessage(ports[i],string(buff));
           return; 
       }
   }
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
