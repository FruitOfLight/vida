/*
 anonym no
 graphType clique
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

void recieveCapture(int port, pair<int,int> strength) {
    if(getSValue("state")!="captured" && strength > make_pair(getIValue("level"),id)) {
        tell(strprintf("I'm captured by ID %d with level %d",strength.second,strength.first));
        setSValue("state","captured");
        setIValue("parent",strength.second);
        setIValue("_parent_port",port);
        setSValue("leader","no");
        setSValue("_vertex_color","255,0,0");
        event(strprintf("capture-active:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second));
        exitProgram("false");
        sendMessage(port, "{accept}");
    }
    else if(getSValue("state")=="captured") {
        tell(strprintf("I need help from my leader"));
        event(strprintf("capture-capture:%d:%d:%d:%d",strength.first,strength.second,id,getIValue("parent")));
        sendMessage(getIValue("_parent_port"),strprintf("{help %d,%d} %d",strength.first,strength.second,port));
    }
}

void recieveAccept(int port) {
    if(getSValue("state")!="active") return;
    setIValue("level",getIValue("level")+1);
    tell(strprintf("I get subordinate, my actual level is %d",getIValue("level")));
    update(strprintf("level:%d",getIValue("level")));
    event(strprintf("accept:%d:%d",id,getIValue("level")));
    setSValue("_vertex_size",strprintf("%d",100+50*getIValue("level")));
    if(getIValue("level")==ports.size()) {
        setSValue("_vertex_color","0,0,255");
        setSValue("leader","yes");
        tell("I'm the leader");
        exitProgram("true");
        return;
    }
    wait = getIValue("level");
    sendMessage(ports[getIValue("level")],strprintf("{capture %d,%d}",getIValue("level"),id));
}

void recieveHelp(int port, pair<int,int> strength, int port1) {
    if(strength < make_pair(getIValue("level"),id)) {
        tell("We won");
        event(strprintf("help-win:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second));
        sendMessage(port,"{victory}");
        return ;
    }
    if(getSValue("state") == "active") setSValue("state","killed");
    setSValue("leader","no");
    setSValue("_vertex_color","255,0,0");
    tell("I'm defeated");
    event(strprintf("help-defeat:%d:%d:%d:%d",getIValue("level"),id,strength.first,strength.second));
    exitProgram("false");
    sendMessage(port,strprintf("{defeat} %d,%d",port1,strength.second));
}

void recieveDefeat(int port, int port1, int newParent) {
    tell(strprintf("I'm captured"));
    event(strprintf("defeat:%d:%d",id,newParent));
    setIValue("parent",newParent);
    setIValue("_parent_port",port1);
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
    setIValue("_parent_port",-1);
    wait = 0;
    sendMessage(ports[0],strprintf("{capture %d,%d}",getIValue("level"),id));
}

int main(){

    setInitListener(init);
    setMessageListener(recieve);

    run();
    return 0;
}
