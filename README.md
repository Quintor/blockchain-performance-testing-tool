# Blockchain Performance Testing Tool
The Blockchain Performance Testing Tool is a java 11 commandline application to collect performance metrics of a Blockchain network. 
To collect the tool sends transactions to the Blockchain network according to a user definably test scenario file. 
Data is collected while sending these transactions and parsed into the following metrics:
- Time to receive
- Time to confirm
- Success rate of receive
- Success rate of confirm
- Timeline of transaction state count
- Timeline of transaction speed
- Error overview

A [*Test Report*](Documentation/ptt-report-191211-1312.pdf) is generated in pdf format with these metrics.

This project uses travis and sonar for CI.

## Maven
Maven is used as build tool with multiple modules.

The following maven commands can be used:

Run all tests:`mvn clean test`

Build everything: `mvn clean package`

While running the tests only automatic tests are ran. Some tests such as the End2EndTests can only be run manual because you need plugin jars. These End2EndTests probably need personal data to run on public blockchains so be sure to not commit those to the project.

The build is a jar with the application and all dependencies in a libs folder.

#  Usage
To be able to use the Blockchain Performance Testing Tool the following artifacts are needed:
- ptt.jar
- libs folder with all dependencies
- plugins folder with all jars you want to use in your test scenario

All these artifacts needs to be in the same folder to be able to run the tool.

To use the Blockchain Performance Testing Tool, first navigate to the correct folder inside a terminal and then use the command: `java -jar ptt.jar`

The following message is printed when the tool is incorrectly used
```
usage: java -jar ptt.jar [options]
  -f,--file <arg>      Location of the config file
  -ft,--format <arg>   Set format of test report supported formats:
                       pdf,yaml - defaults to pdf
  -o,--output <arg>    Set if you want to change the filename and/or path
                       of the test report
  -t,--timeout <arg>   When set application will timeout after given
                       duration, accepts ISO-8601 duration format
```

The test scenario file which defines the scenario the tool runs is a YAML file using the following structure:
```
runners:
  testrunner1:
    &testrunner
    type: testrunner
    providerId: blockchainprovider1
    #  testrunner config block BEGIN

    #  testrunner config block END
  testrunner2:
    <<: *testrunner
    providerId: blockchainprovider2
useProvidersForSetup:
  blockchainprovider1:
    passthroughSetupDataTo:
      - blockchainprovider2
providers:
  blockchainprovider1: &blockchainprovider
    type: empty-blockchainprovider
    #  blockchain provider config block BEGIN

    #  blockchain provider config block END
  blockchainprovider2:
    <<: *blockchainprovider
```
It has 3 parts:
- runners
- useProvidersForSetup
- providers

**runners** are the testrunners which makes up your testscenario. Testrunners are plugins and can be configured according to the plugin documentation. The application needs to have a unique name as key and the fields, type (pluginId) and the name of the provider it used (providerId). As of now every testrunners needs to be 1 on 1 with the provider (this is however not checked so feel free to test it)

**providers** are the blockchain provider plugins which facilitates the communication with the Blockchain network. Providers can also be configured according the the plugin documentation. Providers need to have a unique name as key and the field type (pluginId) needs to be set.

**useProvidersForSetup** Provider plugins can be issued a setup command before starting the test. This is a command to setup the Blockchain network for testing (look at the plugin documentation to see what it does). Because this commands demands that the setup variables are returned to be shared among other providers you can define under this part which provider needs to be send a setup command and to which the returning variables need to be send. In the example above, blockchainprovider1 does the setup and the variables are send to blockchainprovider2

Important to know is that the Blockchain Performance Testing Tool supports the yaml [<< merge key](https://yaml.org/type/merge.html).
This makes it able to quickly duplicatie variables across other parts in the file while also being able to override fields by setting the field below. 
```
blockchainprovider1: &blockchainprovider
    type: empty-blockchainprovider
    #  blockchain provider config block BEGIN
    endPoint: localhost
    username: admin
    password: admin
    #  blockchain provider config block END
  blockchainprovider2:
    <<: *blockchainprovider
    username: user
    password: user
``` 

# Plugins
Plugins, test runner and blockchain provider, can be writing by yourself. The [linear-testrunner](performance-testing-tool/plugins/linear-testrunner) plugin and the [ethereum blockchain provider](performance-testing-tool/plugins/ethereum-blockchainprovider) plugin are already written.

To better support the development of new plugins there are starter projects available for both types. Also there are also manuals available but these are only in Dutch as of now.

Testrunner - [Handleiding](Documentation/PTT-TestRunnerPluginHandleiding.pdf) - [Starter Project](performance-testing-tool/plugins/empty-testrunner).

Blockchain provider - [Handleiding](Documentation/PTT-BlockchainProviderPluginHandleiding.pdf) - [Starter Project](performance-testing-tool/plugins/empty-blockchainprovider)
