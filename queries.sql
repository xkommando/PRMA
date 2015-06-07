SELECT TG.value FROM `tag` as TG
	INNER JOIN `j_event_tag` AS JET ON JET.tag_id = TG.id
WHERE JET.event_id = ?

SELECT PROP.key, PROP.value FROM `property` as PROP
	INNER JOIN `j_event_prop` AS JEP ON JEP.prop_id = PROP.id
WHERE JEP.event_id = ?

SELECT EXP.name, EXP.msg FROM `exception` AS EXP
	INNER JOIN j_event_exception AS JEE ON JEE.except_id = EXP.id
WHERE JEE.event_id = ?

SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK
	INNER JOIN `j_exception_stacktrace` AS JES on JES.stacktrace_id = SCK.id
WHERE JES.except_id IN ()

SELECT SCK.file, SCK.class, SCK.function, SCK.line FROM 'stack_trace' as SCK
WHERE SCK.id = ?

SELECT EV.id,EV.time_created,EV.level,EV.logger_id,EV.thread,EV.flag,EV.message,EV.reserved,
    SK.file,SK.class,SK.function,SK.line
   FROM `event` AS EV
   INNER JOIN `stack_trace` AS SK ON SK.id = EV.caller_id
WHERE 

SELECT id, event_id, time_created, msg, reserved, thread_id, logger_id, caller_id
FROM `event` WHERE flag > 4294967295 AND ? < time_created AND time_created < ?


SELECT EV.id, JEE.event_id, EV.time_created, EV.msg, EV.reserved, EV.thread_id, EV.logger_id, EV.caller_id
FROM `event` as EV INNER JOIN `j_event_exception` as JEE ON JEE.event_id = EV.id
WHERE EV.flag > 4294967295 AND ? < EV.time_created AND EV.time_created < ?

SELECT id, time_created, msg,
reserved, thread_id, logger_id, caller_id
FROM `event` WHERE 4294967295 < flag  AND ? < time_created AND time_created < ?


SELECT  FROM `j_event_exception` as JEE
WHERE JEE.event_id in (?)


SELECT id, time_created, level FROM `event` 
WHERE ? < time_created AND time_created < ?
ORDER BY time_created ASC


SELECT COUNT(1) as CT, logger_id FROM `event`
GROUP BY logger_id ORDER BY CT DSC


-------------------------------------------------------------------------------

Problem not well defined -> difficult to design practical/reliable way vs fancy way

No log files for benchmark and test algorithm


summary
locator
missing event finder

statistics???


lecture2
classify ->
concurrency



1. notification ctrl
2. eclipse modeling
3. email
4. motivation
5. analyze

-------------------------------------------------------------------------------

query
log from packages/class
exception by name

statistics:
level count : how many error warm
log number -> time line

to one exception: {
time,
location: caller logger thread machine  //  Regression analysis,
msg,
}
location-> pkg class method
+

debug
grab nearby logs
execution path ????

sampling
http://en.wikipedia.org/wiki/Reservoir_sampling 


id

level

time

caller
logger
thread
reserved

msg

except
tag
prop
flag

St Christopher's Inn Gare du Nord

to one exception: {
time ??? high CPU
location: 
  caller 
  logger 
  thread
  machine  //  Regression analysis
}


http://sebastianraschka.com/Articles/2015_pca_in_3_steps.html#Introduction

todo:
inspect
loggen
pca

gae

