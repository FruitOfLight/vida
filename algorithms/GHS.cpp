/*
    anonym: no
    graph type: [any]
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

/*
 * zivot vrchola
 *  vypocitaj cenu susednych hran
 *  si sefom svojho fragmentu
 *    najdi najlacnejsiu hranu
 *
 *  
 */

typedef pair<int, int> Value;
typedef pair<Value, int> Edge;

#define INF 1023456789
const Value INFV = Value(INF,INF);

priority_queue<Edge, vector<Edge>, greater<Edge>> unknown;
vector<Edge> inside, sons;
Edge parent;

int waitForSons = -1;
Edge cheapest = Edge(INFV,INF);


void findCheapest(){
    tell("let's find cheapest edge found");
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
    tell(strprintf("cheapest edge found: <%d %d> %d",
                cheapest.first.first, cheapest.first.second, cheapest.second));
        
}

void findCheapestUnknown(){
    if(unknown.size()>0 && unknown.top()<cheapest){
        sendMessage(unknown.top().second,"{who}");
    }else{
        foundCheapest();
    }
}


void recieveCheapest(int port, istringstream& iss){
    int a,b;
    iss >> a >> b;
    Edge e = Edge(Value(a,b), port);
    cheapest = min(cheapest,e);
}

void recieveIAm(int port, istringstream& iss){
    int a;
    iss >> a;
    if (a==getIValue("boss-id")){
        inside.push_back(unknown.top());
        unknown.pop();
        findCheapestUnknown();
    }else{
        cheapest = min(cheapest,unknown.top());
        foundCheapest();
    }
}

void recieveID(int port, istringstream& iss){
    if (getSValue("state")!="stupid"){
        tell("error");
        return;
    }
    int a;
    iss >> a;
    unknown.push(Edge(Value(min(a,id), max(a,id)), port));
    if (unknown.size()==ports.size()){
        setIValue("level", 0);
        setSValue("state", "head");
        setSValue("doing", "finding cheapest edge");

        tell("i know all neighbors");
        tell("i am head of fragment");

        findCheapest();
    }

}

void recieve(int port, string message) {
    For(i, message.size()){
        if (message[i]=='{' || message[i]=='}')
            message[i] = ' ';
    }
    istringstream iss(message);
    string s;
    iss >> s;
    if (s=="id"){
        recieveID(port, iss);
    }
    if (s=="find-cheapest"){
        findCheapest();
    }
    if (s=="my-cheapest"){
        recieveCheapest(port, iss);
    }
    if (s=="who"){
        sendMessage(port, strprintf("{i-am %d}", getIValue("boss-id")));
    }
    if (s=="i-am"){
        recieveIAm(port, iss);
    }
    if (waitForSons==0){
        waitForSons--;
        findCheapestUnknown();
    }
}

void init(){
    
    setIValue("level", -1);
    setIValue("boss-id",id);
    setSValue("state","stupid");
    setSValue("doing","waiting for ids");

    tell("let's send my id to everyone");
    For(i,ports.size()){
        sendMessage(ports[i], strprintf("{id %d}", id));
    }
}

int main(){
    setInitListener(init);
    setMessageListener(recieve);
    
    run();
    return 0;
}
