//Fruit of Light
//FoL CC
//Apple Strawberry

#include<cstdio>
#include<algorithm>
#include<vector>
#include<iostream>
#include<cstring>

using namespace std;

#define For(i, n) for(int i = 0; i<(n); ++i)
#define ForEach(it, i) for(typeof i.begin() it = i.begin(); it!=i.end(); ++it)
#define pass 

typedef long long ll;
typedef pair<int, int> pii;

void eatline(){
    char ch;
    while(scanf("%c",&ch)>0 && ch!='\n') pass;
}

void readline(char str[]){
    int pos = 0;
    while(scanf("%c",str+pos)>0 && str[pos]!='\n') pos++;
    str[pos] = 0;
}

int portn;
int ports[1047];

int main(){
    srand(time(NULL)+getpid());
    int randid = rand()%10000;
    char ch;
    char str[1000];
    while(scanf("%c",&ch)>0) {
        if (ch == '\n') {
            continue;
        }
        else if(ch == '*') {
            scanf("%s", str);
            if (!strcmp(str,"ports")){
                scanf(" : %d", &portn); 
                For(i,portn) scanf("%d", ports+i);
                printf("@ %d : %s %d\n", ports[rand()%portn], "Hello", randid);
                fflush(stdout);
            }
            eatline();
        }
        else if(ch == '@') {
            int from,to;
            scanf("%d : ", &from);
            readline(str);
            while(rand()%2 || rand()%47==0){
                to = ports[rand()%portn];
                printf("@ %d : %s\n", to, str);
            }
            fflush(stdout);
        }
        else if(ch == '!') {
            eatline();
        }
        else eatline();
    }
}
