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

int main(){
    char ch;
    char str[1000];
    printf("@ %d : %s\n", 0, "Hello");
    fflush(stdout);
    while(scanf("%c",&ch)>0) {
        if (ch == '\n') {
            continue;
        }
        else if(ch == '*') {
            eatline();
        }
        else if(ch == '@') {
            int from;
            scanf("%d : ", &from);
            readline(str);
            printf("@ %d : %s\n", from, str);
            fflush(stdout);
        }
        else if(ch == '!') {
            eatline();
        }
        else eatline();
    }
}
