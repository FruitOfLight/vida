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
    int initvalue = 0;
}

namespace WatchVariables {
    // pocuve na mena
    // _vertex_color e.g. "255,47,42"
    // _vertex_size in %, e.g. "80"

    namespace details{
        map<string,string> stringVariables;
        map<string,int> intVariables;
    }

    string getSValue(string name) {
        return details::stringVariables[name];
    }
    int getIValue(string name) {
        return details::intVariables[name];
    }

    void setSValue(string name, string value) {
        details::stringVariables[name]=value;
        printf("$ %s : %s\n",name.c_str(),value.c_str());
        fflush(stdout);
    }    
    void setIValue(string name, int value) {
        details::intVariables[name]=value;
        printf("$ %s : %d\n",name.c_str(),value);
        fflush(stdout);
    }

    string intToStr(int value){
        return to_string(value);
    }
    int strToInt(string str){
        int value = 0;
        For(i, str.size()) if (str[i]>='0' && str[i]<='9') value = value*10+str[i]-'0';
        return value;
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

        // zmaze zo spravy $ 
        string clean(string str){
            string res = "";
            bool active = true;
            For(i, str.size()) {
                if (str[i]<32) continue;
                if (str[i]=='$') active = false;
                if (active) res+=str[i];
                if (str[i]==' ') active = true;                
            }
            return res;            
        }
    }

    // kazda prijata sprava sa ohlasi fuknciou 'func'
    void setMessageListener(MFunc func){
        details::mFunc = func;
    }

    // ked ma program zacat bezat, zavola sa 'func'
    void setInitListener(IFunc func){
        details::iFunc = func;
    }

    // posli spravu po porte 'port' s textom 'str'
    // 'str' moze obsahovat len alfanumericke znaky a medzery
    //   s vyjnimkou ze viete, co robite
    //
    // nepovinny parameter 'color' nastavi sprave farbu
    //   priklad biela = 'FFFFFF'  
    void sendMessage(int port, const string &str, const string &color = ""){
        printf("@ %d :", port);
        // znak $ znamena, ze podplatime spravu aby nieco spravila
        if (color.size()>0) printf(" $C%s", color.c_str());
        printf(" %s\n", str.c_str());
        fflush(stdout);
    }

    void sendInformation(const string &str){
        printf("# %s\n",str.c_str());
        fflush(stdout);
    }

    void algorithmUpdate(const string &str) {
        printf("* %s\n",str.c_str());
        fflush(stdout);
    }

    void pauseProgram() {
        char c='%';
        printf("%c\n",c);
        fflush(stdout);
    }

    void exitProgram(string exitValue) {
        printf("& %s\n",exitValue.c_str());
        fflush(stdout);
    }

    /*
    void sendVertexColorChange(const string &str) {
        printf("+ %s\n",str.c_str());
        fflush(stdout);
    }

    void sendVertexRadiusChange(const string &str) {
        printf("_ %s\n",str.c_str());
        fflush(stdout);
    }
    */

    // spusti pocuvanie
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
            } else if(!strcmp(str,"id")) {
                int _id;
                scanf(" : %d",&_id);
                Property::id = _id;
            } else if(!strcmp(str,"initvalue")) {
                int _initvalue;
                scanf(" : %d",&_initvalue);
                Property::initvalue = _initvalue;
            } else if (!strcmp(str,"start")){
                iFunc();
            }
            readline();
        }
        else if(ch == '@') {
            int port = -1;
            scanf("%d : ", &port);
            readline(&line);
            line = clean(line);
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
