#Linear test runner
A testrunner for the performance testing tool. 
This testrunner is a simple burst testrunner and doesn't follow the best practises describeb for non blocking.

#Setup
    type: linear-testrunner - Static type for using this testrunner
    providerId:  - Id of the provider it uses (1 on 1 relation)
    txAmount: - number of transactions it can send
    functionId: - String of the functionId it uses for all the transactions it needs to send
    
#Sending Transactions
After it gets a start message it goes into a simple for loop sending SendTransactionMessage to the blockchain provider. 
It uses the functionId for the function. How many messages are send is depended on txAmount.
This is not according to best practices because it blocks message handling while it is sending transactions in the for loop. 
Terminate messages are thus not handles during the sending.