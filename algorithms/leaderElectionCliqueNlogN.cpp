/*
 anonym no
 graph clique
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"
#include <pthread.h>

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

int maximalID,myID,freePorts;
bool cakam;
int nakoho=-1;

void recieve(int port, string message) {
    sendInformation("tu som");
    if(port==nakoho) cakam=false;
    else {
        sendMessage(port,"na");
    }
    return ;
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
}

void *code(void *data) {
    pthread_mutex_lock(&mutex);
    char buffer[100];
    sprintf(buffer,"I have ID: {%d}",myID);
    sendMessage(ports[0],string(buffer));
    cakam = true; nakoho = ports[0];
    pthread_mutex_unlock(&mutex);
    while(cakam) {}
    sendInformation("Dockal som sa.");
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    pthread_t thread1;
    pthread_t thread2;
    pthread_create(&thread1,NULL,run,NULL);
    pthread_create(&thread2,NULL,code,NULL);

    
    pthread_join(thread1,NULL);
    pthread_join(thread2,NULL);
    return 0;
}
