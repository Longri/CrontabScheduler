# CrontabScheduler
CrontabScheduler for Java



## *.ini description
Write jcronjobs in section [jobs] <br>
Write in variable count the number of jobs <br>
Write every job in a variable called job following the number of job like:<br>
&nbsp;&nbsp;&nbsp;&nbsp;job1= ..... <br>
&nbsp;&nbsp;&nbsp;&nbsp;job2= ..... <br>
<br><br><br>
Every job has 4 semicolon separated sections

    1. Cron like shudle time (0 0/5 * ? * * *)
    2. The type of job find over reflection 
    3. the name of this job 
    4. the arguments for the job





