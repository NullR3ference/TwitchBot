#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>



enum
{
  CODE_RESTART_PROCESS = 10,
  WATCHDOG_CHECK_TIMEOUT = 5
};



void print_help(const char* name)
{
  printf("Simple watchdog wrapper for TwitchBot\n");
  printf("Usage: %s <command> [arg]\n", name);
}

pid_t run_process(const char* executable, char** argv)
{
  const pid_t pid = fork();

  if (pid == 0)
  {
    const int result = execv(executable, argv);
    if (result == -1)
    {
      perror("execv");
      exit(EXIT_FAILURE);
    }
  }

  printf("Process %d started\n", pid);
  return pid;
}



int main(int argc, char** argv)
{
  if (argc < 2)
  {
    print_help(argv[0]);
    return 0;
  }

  char* child_executable = argv[1];
  const int child_argc = argc - 2;

  char* child_executable_args[3] = {child_executable, NULL, NULL};
  if (child_argc >= 1)
  {
    child_executable_args[1] = argv[2];
  }

  pid_t child = run_process(child_executable, child_executable_args);

  while (1)
  {
    int status = 0;
    const pid_t result = waitpid(child, &status, 0);

    if (result == -1)
    {
      perror("waitpid");
      exit(EXIT_FAILURE);
    }

    if (WIFEXITED(status))
    {
      const int return_code = WEXITSTATUS(status);
      printf("Captured return code from %d: %d\n", child, return_code);

      switch (return_code)
      {
        case CODE_RESTART_PROCESS:
          printf("Restarting child process...\n");
          child = run_process(child_executable, child_executable_args);
          break;
      }
    }
    else
    {
      printf("Process %d was terminated\n", child);
      break;
    }

    sleep(WATCHDOG_CHECK_TIMEOUT);
  }

exit:
  return 0;
}
