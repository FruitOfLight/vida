/*
    anonym: no
    graph type: [any]
*/

//{{{Fruit of Light
//Apple Strawberry

#include"vidalib.h"
#include <iostream>
#include <stdlib.h>
#include <stdio.h>

using namespace Messager;
using namespace Property;
using namespace WatchVariables;

typedef pair<int, int> Value;
typedef pair<Value, int> Edge;

#define INF 1023456789
const Value INFV = Value(INF,INF);
//}}}

priority_queue<Edge, vector<Edge>, greater<Edge>> unknown;
priority_queue<Edge, vector<Edge>, greater<Edge>> waitingRequests;
map<int, Edge> edges;

vector<Edge> inside, sons;
Edge parent = Edge(INFV,INF);

int waitForSons = -1;
Edge cheapest = Edge(INFV,INF);

// function declarations

void receiveID(int port, istringstream& iss);

void findCheapest();
void foundCheapest();
void findCheapestUnknown();
void receiveCheapest(int port, istringstream& iss);
void receiveIAm(int port, istringstream& iss);

void discover();
void receiveDiscover(int port, istringstream& iss);

void sendRebuild(int port);
void receiveRebuild(int port, istringstream& iss);

//{{{ initiation 
void receiveID(int port, istringstream& iss){
    if (getSValue("state")!="stupid"){
        tell("error");
        return;
    }
    int hisID;
    iss >> hisID;
    unknown.push(edges[port] = Edge(Value(min(hisID,id), max(hisID,id)), port));
    if (unknown.size()==ports.size()){
        setIValue("level", 0);
        setSValue("state", "head");
        setSValue("doing", "finding cheapest edge");

        tell("I know all neighbors");
        tell("I am head of fragment");

        findCheapest();
    }
} 
//}}}

//{{{ finding cheapest
void findCheapest(){
    tell("Let's find cheapest edge");
    if (waitForSons>=0){
        tell("error");
    }
    waitForSons = sons.size();
    Edge cheapest = Edge(INFV,INF);
    For(i,sons.size()){
        sendMessage(sons[i].second,"{find-cheapest}");
    }
}
void foundCheapest(){
    tell(strprintf("Cheapest edge found: <%d %d> %d",
                cheapest.first.first, cheapest.first.second, cheapest.second));

    if (parent.second==INF){
        // TODO
    }else{
        sendMessage(parent.second,strprintf("my-cheapest %d %d",
                    cheapest.first.first, cheapest.first.second));
    }
}
void findCheapestUnknown(){
    if(unknown.size()>0 && unknown.top()<cheapest){
        sendMessage(unknown.top().second,"{who}");
    }else{
        foundCheapest();
    }
}
void receiveCheapest(int port, istringstream& iss){
    int a,b;
    iss >> a >> b;
    Edge e = Edge(Value(a,b), port);
    cheapest = min(cheapest,e);
}
void receiveIAm(int port, istringstream& iss){
    int hisBossID;
    iss >> hisBossID;
    if (hisBossID==getIValue("boss-id")){
        inside.push_back(unknown.top());
        unknown.pop();
        findCheapestUnknown();
    }else{
        cheapest = min(cheapest,unknown.top());
        foundCheapest();
    }
}
//}}} 


//{{{ discovery
void discover(){
    int port = cheapest.second;
    sendMessage(port,strprintf("{discovery} %d %d", getIValue("level"), getIValue("boss-id")));
}
void receiveDiscover(int port, istringstream& iss){
    int hisLevel, myLevel;
    iss >> hisLevel;
    myLevel = getIValue("level");
    if (myLevel>hisLevel){
        tell("I win, let's eat his fragment");
        sendRebuild(port);
        return;            
    }
    if (myLevel<=hisLevel){
        waitingRequests.push(edges[port]);
        return;
    }
}
void chceckWaiting(){
   // TODO 
}
//}}}

//{{{ rebuild
void sendRebuild(int port){
    sendMessage(port, strprintf("{rebuild} %d %d", getIValue("level"), getIValue("boss-id")));
}
void receiveRebuild(int port, istringstream& iss){
    int hisLevel, hisBoss;
    iss >> hisLevel >> hisBoss;
    setIValue("_parent_port", port);
    setIValue("level", hisLevel);
    setIValue("boss-id", hisBoss);
    setSValue("state", "slave");
    setSValue("doing", "forwarding messages");
    sons.push_back(parent);
    parent = edges[port];
    tell(strprintf("My new boss is %d", getIValue("boss-id")));
    For(i, sons.size()) {
        sendRebuild(sons[i].second);
    }
}

//}}}

void receive(int port, string message) { //{{{
    For(i, message.size()){
        if (message[i]=='{' || message[i]=='}')
            message[i] = ' ';
    }
    istringstream iss(message);
    string s;
    iss >> s;
    if (s=="id") receiveID(port, iss);
    if (s=="find-cheapest") findCheapest();
    if (s=="my-cheapest") receiveCheapest(port, iss);
    if (s=="who") sendMessage(port, strprintf("{i-am} %d", getIValue("boss-id")));
    if (s=="i-am") receiveIAm(port, iss);
    if (s=="discovery") receiveDiscover(port,iss);
    if (s=="rebuild") receiveRebuild(port,iss);
    if (waitForSons==0){
        waitForSons--;
        findCheapestUnknown();
    }
}//}}}

void init(){ //{{{
    setIValue("level", -1); //level fragmentu
    setIValue("boss-id",id); // sef fragmentu
    setIValue("_parent_port", -1); //ktorym smerom je kostrovy otec 
    setSValue("state","stupid");
    setSValue("doing","waiting for ids");

    tell("Let's send my id to everyone");
    For(i,ports.size()){
        sendMessage(ports[i], strprintf("{id} %d", id));
    }
}//}}}

int main(){ //{{{
    setInitListener(init);
    setMessageListener(receive);
    
    run();
    return 0;
} //}}}
