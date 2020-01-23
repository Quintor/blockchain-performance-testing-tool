package nl.quintor.blockchain.ptt.blockchainproviders;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import nl.quintor.blockchain.ptt.api.TransactionResult;
import nl.quintor.blockchain.ptt.api.messages.SendTransactionMessage;
import nl.quintor.blockchain.ptt.api.messages.SetupNetworkMessage;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumBlockchainConfig;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumContract;
import nl.quintor.blockchain.ptt.blockchainproviders.config.EthereumFunction;
import nl.quintor.blockchain.ptt.blockchainproviders.config.FUNCTION_TYPE;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("integration")
@ExtendWith(MockitoExtension.class)
class EthereumBlockchainProviderIntegrationTest {


    private static final String DEFAULTNODEURL = "http://localhost:8545/";
    private static final String DEFAULTCONTRACTADDRESS = "0x84B872B7ac4a89f66808D94b966CdE3d4D1bAcCC";
    private static final String DEFAULTWALLET = "0288ac065901335687b6ad98c7580841c3b70634ad9da7a5ed454cef47b7efa5";
    private static final String VOTEWALLET = "95f6276e4b95b7a9eea692033887d5f7a250e591f11664ec0c38b1719d3b2c29";
    private static final String VOTER_ADDRESS = "0xAe2ba17bf606665485B4B9476f867D8Ba141bB68";
    private static final String DEFAULTBINARY = "608060405234801561001057600080fd5b506040516104793803806104798339818101604052602081101561003357600080fd5b5051600080546001600160a01b03191633178082556001600160a01b031681526001602081905260409091205560ff811661006f600282610076565b50506100c0565b81548183558181111561009a5760008381526020902061009a91810190830161009f565b505050565b6100bd91905b808211156100b957600081556001016100a5565b5090565b90565b6103aa806100cf6000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80635c19a95c14610051578063609ff1bd146100795780639e7b8d6114610097578063b3f98adc146100bd575b600080fd5b6100776004803603602081101561006757600080fd5b50356001600160a01b03166100dd565b005b610081610230565b6040805160ff9092168252519081900360200190f35b610077600480360360208110156100ad57600080fd5b50356001600160a01b0316610298565b610077600480360360208110156100d357600080fd5b503560ff166102f5565b3360009081526001602081905260409091209081015460ff1615610101575061022d565b5b6001600160a01b0382811660009081526001602081905260409091200154620100009004161580159061015957506001600160a01b0382811660009081526001602081905260409091200154620100009004163314155b1561018b576001600160a01b039182166000908152600160208190526040909120015462010000900490911690610102565b6001600160a01b0382163314156101a2575061022d565b6001818101805460ff1916821762010000600160b01b031916620100006001600160a01b0386169081029190911790915560009081526020829052604090209081015460ff16156102225781546001820154600280549091610100900460ff1690811061020b57fe5b60009182526020909120018054909101905561022a565b815481540181555b50505b50565b600080805b60025460ff82161015610293578160028260ff168154811061025357fe5b9060005260206000200160000154111561028b5760028160ff168154811061027757fe5b906000526020600020016000015491508092505b600101610235565b505090565b6000546001600160a01b0316331415806102ce57506001600160a01b0381166000908152600160208190526040909120015460ff165b156102d85761022d565b6001600160a01b0316600090815260016020819052604090912055565b3360009081526001602081905260409091209081015460ff168061031e575060025460ff831610155b15610329575061022d565b6001818101805460ff191690911761ff00191661010060ff85169081029190911790915581546002805491929091811061035f57fe5b600091825260209091200180549091019055505056fea265627a7a72315820ec95a655a045c6ee05dc09b514d414a8012872a994c6f67f15854294cfc2e76b64736f6c634300050b0032";
    private static final String GIVE_RIGHT_TO_VOTE = "giveRightToVote";
    private static final String VOTE = "vote";
    private static final String WINNINGPROPOSAL = "winningProposal";

    @Mock
    private EthereumBlockchainConfig mockBlockchainConfig;
    @Mock
    private EthereumFunction mockConstructor;
    @Mock
    private EthereumContract mockContract;

    private List<Object> mockParameters;

    private TestKit testkit;
    private ActorRef sut;

    private static ActorSystem akkaSystem;

    @BeforeAll
    public static void setupSystem() {
        akkaSystem = ActorSystem.create();
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(akkaSystem);
        akkaSystem = null;
    }

    @BeforeEach
    private void setup(){
        when(mockBlockchainConfig.getNodeUrl()).thenReturn(DEFAULTNODEURL);
        when(mockBlockchainConfig.getWallet()).thenReturn(DEFAULTWALLET);
        testkit = new TestKit(akkaSystem);
        Props providerProps = Props.create(EthereumBlockchainProvider.class, mockBlockchainConfig);
        sut =  akkaSystem.actorOf(providerProps);
    }




    @Test
    public void givenSetupNetworkThenNoErrors() throws ExecutionException, InterruptedException {
        mockParameters = new ArrayList<>();
        mockParameters.add(5);
        when(mockBlockchainConfig.getContract()).thenReturn(mockContract);
        when(mockContract.getConstructor()).thenReturn(mockConstructor);
        when(mockContract.getValue()).thenReturn(BigInteger.ZERO);
        when(mockConstructor.getInputParameters()).thenReturn(mockParameters);
        when(mockConstructor.getName()).thenReturn("deploy");
        when(mockContract.getBinary()).thenReturn(DEFAULTBINARY);
        testkit.within(Duration.ofMinutes(10), ()->{
            sut.tell(new SetupNetworkMessage(), testkit.getRef());
            SetupNetworkMessage message = testkit.expectMsgClass(SetupNetworkMessage.class);
            return null;
        });

    }

