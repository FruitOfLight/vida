/*
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

vector<int> A;

void recieve(int port, string message) {
    if(message[0]=='{') {
        int cis=0;
        int p=1;
        while(message[p]!='}') {cis*=10; cis+=message[p]-'0'; p++;}
        For(i,ports.size()) if(ports[i]==port) A[i]=cis;
        if(ports.size()>1) return;
        if(A[0]>id) return;
        sendInformation("Dakujem",10);
        sendMessage(ports[0],"za pozornost :-) {}");
        return;
    }
    string co = "";
    int p=0;
    while(message[p]!=' ') {co+=message[p]; p++;}
    string dal = "";
    for(int i=p+1; i<message.length(); i++) dal+=message[i];
    int kam=-1;
    for(int i=0; i<ports.size(); i++) if(id>A[i]) kam=ports[i];
    sendInformation(co,10);
    if(kam!=-1) sendMessage(kam,dal);
}

void init() {
    A.resize(ports.size());
    char buff[100];
    sprintf(buff,"{%d}",id);
    for(int i=0; i<ports.size(); i++) 
            sendMessage(ports[i],string(buff));
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
