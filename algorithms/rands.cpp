//Fruit of Light
//Apple Strawberry

#include"vidalib.h"

using namespace Messager;
using namespace Property;

void recieve(int port, string message) {
    while(rand()%2 || rand()%47==0){
        int to = ports[rand()%ports.size()];
        char buffer[100];
        sprintf(buffer, "Hello %d", rand()%1000);
        sendMessage(to, string(buffer));
    }
}

void init(){
    int to = ports[rand()%ports.size()];
    char buffer[100];
    sprintf(buffer, "Hello %d", rand()%1000);
    sendMessage(to, string(buffer));
}

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%1000;

    setInitListener(init);
    setMessageListener(recieve);

    run();
}
