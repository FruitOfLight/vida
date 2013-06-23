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
typedef pair<Edge, int> Request;

#define INF 1023456789
const Value INFV = Value(INF,INF);
//}}}

priority_queue<Edge, vector<Edge>, greater<Edge>> unknown;
priority_queue<Request, vector<Request>, greater<Request>> waitingRequests;
map<int, Edge> edges;

set<Edge> sons;

Edge parent = Edge(INFV,INF);
bool cheapestIsActual;

int waitForSons = -1;
namespace my_details{
    Edge cheapest = Edge(INFV,INF);
}
void setCheapest(Edge e){
    my_details::cheapest = e;
    if (e.first==INFV){
        setSValue("cheapest-edge", "undef");
    }
    setSValue("cheapest-edge", strprintf("%s<%d, %d> %d",
                cheapestIsActual?"":"so far ",
                e.first.first, e.first.second, e.second));
}
Edge getCheapest(){
    return my_details::cheapest;
}

// function declarations

void receiveID(int port, istringstream& iss);

void resetCheapest();
void findCheapest();
void foundCheapest();
void findCheapestUnknown();
void receiveCheapest(int port, istringstream& iss);
void receiveIAm(int port, istringstream& iss);

void discover();
void receiveDiscover(int port, istringstream& iss);
void checkWaiting();

void eatFragment(int port);
void becomeNewBoss();
void sendRebuild(int port);
void receiveRebuild(int port, istringstream& iss);

void becomeLeader();
void spreadLeader();
void receiveLeader(int port, istringstream& iss);

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
        tell("I know all neighbors");
        becomeNewBoss();
    }
} 
//}}}

//{{{ finding cheapest
void resetCheapest(){
    cheapestIsActual = 0;
    setCheapest(Edge(INFV,INF));
}
void findCheapest(){
    tell("Let's find cheapest edge");
    if (waitForSons>=0){
        tell("error");
    }
    resetCheapest();
    waitForSons = sons.size();
    for(auto son : sons)
        sendMessage(son.second,"{find-cheapest}");
}
void foundCheapest(){
    if (getCheapest().first == INFV && parent.second==INF){
        becomeLeader();
        return;
    }
    tell(strprintf("Cheapest edge found: <%d %d> %d",
                getCheapest().first.first, getCheapest().first.second, getCheapest().second));
    cheapestIsActual = 1;
    if (parent.second==INF){
        discover();
        tell("I found cheapest edge in whole fragment");
        tell("Let's discover the next fragment");
    }else{
        sendMessage(parent.second,strprintf("{my-cheapest} %d %d",
                    getCheapest().first.first, getCheapest().first.second));
    }
}
void findCheapestUnknown(){
    while (unknown.size()>0 && (unknown.top()==parent))
        unknown.pop();
    if (unknown.size()>0 && sons.count(unknown.top())){
        waitForSons = 1;
        sendMessage(unknown.top().second,"{find-cheapest}");
        unknown.pop();
        return;
    }
    if(unknown.size()>0 && unknown.top()<getCheapest()){
        sendMessage(unknown.top().second,"{who}");
    }else{
        foundCheapest();
    }
}
void receiveCheapest(int port, istringstream& iss){
    int a,b;
    iss >> a >> b;
    waitForSons--;
    Edge e = Edge(Value(a,b), port);
    setCheapest(min(getCheapest(),e));
}
void receiveIAm(int port, istringstream& iss){
    int hisBossID;
    iss >> hisBossID;
    if (hisBossID==getIValue("boss-id")){
        unknown.pop();
        findCheapestUnknown();
    }else{
        setCheapest(min(getCheapest(),unknown.top()));
        foundCheapest();
    }
}
//}}} 

//{{{ discovery
void discover(){
    int port = getCheapest().second;
    sendMessage(port,strprintf("{discovery} %d %d", getIValue("level"), getIValue("boss-id")));
    setSValue("doing", "discovering");
}
void receiveDiscover(int port, istringstream& iss){
    int hisLevel, myLevel, hisBoss, myBoss;
    iss >> hisLevel >> hisBoss;
    myBoss = getIValue("boss-id");
    myLevel = getIValue("level");
    if (myBoss == hisBoss){
        discover();
        return;
    }
    waitingRequests.push(Request(edges[port],hisLevel));
}
void checkWaiting(){
    while (!waitingRequests.empty()) {
        setSValue("w-req", strprintf("(%d %d)",waitingRequests.top().first.second,
                    waitingRequests.top().second));
   
        Request r = waitingRequests.top();
        if (r.first == parent || sons.count(r.first)){
            waitingRequests.pop();
            continue;
        }
        if (getIValue("level") > r.second) {
            eatFragment(r.first.second);
            waitingRequests.pop();
            continue;
        }        
        if (getSValue("doing")!="discovering") return;
        if (unknown.top()!=getCheapest()) return;

        if (getCheapest() == r.first && id == getCheapest().first.first){
            sons.insert(unknown.top());
            unknown.pop();
            becomeNewBoss();
        }
        return;
    }
}
//}}}

//{{{ rebuild
void eatFragment(int port){
    tell("I win, let's eat his fragment");
    sons.insert(edges[port]);
    sendRebuild(port);
}
void becomeNewBoss(){
    setIValue("level", getIValue("level")+1);
    setIValue("_parent_port", -1);
    setIValue("boss-id",id);
    setSValue("state","boss");
    if (parent.second!=INF){
        sons.insert(parent);
    }
    parent = Edge(INFV, INF);
    
    tell(strprintf("I am boss of my fragment with level %d", getIValue("level")));

    for(auto son : sons) 
        sendRebuild(son.second);

    setSValue("doing", "finding cheapest edge");
    findCheapest();
}
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
    if (parent.second!=INF)
        sons.insert(parent);
    parent = edges[port];
    sons.erase(parent);
    
    tell(strprintf("My new boss is %d", getIValue("boss-id")));

    for(auto son : sons) 
        sendRebuild(son.second);
}

//}}}

//{{{ Leader
void becomeLeader(){
    tell("I am the leader");
    setIValue("leader", id);
    spreadLeader();
    exitProgram("true");
}
void spreadLeader(){
    for(auto son : sons)
        sendMessage(son.second, strprintf("{leader} %d", getIValue("leader")));
}
void receiveLeader(int port, istringstream& iss){
    int leader;
    iss >> leader;
    setIValue("leader", leader);
    spreadLeader();
    exitProgram("false");
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
    if (s=="leader") receiveLeader(port,iss);

    checkWaiting();
    setIValue("waiting-requests", waitingRequests.size());

    if (waitForSons==0){
        waitForSons--;
        findCheapestUnknown();
    }
    string ssons = "";
    for(auto son : sons) 
        ssons+=strprintf("%d, ",son.second);
    setSValue("sons", ssons);
    string sreq = "";
}//}}}

void init(){ //{{{
    cheapestIsActual = 0;
    waitForSons = -1;
    setIValue("level", -1); //level fragmentu
    setIValue("boss-id",id); // sef fragmentu
    setIValue("_parent_port", -1); //ktorym smerom je kostrovy otec 
    setSValue("state","stupid");
    setSValue("doing","waiting for ids");
    setIValue("leader", -1);

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
