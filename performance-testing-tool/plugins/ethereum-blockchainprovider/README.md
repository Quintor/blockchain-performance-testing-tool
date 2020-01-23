#Ethereum Blockchain Provider
An ethereum blockchain provider for the Performance testing tool.

#Configuration
Needs to following configuration variables in the configuration file (Example the test resources testBlockchainProviderConfigBlock.yml)
```
confirmCheckInterval: # time at which a block poll is done on the node
contract:
  address: # Adres of the contract on the network if you dont wanna use the setup make sure this is set
  binary: # Binary string of the contract, used when setup is used to deploy a contract
  functionList: # a list of functions in the contract uses
  inputParameters: # [] list of values supports address string, normal strings and integers
  name: # name of the function it is case sensitive
  outputParameters: # [] list of values it is suppose to return supports Uint8 and Utf8String
  type: # Either TRANSACTION or QUERY
nodeUrl: # url of the node endpoint
wallet: # the private key of the account you wanna use
```
The performance testing tool variables under the same id are:
```
type: ethereum-blockchainprovider # pluginId of this provider
functions: # List of functionId's must be exactly the same are the name of the function in the functionList
```
#Parameters
For the functions in the functionList you need to assign values to inputParameters if your contract function needs these. 
These values are static and will be parsed internally to the right web3j type.
Input Parameters parse as follows:
- Normal string -> UtfString
- Address string -> Address (it will try to generate an Address object otherwise defaults to normal string)
- Number -> Uint8

Queries are supported minimaly and are not tested but it will try to query. It does not check if the return values are correct only if no errors occure.
To do a query you can use the QUERY type and specify the input and outputParameters. Output parameters are always Strings.
These string values are then parsed to web3j typeReferences.
Output parameters parse as follows:
- Uint8 -> Uint8
- Utf8String -> Utf8String

#Setup
You can use the provider in the useProviderForSetup block.
This will automaticly deploy a contract on the blockchain using the binary string and distribute the contract adres.

#TransactionResult
The TransactionResult collecting starts in the EthereumBlockchainProvider the rotation is:
- Transaction is send
- set timeAtSend
- wait for response if response has no errors
- set timeAtReceive
- give transactionHash to the EthereumBlockchainConfirmer
- Continue handling messages

When the EthereumBlockchainProvider is started it makes a EthereumBlockchainConfirmer.
This confirmer starts asking for the latest block on the network every time according to confirmCheckInterval and remembers the lastBlock it checked.
when it gets a transactionHash from the provider it adds it to the list.
When the confirmer gets a block it checks if it hasn't check any of the previous blocks using the lastCheckedBlockNumber variable.
Every block it hasn't checked it checks. 
Checking is done by comparing the hashes. Before going through all the transactions in the block it saves the time. 
So when multiple transactions it needed to confirm are inside the block they all get the same confirm time. 
When it has confirmed a transaction it sends the TransactionResult back to the provider which sends it back to the testrunner.
