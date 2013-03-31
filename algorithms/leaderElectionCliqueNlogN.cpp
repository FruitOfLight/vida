/*
 anonym no
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"
#include <iostream>
#include <stdlib.h>
#include <stdio.h>

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

int wait;
int parentPort;

void recieveCapture(int port, pair<int,int> strength) {
    if(getSValue("state")!="captured" && strength > make_pair(getIValue("level"),id)) {
        char inf[100];
        sprintf(inf,"I'm captured by ID %d with level %d",strength.second,strength.first);
        sendInformation(string(inf));
        setSValue("state","captured");
        setIValue("parent",strength.second);
        parentPort = port;
        setSValue("leader","no");
        setSValue("_vertex_color","255,0,0");
        char up[100];
        sprintf(up,"capture-active:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second);
        algorithmUpdate(string(up));
        sendMessage(port, "{accept}");
    }
    else if(getSValue("state")=="captured") {
        char inf[100];
        sprintf(inf,"I need help from my leader");
        sendInformation(string(inf));
        char up[100];
        sprintf(up,"capture-capture:%d:%d:%d:%d",strength.first,strength.second,id,getIValue("parent"));
        algorithmUpdate(string(up));
        char buff[100];
        sprintf(buff,"{help %d,%d} %d",strength.first,strength.second,port);
        sendMessage(parentPort,string(buff));
    }
}

void recieveAccept(int port) {
    if(getSValue("state")!="active") return;
    setIValue("level",getIValue("level")+1);
    char inf[100];
    sprintf(inf,"I get subordinate, my actual level is %d",getIValue("level"));
    sendInformation(string(inf));
    char up[100];
    sprintf(up,"level:%d",getIValue("level"));
    algorithmUpdate(up);
    sprintf(up,"accept:%d:%d",id,getIValue("level"));
    algorithmUpdate(up);
    char num[100];
    sprintf(num,"%d",100+50*getIValue("level"));
    setSValue("_vertex_size",string(num));
    if(getIValue("level")==ports.size()) {
        setSValue("_vertex_color","0,0,255");
        setSValue("leader","yes");
        sendInformation("I'm the leader");
        return;
    }
    char buff[100];
    sprintf(buff,"{capture %d,%d}",getIValue("level"),id);
    wait = getIValue("level");
    sendMessage(ports[getIValue("level")],string(buff));
}

void recieveHelp(int port, pair<int,int> strength, int port1) {
    if(strength < make_pair(getIValue("level"),id)) {
        sendInformation("We won");
        char up[100];
        sprintf(up,"help-win:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second);
        algorithmUpdate(string(up));
        sendMessage(port,"{victory}");
        return ;
    }
    if(getSValue("state") == "active") setSValue("state","killed");
    setSValue("leader","no");
    setSValue("_vertex_color","255,0,0");
    char buff[100];
    sendInformation("I'm defeated");
    char up[100];
    sprintf(up,"help-defeat:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second);
    algorithmUpdate(string(up));
    sprintf(buff,"{defeat} %d,%d",port1,strength.second);
    sendMessage(port,string(buff));
}

void recieveDefeat(int port, int port1, int newParent) {
    char inf[100];
    sprintf(inf,"I'm captured");
    sendInformation(string(inf));
    char up[100];
    sprintf(up,"Defeat:%d:%d",id,newParent);
    algorithmUpdate(up);
    setIValue("parent",newParent);
    parentPort = port1;
    sendMessage(port1,"{accept}");
}

void recieve(int port, string message) {
    string s="";
    int p=1;
    while(message[p]!='}' && message[p]!=' ') {s+=message[p]; p++;}
    if(s=="capture") {
        pair<int,int> strength = make_pair(0,0);
        while(message[p]!=',') {
            if(message[p]>='0' && message[p]<='9') {strength.first*=10; strength.first+=message[p]-'0';}
            p++;
        }
        while(p!=message.length() && message[p]!='}') {
            if(message[p]>='0' && message[p]<='9') {strength.second*=10; strength.second+=message[p]-'0';}
            p++;
        }
        recieveCapture(port,strength);
    }
    else if(s[0]=='a' && s[1]=='c') recieveAccept(port);
    else if(s=="help") {
        pair<int,int> strength=make_pair(0,0);
        int port1=0;
        while(message[p]!=',') {
            if(message[p]>='0' && message[p]<='9') {strength.first*=10; strength.first+=message[p]-'0';}
            p++;
        }
        while(p!=message.length() && message[p]!='}') {
            if(message[p]>='0' && message[p]<='9') {strength.second*=10; strength.second+=message[p]-'0';}
            p++;
        }
        while(p!=message.length()) {
            if(message[p]>='0' && message[p]<='9') {port1*=10; port1+=message[p]-'0';}
            p++;
        }
        recieveHelp(port,strength,port1);
    }
    else if(s=="defeat") {
        int p1 = 0, id = 0;
        p=0;
        while(message[p]!=',') {
            if(message[p]>='0' && message[p]<='9') {p1*=10; p1+=message[p]-'0';}
            p++;
        }
        while(p!=message.length()) {
            if(message[p]>='0' && message[p]<='9') {id*=10; id+=message[p]-'0';}
            p++;
        }
        recieveDefeat(port,p1,id);
    }
    return;
}

void init(){
    setIValue("level",0);
    setIValue("parent",-1);
    setSValue("leader","maybe");
    setSValue("state","active");
    parentPort = -1;
    char buffer[100];
    sprintf(buffer,"{capture %d,%d}",getIValue("level"),id);
    wait = 0;
    sendMessage(ports[0],string(buffer));
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
    return 0;
}
