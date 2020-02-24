#include <time.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h>
#include <sys/mman.h>
#include <dirent.h> 
int i;

void overwrite(int n){
    write(STDOUT_FILENO, "Ya NOPE HAHHAHAHAHAHAH\n", 25);  
}
int main(){
    //This is a dangerous program that creates a virtuly indesturctable child and parent process
    //Note* only works on unix systems
    struct dirent *de;
    signal(SIGINT, overwrite);
    signal(SIGTSTP, overwrite);
    signal(SIGTERM, overwrite);
    signal(SIGQUIT, overwrite);
    signal(SIGHUP, overwrite);
    time_t T= time(NULL);
    struct tm tm = *localtime(&T); 
    printf("Parent Process Pid: %d \n", getpid());
    while(1){
        if(fork()==0){
            while(1){
                DIR *dr = opendir(".");
                while((de = readdir(dr)) != NULL){
                    printf("%s\n", de->d_name);
                }
                closedir(dr);
                printf("Child Process Pid: %d \n", getpid());
                printf("%.2d: %.2d\n", tm.tm_hour, tm.tm_min);
                sleep(10);
            }  
            return 0;
        }
        else{
            wait(&i);
        }
    }

}
