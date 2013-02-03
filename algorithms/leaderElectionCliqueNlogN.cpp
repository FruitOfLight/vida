/*
 anonym no
 graph clique
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

int wait,conquerer;

void recieveCapture(int port, pair<int,int> strength) {
    if(getValue("state")!=1 && strength>make_pair(getValue("level"),id)) {
        char inf[100];
        sprintf(inf,"I'm captured by ID %d with level %d",strength.second,strength.first);
        sendInformation(string(inf));
        setValue("state",1);
        setValue("parent",port);
        setValue("leader",0);
        sendVertexColorChange("255,0,0");
        sendMessage(port,"accept");
    }
    else if(getValue("state")==1) {
        char inf[100];
        sprintf(inf,"I need help from my boss");
        sendInformation(string(inf));
        char buff[100];
        sprintf(buff,"help {%d,%d} %d,",strength.first,strength.second,port);
        sendMessage(getValue("parent"),string(buff));
    }
}

void recieveHelp(int port, pair<int,int> strength, int port1) {
    if(strength<make_pair(getValue("level"),id)) {
        sendInformation("We won");
        sendMessage(port,"victory");
        return;
    }
    if(getValue("state")==2) setValue("state",0);
    char buff[100];
    sendInformation("I'm defeated");
    sprintf(buff,"defeat {%d}",port1);
    sendMessage(port,string(buff));
}

void recieveAccept(int port) {
    if(getValue("state")!=2) return ;
    setValue("level",getValue("level")+1);
    char inf[100];
    sprintf(inf,"I get subordinate, my actual level is %d",getValue("level"));
    int p=getValue("level");
    vector<char> A;
    if(p==0) A.push_back('0');
    char num[100];
    sprintf(num,"%d",getValue("level"));
    sendVertexRadiusChange(string(num));
    if(getValue("level")==ports.size()) {
        sendInformation("I'm the leader");
        return ;
    }
    char buffer[100];
    sprintf(buffer,"capture {%d,%d}",getValue("level"),id);
    wait = getValue("level");
    sendMessage(ports[getValue("level")],string(buffer));
}

void recieveDefeat(int port, int port1) {
    char inf[100];
    sprintf(inf,"I'm captured");
    sendInformation(string(inf));
    setValue("parent",port1);
    sendMessage(port1,"accept");
}

void recieve(int port, string message) {
    string s = "";
    int p=0;
    while(p!=message.length() && message[p]!=' ') {s+=message[p]; p++;}
    if(s=="capture") {
        pair<int,int> strength=make_pair(0,0);
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
        int p1 = 0;
        for(int i=0; i<message.length(); i++)
            if(message[i]>='0' && message[i]<='9') {p1*=10; p1+=message[i]-'0';}
        recieveDefeat(port,p1);
    }
    return ;
}

void init(){
    setValue("level",0);
    setValue("parent",-1);
    setValue("leader",-1);
    setValue("state",2);
    char buffer[100];
    sprintf(buffer,"capture {%d,%d}",getValue("level"),id);
    wait = 0;
    sendMessage(ports[0],string(buffer));
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
    return 0;
}
