// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.apps.misc.stableRoommates;

import com.becker.common.format.FormatUtil;
import com.becker.common.math.MathUtil;

import java.math.BigDecimal;

/**
 *
 *
 * @author Barry Becker
 */
public class StableRoommatesApp {


    private static final int NUM_QUESTIONS = 40;


    private StableRoommatesApp() {}


    public static void main(String[] args) {

        for (int i=0; i<=NUM_QUESTIONS; i++) {

            System.out.println("Probability " + i + " or more right when taking test is " + FormatUtil.formatNumber(1));
        }


    }
}

/*
const  SIZE  =  91;  {FOR  PROBLEM  INSTANCES  OF  SIZE  <  =  90,
ALLOWING  FOR  SENTINELS}
type  person-type  =  O..SIZE;  rank-type  =  O..SIZE;
matrix  =  array[person-type,rank&pe]  of  person-type;
vector  =  arrafiperson-type]  of  person-type;
set-type  =  set  of  person-type;
procedure  room-mates(var  preference  : matrix;  n  : integer;
var  partner  : vector;  var  soln-found  : boolean);
var  ranking  : arrayIperson_type,persorUype]  of  rank-type;
leftmost,second,rightmost  : array[person-type]  of  rank-type;
cycle  : arrafirank-type]  of  person-type;
person,first-unmatched  : person-type;
rank,firstin-cycle,lastin-cycle  : rank-type;
solnpossible  : boolean;
tail  : set-type;
procedure  phase-lreduce(var  soln-possible  : boolean);
var  set-proposed-to  : set-type;
person,proposer,nextchoice,current  : person-type;

begin
set-proposed-to  :=  [I;
for  person  :=  1  to  n  do
begin
proposer  :=  person;
repeat
next-choice  :=  preference[proposer,leftmost[proposer]];
{BEST  POTENTIAL  PARTNER}
current  :=  preferencdnext-choice,rightmost[nextchoice]];
{NEXT-CHOICE  HOLDS  CURRENT}
while  ranking@ext-choice,proposer]  >  ranking[next~choice,current]
do
begin  {PROPOSER  IS  REJECTED  BY  NEXT-CHOICE}
leftmost[proposer]  :=  leftmost[proposer]  +  1;
next-choice  :=  preference[proposer,leftmost[proposer]];
current  :=  preference[next-choice,rightmost[next-choice]]
end;
rightmost[next-choice]  :=  ranking[next-choice,proposer];
{NEXT-CHOICE  HOLDS  PROPOSER}
proposer  :=  current
{AND  REJECTS  CURRENT}
until  not  (next-choice  in  set-proposed-to);
set-proposed-to  :=  set-proposed-to  +  [next-choice]
end;
soln-possible  :=  proposer  =  next-choice
end;  {phase-l-reduce}
procedure  find(var  first-unmatched  : person-type);
begin  {FINDS  FIRST  PERSON  WITH  >  1 POTENTIAL  PARTNER)
while  leftmost[first-unmatched]  =  rightmost[first~unmatched]  do
first-unmatched  :=  first-unmatched  +  1
end;  {find}
procedure  seek-cycle@.r  first3n~cycle,last3nn_cycle  : rank-type;
first-unmatched  : person-type;  var  tail  : set-type);
var  cycle-set  : set-type;
personnext-choice  : person-type;
posn-in-cycle,pos-in-list  : rank-type;
begin
if  first-in-cycle  >  1
then  begin
person  :=  cycle[first-in-cycle-l];  {LAST  PERSON  IN
PREVIOUS  TAIL)

posn-in-cycle  :=  firstin-cycle-l;  {HIS  SECOND  CHOICE  MAY
HAVE  TO  BE  UPDATED}
cycle-set  :=  tail
end
else begin
cycle-set  :=  [];
posnin-cycle  :=  1;
person  :=  firstunmatched
end;
repeat  {GENERATE  SEQUENCE}
cycle-set  :=  cycle-set  +  [person];
cycle[posn-in-cycle]  :=  person;
posn-in-cycle  :=  posn-in-cycle  +  1;
pas-in-list  :=  second[person];
repeat  {UPDATE  SECOND  CHOICE  FOR  CURRENT  PERSON}
next-choice  :=  preference[person,pos_in_list];
pas-in-list  :=  pas-in-list  +  1
until  ranking[next-choice,person]  <  =  rightmost[next-choice];
second[person]  :=  posinlist  -  1;
person  :=  preferenc~next~choice,rightmost[nextchoe]]
until  person  in  cycle-set;  {SEQUENCE  STARTS  TO  CYCLE}
last-in-cycle  :=  posn-in-cycle  -  1;
tail  :=  cycle-set;
repeat  {WORK  BACK  TO  BEGINNING  OF  CYCLE}
posn-in-cycle  :=  posnin_cycle  -  1;
tail  :=  tail  -  [cycle[posnin-cycle]]
until  cycle[posnin-cycle]  =  person;
first-in-cycle  :=  posn-in-cycle
end;  {seek-cycle}
procedure  phase_2_reduce(firstjn_cycle,lastin_cycle  : rank-type;
var  soln-possible  : boolean);
var  proposer,next-choice  : person-type;
rank  : rank-type;
begin
for  rank  :=  first-in-cycle  to  last-in-cycle  do
begin  {ALLOW  NEXT  PERSON  IN  CYCLE  TO  BE  REJECTED}
proposer  :=  cycle[rank];
leftmost[proposer]  :=  second[proposer];
second[proposer]  :=  leftmost[proposer]  +  1;  {PROPER  UPDATE
UNNECESSARY  AT  THIS  STAGE]
next-choice  :=  preference[proposer,leftmost[proposer]];

rightmost[next-choice]  :=  ranking[next-choice,proposer]
{NEXT-CHOICE  HOLDS  PROPOSER}
end;
rank  :=  firstin_cycle;
while  (rank  <  =  last-in-cycle)  and  soln-possible  do
begin  {CHECK  NO-ONE  HAS  RUN  OUT
OF  POTENTIAL  PARTNERS}
proposer  :=  cycle[rank],;
soln-possible  :=  leftmost[proposer]  <  =  rightmost[proposer];
rank:=rank+l
end
end;  { phaseZ_reduce}
begin
soln-found  :=  false;
first-unmatched  :=  1;
first-in-cycle  :=  1;
for  person  :=  1 to  n  do
begin
preference[person,n]  :=  person;  {SENTINEL}
for  rank  :=  1  to  n  do
ranking[person,preference[person,rank]]  :=  rank;
leftmost[person]  :=  1;
rightmost[person]  :=  n
end;
leftmost[n  +  l]  :=  1; rightmost[n  +  l]  :=  n;  {SENTINELS  FOR
PROCEDURE  FIND}
phase-l-reduce(soln_possible);
for  person  :=  1  to  n  do
second[person]  :=  leftmost[person]  +  1;  {PROPER  INITIALISA-
TION  UNNECESSARY}
while  soln-possible  and  not  sohr-found  do
begin
find(first-unmatched);
if  first-unmatched  >  n
then  soln-found  :=  true
else begin
seekcycle(firstin_cycle,last_in_cycle,fid,tail);
phase_2_reduce(firstjn_cycle,last_in_cycle,so~-~ssible)
end
end;
if  sohr-found

then  for  person  :=  1  to  n  do
partner[person]  :=  preference[person,leftmost[person]]
end;  {roommates}
*/