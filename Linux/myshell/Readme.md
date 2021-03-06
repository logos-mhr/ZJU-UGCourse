# Welcome to myshell.

This is a user manual helping you get start with myshell.

## What is a shell?

Cite the defination of Wikipeida: In computing, a shell is a user interface for access to an operating system's services.
So with a shell, you can login to a computer and use the computer to complete some tasks or change the computer's status.
"myshell" is a test(CLI) shells. So you can only privide instructions by typing characters using your keyboard.

## Some concepts we have to know before using the shell.

1. *executive file*: Files that can execute directly by the system. It receive some inputed infromation and output the result after processing. We can call it a program.
2. *directory*: You can regard it as folder in Windows. They ARE different but similiar. They both contain some files and directories in itshelf.
3. *path*: It addresses to the file or folder like address in the real life.
4. *environment value*: Some values in shell that will be replace as the value of it when the shell parse the instructions. Program that lunched by the shell usually share these values in the shell. It's familiar with the concept of value in most of the advanced programing languages.
5. *working direcotry*: The current working direcotry of the shell. Files or directories in the working direcotry can omit its long prefix of its path and only type its name. Of course, it can be changed by some introductions.

## First impression of myshell

When you lunch myshell, you can see a prompt string like `myshell:/home/yujincheng/project3/myshell>`.
Every characters you type will appear after the prompt string. Finishing your typing, you can simply press the "Enter" button and the shell will parse your instructions, which we call commands, and complete the tasks.

### Struction of a command

Each command is consist of a program name, some options and some arguments. They are placed in the following order:
```bash
Program [option] [argument]
```
For instance, here is an avaliable command: `ls -l /bin/ls`. In this command, `ls` is the program name, `-l` is the option and `/bin/ls` is the argument. This command can list the detail infromation of the file `/bin/ls`.

### What programs are there I can use?

Well, there are tons of powerful programs, or tools, you can use. They privide different useful functions and their usages are also vairous.
But there are two kinds of programs. One kind of programs placed in the paths which are stored the environment value PATH. The other is privide by the shell itshelf. We call it inner command, which will be introduced later.
If you want to know about the detailed information of a program, you can type:
```shell
man <program name>
```
to learn about it.

Now you can try to type `man cat` to know the function of the "cat" program.

Ok, now you know the function of "cat": It receives string from your keyboard or from the file you provied and output it to your screen. We call the input of your keyboard standrad input(in short, stdin) and call the output to you screen standard output(in short, stdout).
Without any arguments, cat receives string from stdin and output to stdout. But I want it output the infromation to a certain file rather than stdout. Can it come true? Sure, it's I/O redirection.
Output redirection can store the output characters to a certain file rather than the screen. Input redirection is similiar with it. We use a operatior '<' to indicate an input redirection and '>' to an output redirection. The following string after them is the filename. For instance, you can redirection the output of cat to file out using this command: `cat > out`. Now you can type any string you like and they will be stored in file out. You can check it by entering `cat out`.
Of coure, you can copy a file using `cat`. For instance, I would like to copy file in to file out. Here are you the command: `cat in > out`.
Well, clever as you are, we can also realise the same function using the following command: `cat <in >out`. It redirects the stdin using file in!
Indeed, may program can receive input from a certain file. But they can also receive input from stdin. In this case, you can use input redirection to send a file into the program.

So if I am going to input data to a program A from the output of another program B, I just have to store the output of the B and then redirect the input of program A by the file I store. Yes, it an avaliable way to realise. But I am going to introduce a new and convenient way to realise it -- using pipe.
Pipe is a communication method between two process. We can use operatior '|' to create a pipe, and the output of the former program will redirect to the input of the latter program. For example: `ls -l | grep \.c`. This command will output all the c file in the working direcotry.

You can only control one program at once. In another word, you can only input your string to only onprogram. This program we call it foreground program. But there can be lost of program running background. You can run multiply programs at once using operatior '&'. For example, you can find c files in the background meanwhile you can edit a file foreground: `find $HOME -name *.c -print > out & vim edit`

You can put a foreground program background using short key "Ctrl+z". You can list all the background programs, which we call jobs, by command jobs. You can put a background program foreground by commnand `fg X`. X is the job id.
For instance:
```shell
> vim file.c
Ctrl+z
	[2] vim file.c
> gcc -ofile file.cal
> ./file
> jobs
	[1] find $HOME -name *.c -print > out
	[2] vim file.c
> fg 2
```
Do you notice that I use `$HOME` above. What does it mean? In fact, it refers to the environment value HOME. When we have to refer to a environment value, `$+its name`. For example, echo `$PATH` output the value of PATH.

### Let me introduce all the inner commands.

1. `bg [id]`
   Let a stopped job with id run background. Remember not to run a job that use stdin, which may cause error.
2. `pwd`
   Print the current working direcotry.
3. `cd [path]`
   Change the working directory to path. If no path provied or it's output has been redirected by pipe, it equals to pwd, but redirected.
4. `time [command]`
   Lunch the command and list the real time, user time and system time it takes. All redirection is for the command. And only one command once. If command is not provided, it prints the current time.
5. `clr`
   Clear the screen. Equals to clear.
6. `dir [directory]`
   List all contains of direcoty. If direcotry is not privided, list all the contains of the current working direcotry.
7. `environ`
   List all the environment values.
8. `echo <comment>`
   Print the comment stirng. All space will be replace as one space, not including tabs.
9. `help`
   "more" the readme file (more readme) .
10. `quit`
    Quit the shell when no jobs working background.
11. `exec [command]`
    Lunch the command and exit the shell. If command is not privided, it relunch the shell. Notably, redirection is avaliable.
12. `exit`
    The same as quit.
13. `fg [id]`
    Put a background job with id foreground.
14. `jobs [id...]`
    List jobs with privided id. If no id privided, list all the jobs.
15. `set [name] [value]`
    Set the environment value named name and values value. You can use it after setting with form $name.
16. `test [condiction]`
    If the condiction privided is true, output "ture", othewise, output "false". More information about the formate of the condicton plese check its manual.
17. `umask [mask]`
    Set file mode creation mask. It will be inherited by child processes. If mask is not privided, print current umask value.
18. `unset [name]`
    Unset the environment value named name.

All inner commands that have output can redirection execpt for time.