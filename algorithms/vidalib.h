//Fruit of Light
//FoL lib
//Apple Strawberry

#include<cstdio>
#include<iostream>
#include<cstring>
#include<algorithm>
#include<vector>
#include <map>

using namespace std;

#define For(i, n) for(int i = 0; i<(n); ++i)
#define ForEach(it, i) for(typeof i.begin() it = i.begin(); it!=i.end(); ++it)
#define pass 

typedef long long ll;
typedef pair<int, int> pii;
typedef vector<int> vi;
typedef void (MFunc)(int, string);
typedef void (IFunc)();

namespace Property {
    vi ports;
    int id;
}

namespace WatchVariables {

    map<string,int> variables;

    void addVariable(string name, int value) {
        variables.insert(make_pair(name,value));
        printf("Wa %s : %d\n",name.c_str(),value);
        fflush(stdout);
    }

    int getValue(string name) {
        return variables[name];
    }

    void setValue(string name, int value) {
        variables[name]=value;
        printf("Wc %s : %d\n",name.c_str(),value);
        fflush(stdout);
    }

}

namespace Messager {
    namespace details {
        MFunc *mFunc;
        IFunc *iFunc;

        void readline(string *str = NULL){
            char ch;
            if (str==NULL){
                while(scanf("%c",&ch)>0 && ch!='\n') ;
                return;
            }
            str->clear();
            while(scanf("%c",&ch)>0 && ch!='\n') (*str) += ch;
        }
    }

    void setMessageListener(MFunc func){
        details::mFunc = func;
    }
    void setInitListener(IFunc func){
        details::iFunc = func;
    }
    void sendMessage(int port, const string &str){
        printf("@ %d : %s\n", port, str.c_str());
        fflush(stdout);
    }
    void sendInformation(const string &str){
        printf("# %s\n",str.c_str());
        fflush(stdout);
    }
    void run();
};

void Messager::run(){
    using namespace details;
    char ch;
    string line;
    char str[1047];
    while(scanf("%c",&ch)>0) {
        if (ch == '\n') {
            continue;
        }
        else if(ch == '*') {
            scanf("%s", str);
            if (!strcmp(str,"ports")){
                int portn, portid;
                scanf(" : %d", &portn); 
                For(i,portn) {
                    scanf("%d", &portid);
                    Property::ports.push_back(portid);
                }
            } else if (!strcmp(str,"start")){
                iFunc();
            }
            else if(!strcmp(str,"id")) {
                int ID;
                scanf(" : %d",&ID);
                Property::id = ID;
            }
            readline();
        }
        else if(ch == '@') {
            int port = -1;
            scanf("%d : ", &port);
            readline(&line);
            mFunc(port, line);
        }
        else if(ch == '!') {
            readline();
        }
    }
}

/////////////////////////////////
/*
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
*/
