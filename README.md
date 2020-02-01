## Event Driven Architecture
### Important Notes
This pattern already replaced by SAGA Pattern.


## Two Phase Commit Mechanism
### Goal
Distributed transaction need a rollback mechanism.
Two-phase commit is one of the solution.
### Step
1. Prepare Phase: All participant of the transaction prepare for commit and notify the coordinator that they are ready to complete the transaction.
2. Commit / Rollback Phase: After the coordinator identified that the transaction is success / fail, 
the coordinator will notify all participant to commit / cancel the current transaction.
### Drawback
2PC is quite slow because the coordinator need to prepare the transaction and notify to all participant first
before run the action. Even the coordinator and participant are on the same network, this can lead to slowing down the system.
So, this mechanism isn't fit in a high load transaction scenario.


## SAGA Pattern
### Goal
Microservice architecture can lead to span multiple business transaction across service.
We need a mechaninsm to ensure data consistency accross service.

Let's imagine we are building a digital banking application where customer can top-up their e-wallet 
account through their balance.

The application must ensure that the transaction will not deduct their balance once there are system failure.
