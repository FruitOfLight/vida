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
typedef pair<int, Edge> DRequest;
typedef pair<int, int> WRequest;

#define INF 1023456789
#define PUNDEF INF
const Value INFV = Value(INF,INF);
//}}}

priority_queue<Edge, vector<Edge>, greater<Edge>> unknown;
priority_queue<DRequest, vector<DRequest>, greater<DRequest>> discoverRequests;
priority_queue<WRequest, vector<WRequest>, greater<WRequest>> whoRequests;

map<int, Edge> edges;

int parent = PUNDEF;
set<int> sons;
set<int> waitForSons;

bool cheapestIsActual = 0;
bool waitingForWho = 0;
///{{{ stats
namespace my_details{
    Edge cheapest = Edge(INFV,INF);
}
void setCheapest(Edge e){
    my_details::cheapest = e;
    if (e.first==INFV){
        setSValue("cheapest-edge", "undef");
    }
    setSValue("cheapest-edge", strprintf("%s<%d, %d> %d",
                (cheapestIsActual)?"":"so far ",
                e.first.first, e.first.second, e.second));
}
Edge getCheapest(){
    return my_details::cheapest;
}
//}}}

// function declarations

void receiveID(int port, istringstream& iss);

void resetCheapest();
void findCheapest();
void foundCheapest();
void findCheapestUnknown();
void receiveCheapest(int port, istringstream& iss);
void receiveIAm(int port, istringstream& iss);
void receiveWho(int port, istringstream& iss);
void checkWhoRequests();

