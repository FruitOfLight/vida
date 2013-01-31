/*
 anonym no
 graph none
*/

//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;

void recieve(int port, string message) {
    vi used = vi(ports.size()*2+47,0); 
    while(rand()%2 || rand()%47==0){
        int to = ports[rand()%ports.size()];
        char buffer[100];
        if (used[to])
            sprintf(buffer, "$C0000FF Hello {%d}", rand()%1000);
        else
            sprintf(buffer, "Hello {%d}", rand()%1000);
        used[to] = 1;
        sendMessage(to, string(buffer));
    }
}

void init(){
    int to = ports[rand()%ports.size()];
    char buffer[100];
    sprintf(buffer, "Hello {%d}", rand()%1000);
    sendMessage(to, string(buffer));
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