    @Test
    public void givenMultipleParallelSendTransactionGiveRightToVoterThenGetTransactionResults() throws ExecutionException, InterruptedException {
        mockParameters = new ArrayList<>();
        mockParameters.add(VOTER_ADDRESS);
        when(mockBlockchainConfig.getContract()).thenReturn(mockContract);
        when(mockContract.getAddress()).thenReturn(DEFAULTCONTRACTADDRESS);
        when(mockContract.getFunction(GIVE_RIGHT_TO_VOTE)).thenReturn(mockConstructor);
        when(mockConstructor.getInputParameters()).thenReturn(mockParameters);
        when(mockConstructor.getType()).thenReturn(FUNCTION_TYPE.TRANSACTION);
        when(mockConstructor.getName()).thenReturn(GIVE_RIGHT_TO_VOTE);
        when(mockBlockchainConfig.getConfirmCheckInterval()).thenReturn(1);
        testkit.within(Duration.ofMinutes(10), ()->{
            for (int i = 0; i < 50; i++) {
                sut.tell(new SendTransactionMessage(GIVE_RIGHT_TO_VOTE), testkit.getRef());
            }

            List<Object> results = testkit.receiveN(50);
            for (Object result : results) {
             assertEquals(TransactionResult.class, result.getClass());
            }
            return null;
        });
    }

    @Test
    public void givenSendTransactionGiveRightToVoterThenGetTransactionResult() throws ExecutionException, InterruptedException {
        mockParameters = new ArrayList<>();
        mockParameters.add(VOTER_ADDRESS);
        when(mockBlockchainConfig.getContract()).thenReturn(mockContract);
        when(mockContract.getAddress()).thenReturn(DEFAULTCONTRACTADDRESS);
        when(mockContract.getFunction(GIVE_RIGHT_TO_VOTE)).thenReturn(mockConstructor);
        when(mockConstructor.getInputParameters()).thenReturn(mockParameters);
        when(mockConstructor.getType()).thenReturn(FUNCTION_TYPE.TRANSACTION);
        when(mockConstructor.getName()).thenReturn(GIVE_RIGHT_TO_VOTE);
        when(mockBlockchainConfig.getConfirmCheckInterval()).thenReturn(1);
        testkit.within(Duration.ofMinutes(5), ()->{
            sut.tell(new SendTransactionMessage(GIVE_RIGHT_TO_VOTE), testkit.getRef());
            TransactionResult result = testkit.expectMsgClass(TransactionResult.class);
            assertNotNull(result);
            return null;
        });
    }

    @Test
    public void givenSendTransactionVoteThenGetTransactionResult() throws ExecutionException, InterruptedException {
        when(mockBlockchainConfig.getWallet()).thenReturn(VOTEWALLET);
        Props providerProps = Props.create(EthereumBlockchainProvider.class, mockBlockchainConfig);
        sut =  akkaSystem.actorOf(providerProps);
        mockParameters = new ArrayList<>();
        mockParameters.add(2);
        when(mockBlockchainConfig.getContract()).thenReturn(mockContract);
        when(mockContract.getAddress()).thenReturn(DEFAULTCONTRACTADDRESS);
        when(mockContract.getFunction(VOTE)).thenReturn(mockConstructor);
        when(mockConstructor.getInputParameters()).thenReturn(mockParameters);
        when(mockConstructor.getType()).thenReturn(FUNCTION_TYPE.TRANSACTION);
        when(mockConstructor.getName()).thenReturn(VOTE);
        when(mockBlockchainConfig.getConfirmCheckInterval()).thenReturn(1);
        testkit.within(Duration.ofMinutes(5), ()->{
            sut.tell(new SendTransactionMessage(VOTE), testkit.getRef());
            TransactionResult result = testkit.expectMsgClass(TransactionResult.class);
            assertNotNull(result);
            return null;
        });}

    @Test
    public void givenSendTransactionWithQueryThenGetTransactionResult() throws ExecutionException, InterruptedException {
        mockParameters = new ArrayList<>();
        mockParameters.add("Uint8");
        when(mockBlockchainConfig.getContract()).thenReturn(mockContract);
        when(mockContract.getAddress()).thenReturn(DEFAULTCONTRACTADDRESS);
        when(mockContract.getFunction(WINNINGPROPOSAL)).thenReturn(mockConstructor);
        when(mockConstructor.getOutputParameters()).thenReturn(mockParameters);
        when(mockConstructor.getType()).thenReturn(FUNCTION_TYPE.QUERY);
        when(mockConstructor.getName()).thenReturn(WINNINGPROPOSAL);
        testkit.within(Duration.ofMinutes(5), ()->{
            sut.tell(new SendTransactionMessage(WINNINGPROPOSAL), testkit.getRef());
            TransactionResult result = testkit.expectMsgClass(TransactionResult.class);
            assertNotNull(result);
            return null;
        }); }

}