void discover();
void receiveDiscover(int port, istringstream& iss);
void checkDiscoverRequests();

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
    resetCheapest();
    for(auto son : sons){
        waitForSons.insert(son);
        sendMessage(son,"{find-cheapest}");
    }
    waitingForWho = 0;
}
void foundCheapest(){
/*    if (cheapestIsActual)
        return;*/
    cheapestIsActual = 1;
    waitingForWho = -1;
    if (getCheapest().first == INFV){
        tell("No output edge found");
        if (parent==PUNDEF){
            becomeLeader();
            return;
        }
    }else{
        tell(strprintf("Cheapest edge found: <%d %d> %d",
                getCheapest().first.first, getCheapest().first.second, getCheapest().second));
    }
    if (parent==PUNDEF){
        tell("I found cheapest edge in whole fragment");
        tell("Let's discover the next fragment");
        discover();
    }else{
        tell("Let's send it to my parent");
        sendMessage(parent,strprintf("{my-cheapest} %d %d",
                    getCheapest().first.first, getCheapest().first.second));
    }
}
void findCheapestUnknown(){
    while (unknown.size()>0 && (unknown.top().second==parent))
        unknown.pop();
    if (unknown.size()>0 && sons.count(unknown.top().second)){
        waitForSons.insert(unknown.top().second);
        sendMessage(unknown.top().second,"{find-cheapest}");
        unknown.pop();
        return;
    }
    if(unknown.size()>0 && unknown.top()<getCheapest()){
        waitingForWho = 1;
        sendMessage(unknown.top().second,strprintf("{who} %d", getIValue("level")));
    }else{
        foundCheapest();
    }
}
void receiveCheapest(int port, istringstream& iss){
    int a,b;
    iss >> a >> b;
    Edge e = Edge(Value(a,b), port);
    setCheapest(min(getCheapest(),e));
    waitForSons.erase(e.second);
}
void receiveIAm(int port, istringstream& iss){
    int hisBossID;
    iss >> hisBossID;
    waitingForWho = 0;
    if (hisBossID==getIValue("boss-id")){
        unknown.pop();
        findCheapestUnknown();
    }else{
        setCheapest(min(getCheapest(),unknown.top()));
        foundCheapest();
    }
}
void receiveWho(int port, istringstream& iss){
    int hisLevel;
    iss >> hisLevel;
    whoRequests.push(WRequest(hisLevel, port));
}
void checkWhoRequests(){
    int myLevel = getIValue("level");
    WRequest r;
    while (!whoRequests.empty() && (r=whoRequests.top()).first<=myLevel){
        whoRequests.pop();
        sendMessage(r.second, strprintf("{i-am} %d", getIValue("boss-id")));
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
    int hisLevel, hisBoss, myBoss;
    iss >> hisLevel >> hisBoss;
    myBoss = getIValue("boss-id");
    if (myBoss == hisBoss){
        discover();
        return;
    }
    discoverRequests.push(DRequest(hisLevel,edges[port]));
}
void checkDiscoverRequests(){
    while (!discoverRequests.empty()) {
        setSValue("w-req", strprintf("(%d %d)",discoverRequests.top().second.second,
                    discoverRequests.top().first));
   
        DRequest r = discoverRequests.top();
        if (r.second.second == parent || sons.count(r.second.second)){
            discoverRequests.pop();
            continue;
        }
        if (getIValue("level") > r.first) {
            eatFragment(r.second.second);
            discoverRequests.pop();
            continue;
        }        
        if (getIValue("level") < r.first) {
            return;
        }
        if (getSValue("doing")!="discovering") return;
        if (unknown.top()!=getCheapest()) return;

        if (getCheapest() == r.second && id == getCheapest().first.first){
            sons.insert(unknown.top().second);
            unknown.pop();
            discoverRequests.pop();
            becomeNewBoss();
        }
        return;
    }
}
//}}}

//{{{ rebuild
void eatFragment(int port){
    tell("I win, let's eat his fragment");
    sons.insert(port);
    sendRebuild(port);
}
void becomeNewBoss(){
    setSValue("_vertex_color","100,255,255");
    setIValue("level", getIValue("level")+1);
    setIValue("_parent_port", -1);
    setIValue("boss-id",id);
    setSValue("state","boss");
    if (parent!=PUNDEF) sons.insert(parent);
    parent = PUNDEF;
    
    tell(strprintf("I am boss of my fragment with level %d", getIValue("level")));

    for(auto son : sons) 
        sendRebuild(son);

    setSValue("doing", "finding cheapest edge");
    findCheapest();
}
void sendRebuild(int port){
    sendMessage(port, strprintf("{rebuild} %d %d", getIValue("level"), getIValue("boss-id")));
}
void receiveRebuild(int port, istringstream& iss){
    int hisLevel, hisBoss;
    iss >> hisLevel >> hisBoss;
    setSValue("_vertex_color","255,200,50");
    setIValue("_parent_port", port);
    setIValue("level", hisLevel);
    setIValue("boss-id", hisBoss);
    setSValue("state", "slave");
    setSValue("doing", "forwarding messages");
    if (parent!=PUNDEF)
        sons.insert(parent);
    parent = port;
    sons.erase(parent);
    
    tell(strprintf("My new boss is %d", getIValue("boss-id")));

    for(auto son : sons)
        sendRebuild(son);
}

//}}}

//{{{ Leader
void becomeLeader(){
    tell("I am the leader");
    setIValue("leader", id);
    setSValue("_vertex_color","50,50,255");
    spreadLeader();
    exitProgram("true");
}
void spreadLeader(){
    for(auto son : sons)
        sendMessage(son, strprintf("{leader} %d", getIValue("leader")));
}
void receiveLeader(int port, istringstream& iss){
    int leader;
    iss >> leader;
    setIValue("leader", leader);
    setSValue("_vertex_color","255,0,0");
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
    if (s=="who") receiveWho(port,iss);
    if (s=="i-am") receiveIAm(port, iss);
    if (s=="discovery") receiveDiscover(port,iss);
    if (s=="rebuild") receiveRebuild(port,iss);
    if (s=="leader") receiveLeader(port,iss);

    checkWhoRequests();
    checkDiscoverRequests();
    setIValue("discover-requests", discoverRequests.size());
    setIValue("who-requests", whoRequests.size());
    if (!waitForSons.size() && !waitingForWho) findCheapestUnknown();

    string ssons = "";
    for(auto son : sons) 
        ssons+=strprintf("%d, ",son);
    setSValue("sons", ssons);
    ssons = "";
    for(auto son : waitForSons) 
        ssons+=strprintf("%d, ",son);
    setSValue("waiting-sons", ssons);
    setIValue("unknown", unknown.size());
}//}}}

void init(){ //{{{
    cheapestIsActual = 0;
    waitingForWho = -1;
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
